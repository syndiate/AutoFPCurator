package org.syndiate.FPCurate;

import java.awt.Desktop;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;
import org.syndiate.FPCurate.gui.cropper.CropperManager;
import org.syndiate.FPCurate.gui.main.MainGUI;
import org.syndiate.FPCurate.gui.main.MainWindow;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;




public class Curation {
	

	private File metaYAML;
	private File curFolder;
	private File swfPath;
	private String curationId;
	
	private static final String curationDataDirPrefix = "curation_data/";
	public static final String[] lcProtocols = {"http"};
	
	private static final Map<String, String> errorStrs = I18N.getStrings("exceptions/curation");
	private static final Map<String, String> curationStrs = I18N.getStrings("curation");
	
	
	private static final KeyAdapter requireInput = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER && ((JTextField) e.getComponent()).getText().isEmpty()) {
				e.consume();
			}
		}
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Curation() {
		
		this.curationId = UUID.randomUUID().toString();
		
		this.curFolder = new File(SettingsManager.getSetting("workingCurations") + File.separator + curationId);
		this.curFolder.mkdirs();
		this.metaYAML = new File(curFolder + "/meta.yaml");

	}
	
	
	
	// support for folders with swf files inside them
	public Curation(File folder) {

		if (!folder.isDirectory()) {
			return;
		}

		Path dir = folder.toPath();
		try {

			// iterate through all swf files in the folder
			Files.walk(dir).forEach(path -> {
				File curFile = path.toFile();
				if (!CommonMethods.getFileExtension(curFile).equals("swf")) {
					return;
				}
				new Curation().init(curFile, false);
			});
			// finish operation
			MainWindow.loadWelcomeScreen();

		} catch (IOException e) {
			new ErrorDialog(e);
		}

	}
	
	
	
	
	
	
	

	
	
	
	
	
	
	public void writeMeta(String key, Object value) {
		
		try {
			
			final Yaml yaml = new Yaml();
			FileReader reader = new FileReader(this.metaYAML);
			Map<String, Object> meta = yaml.load(reader);
			
			
			if (meta == null) {
				meta = new HashMap<>();
			}
			if (value.equals("")) {
				value = null;
			}
			meta.put(key, value);
			

			FileWriter writer = new FileWriter(this.metaYAML);
			
			DumperOptions options = new DumperOptions();
	        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
	        
			new Yaml(options).dump(meta, writer);

			writer.close();
			reader.close();
			
			
		} catch (IOException e) {
			new ErrorDialog(new IOException(errorStrs.get("metaYamlWrite"), e));
		}
		
	}
	
	
	
	
	public Optional<Object> readMeta(String key) {
		try {
			
			final Yaml yaml = new Yaml();
			FileReader reader = new FileReader(this.metaYAML);
			Map<String, Object> meta = yaml.load(reader);
			
			if (meta == null) {
				meta = new HashMap<>();
			}
			Object value = meta.get(key);
			// account for fields whose values are literally null
			if (value == null && meta.containsKey(key)) {
				value = "null";
			}
			
			reader.close();
			return Optional.ofNullable(value);
			
		} catch (IOException e) {
			new ErrorDialog(e);
		}
		return null;
	}

	
	
	
	
	
	
	
	
	public static String generateTagCats(String tagsStr) {
		
		boolean manualTagCats = false;
		String[] tags = tagsStr.split(";");
		ArrayList<String> tagCats = new ArrayList<>();
		TagElement[] elements = new Gson().fromJson(CommonMethods.getResource(curationDataDirPrefix + "tags.json"), TagElement[].class);
		
		
		for (String tag : tags) {
			
			int oldLength = tagCats.size();

			
			for (TagElement element : elements) {
				if (!element.getAliases().contains(tag.trim())) {
					continue;
				}
				tagCats.add(element.getCategory());
				break;
			}
			if (oldLength != tagCats.size()) {
				continue;
			}
			
			
			System.out.println(curationStrs.get("invalidTagPt1") + tag + curationStrs.get("invalidTagPt2"));
			manualTagCats = true;
			
		}
		
		if (manualTagCats == true) {
			return "";
		}
		
		return String.join("; ", tagCats);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean checkPotentialDupes(String title) {
		
		
		String searchHTML = FPData.getSearchData(title);
		if (searchHTML == "") {
			return false;
		}
		Document searchData = Jsoup.parse(searchHTML);
		
		
		for (Element game : searchData.select("div.game")) {
			
			
			String gameTitle = game.selectFirst("a").text().replaceAll("\\[.*?\\]", "");
			String UUID = game.selectFirst("div.game-details").attr("data-id");
			LevenshteinDistance ld = new LevenshteinDistance();
			
			int distance = ld.apply(title, gameTitle);
			int maxLength = Math.max(title.length(), gameTitle.length());
			
			
			double similarityPercentage = ((double) (maxLength - distance)) / maxLength * 100.0;
			if (similarityPercentage < 0.85) {
				continue;
			}
			
			boolean shouldEndCuration = Curation.dupePrompt(gameTitle, UUID);
			
			
			if (shouldEndCuration) {
				closeCuration(UUID, false);
				return true;
			}
			
		}
		
		return false;
		
	}
	
	
	
	
	
	
	public static boolean dupePrompt(String gameTitle, String UUID) {
		
		
		System.out.println(curationStrs.get("possibleDupe"));
        System.out.println(curationStrs.get("dupeTitleInQuestion") + gameTitle);
        	
        String initialDecision = MainGUI.input(curationStrs.get("initialDupePrompt"), requireInput).toLowerCase();
        	
        switch(initialDecision) {
        
        	
        	case "y":
        		return true;
        	case "n":
        		return false;
        	case "more": {
        		
        		
        		String curationData = Jsoup.parse(FPData.getCurationData(UUID)).selectFirst("pre").text();
        		System.out.println(curationStrs.get("dupeMetadata"));
        		System.out.println(curationData);
        		
        		String finalDecision = MainGUI.input(curationStrs.get("finalDupePrompt"), requireInput).toLowerCase();
        		
        		
        		if (finalDecision.equals("y")) {
        			return true;
        		}
        		if (finalDecision.equals("n")) {
        			return false;
        		}
        		
        		
        		System.out.println(curationStrs.get("invalidOption") + curationStrs.get("skipping"));
				return false;
        		
        		
        	}
        	
        	default: {
        		System.out.println(curationStrs.get("invalidOption") + curationStrs.get("skipping"));
        		return false;
        	}
        		
        			
        }
        
        
	}
	
	
	

	
	
	
	
	
	
	
	
	public static void closeCuration(String curationId, boolean endProgram) {
		
		try {
			FileUtils.deleteDirectory(new File(SettingsManager.getSetting("workingCurations") + File.separator + curationId));
		} catch (IOException ex) {
			new ErrorDialog(ex);
			return;
		}
		
		if (endProgram) {
			System.exit(0);
		}
		
	}
	
	public void closeCuration(boolean shouldCloseCurationView) {
		
		try {
			FileUtils.deleteDirectory(this.curFolder);
		} catch(IOException ex) {
			new ErrorDialog(ex);
		}
		
		if (shouldCloseCurationView) {
			MainWindow.loadWelcomeScreen();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void init(File swfPath, boolean shouldCloseCurationView) {
		
		
		this.swfPath = swfPath;
		
		
		// set system.out.println to this function
		PrintStream customOut = new PrintStream(new OutputStream() {
		    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

		    @Override
		    public void write(int b) throws IOException {
		        if (b == '\n') {
		            String output = baos.toString("UTF-8").trim();
		            JLabel label = new JLabel(output);
		            MainWindow.addComponent(label);
		            baos.reset(); // reset the ByteArrayOutputStream for the next output
		        } else {
		            baos.write(b);
		        }
		    }
		});
		System.setOut(customOut);
		
		
		
		
		try {
			metaYAML.createNewFile();
		} catch (IOException ex) {
			new ErrorDialog(new IOException(errorStrs.get("metaYamlAccess"), ex));
		}

		
		
		
		String launchCommand = "";
		if (SettingsManager.getSetting("lcDirDefOff").equals("true")) {
			
			launchCommand = MainGUI.input(curationStrs.get("lcPrompt"), new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {

					if (e.getKeyCode() != KeyEvent.VK_ENTER) {
						return;
					}

					JTextField field = ((JTextField) e.getComponent());
					String text = field.getText();
					if (text.isEmpty()) {
						e.consume();
						return;
					}

					if (!text.startsWith("http://") && !text.startsWith("https://")) {
						text = "http://" + text;
					}

					String[] schemes = { "http" };
					UrlValidator urlValidator = new UrlValidator(schemes);

					if (!urlValidator.isValid(text)) {
						System.out.println(curationStrs.get("invalidLc"));
						e.consume();
						return;
					}

					field.setText(text);
					return;

				}
			});
			
		} else {
			launchCommand = SettingsManager.getSetting("lcDirDefault") + this.swfPath.getName();
		}
		
		

		
		
		
		File lcDir = new File(this.curFolder + "/content/" + launchCommand.replaceAll("//|http|https|:|\\/\\/|/g", ""));
		new File(lcDir.getParent()).mkdirs();
		
		
		
		
		try {
			System.out.println(curationStrs.get("moveSWF"));
			Files.move(Paths.get(this.swfPath.toURI()), Paths.get(lcDir.toURI()));
			this.swfPath = lcDir;
		} catch (IOException e1) {
			new ErrorDialog(new IOException(errorStrs.get("moveSWF"), e1));
		}
		
		
		try {
			System.out.println(curationStrs.get("openSWF"));
			Desktop.getDesktop().open(this.swfPath);
		} catch (IOException e) {
			new ErrorDialog(new IOException(errorStrs.get("openSWF") + this.swfPath, e));
		}
		
		
		
		
		String logoSS = MainGUI.input(curationStrs.get("logoPrompt"), requireInput);
		switch (logoSS.toLowerCase()) {
			case "y":
				System.out.println(curationStrs.get("openCropper"));
				new CropperManager(Screenshot.takeScreenshot(), new File(this.curFolder.getAbsolutePath() + "/logo.png"));
				break;
			default:
				System.out.println(curationStrs.get("noSS"));
				break;
		}
		
		
		
		
		
		
		
		
		
		
		
		String title = MainGUI.input(curationStrs.get("titlePrompt"), requireInput);
		System.out.println(curationStrs.get("checkingDupes"));
		if (Curation.checkPotentialDupes(title)) {
			 return;
		}
		
		
		
		
		writeMeta("Title", title);
		writeMeta("Alternate Titles", MainGUI.input(curationStrs.get("alternateTitlePrompt"), null));
		
		
		
		
		if (readMeta("Library").orElse(null) == null) {

			String curType = "";
			
			if (SettingsManager.getSetting("libraryDefOff").equals("true")) {
				curType = MainGUI.input(curationStrs.get("gaPrompt"), null).toLowerCase();
			} else {
				curType = SettingsManager.getSetting("libraryDefault");
			}
			
			switch (curType.toLowerCase()) {
				case "arcade":
				case "g":
					writeMeta("Library", "arcade");
					break;
				case "theatre":
				case "a":
					writeMeta("Library", "theatre");
					break;
				default:
					System.out.println(curationStrs.get("invalidOption") + curationStrs.get("defaultingTo") + "game.");
					writeMeta("Library", "arcade");
					break;
			}

		} else {
			System.out.println("Library" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		
		
		
		
		
		
		
		
		
		
		
		if (readMeta("Developer").orElse(null) == null) {
			String dev = SettingsManager.getSetting("devDefOff").equals("true") ? CommonMethods.correctSeparators(MainGUI.input(curationStrs.get("devPrompt"), null), ";")
							: SettingsManager.getSetting("devDefault");
			writeMeta("Developer", dev);
		} else {
			System.out.println("Developer" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		
		if (readMeta("Publisher").orElse(null) == null) {
			String publisher = SettingsManager.getSetting("publishDefOff").equals("true") ? CommonMethods.correctSeparators(MainGUI.input(curationStrs.get("publishPrompt"), null), ";")
							: SettingsManager.getSetting("publishDefault");
			writeMeta("Publisher", publisher);
		} else {
			System.out.println("Publisher"  + curationStrs.get("fieldAlreadyPresent"));
		}
		
		
		if (readMeta("Series").orElse(null) == null) {
			writeMeta("Series", MainGUI.input("Series:", null));
		} else {
			System.out.println("Series" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		
		
		
		
		if (readMeta("Play Mode").orElse(null) == null) {

			String playMode = "";
			if (SettingsManager.getSetting("modeDefOff").equals("true")) {

				playMode = MainGUI.input(curationStrs.get("playModePrompt"),
						new KeyAdapter() {
					
							@Override
							public void keyPressed(KeyEvent e) {

								if (e.getKeyCode() != KeyEvent.VK_ENTER) {
									return;
								}

								JTextField field = ((JTextField) e.getComponent());
								String text = field.getText();
								if (text.isEmpty()) {
									e.consume();
									return;
								}

								text = CommonMethods.correctSeparators(text.replaceAll("s", "Single Player")
										.replaceAll("m", "Multiplayer").replaceAll("c", "Cooperative"), ";");

								for (String mode : text.split(";")) {

									mode = mode.trim();
									if (!mode.equals("Single Player") && !mode.equals("Multiplayer")
											&& !mode.equals("Cooperative")) {
										System.out.println(curationStrs.get("invalidPlayMode"));
										e.consume();
										return;
									}

								}

								field.setText(text);

							}
						});

			} else {
				playMode = SettingsManager.getSetting("modeDefault").replaceAll("s", "Single Player")
						.replaceAll("m", "Multiplayer").replaceAll("c", "Cooperative");
			}
			writeMeta("Play Mode", playMode);

		} else {
			System.out.println("Play Mode" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		
		
		
		
		
		
		
		
		if (readMeta("Release Date").orElse(null) == null) {
			String releaseDate = "";
			if (SettingsManager.getSetting("releaseDefOff").equals("true")) {

				releaseDate = MainGUI.input(curationStrs.get("releasePrompt"), new KeyAdapter() {

					@Override
					public void keyPressed(KeyEvent e) {

						if (e.getKeyCode() != KeyEvent.VK_ENTER) {
							return;
						}

						JTextField field = ((JTextField) e.getComponent());
						String text = field.getText();
						if (text.isEmpty()) {
							return;
						}

						if (!CommonMethods.isValidDate(text)) {
							System.out.println(curationStrs.get("invalidDate"));
							e.consume();
							return;
						}

					}
				});
			} else {
				releaseDate = SettingsManager.getSetting("releaseDefault");
			}
			writeMeta("Release Date", releaseDate);
		} else {
			System.out.println("Release Date" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		
		
		if (readMeta("Version").orElse(null) == null) {
			String version = SettingsManager.getSetting("verDefOff").equals("true") ? MainGUI.input(curationStrs.get("verPrompt"), null) : SettingsManager.getSetting("verDefault");
			writeMeta("Version", version);
		} else {
			System.out.println("Version" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		
		
		
		
		
		
		
//		String langs = CommonMethods.correctSeparators(MainGUI.input("Languages (separate with semicolons):", null), ";");
		
		if (readMeta("Languages").orElse(null) == null) {
			
			String langs = "";
			if (SettingsManager.getSetting("langDefOff").equals("true")) {

				langs = MainGUI.input(curationStrs.get("languagePrompt"), new KeyAdapter() {
					@SuppressWarnings("unchecked")
					@Override
					public void keyPressed(KeyEvent e) {

						if (e.getKeyCode() != KeyEvent.VK_ENTER) {
							return;
						}

						JTextField field = ((JTextField) e.getComponent());
						String text = field.getText();
						if (text.isEmpty()) {
							e.consume();
							return;
						}

						text = CommonMethods.correctSeparators(text, ";");
						Map<String, String> langs = (Map<String, String>) CommonMethods
								.parseJSONStr(CommonMethods.getResource(curationDataDirPrefix + "langs.json"));

						for (String lang : text.split(";")) {
							lang = lang.trim();
							if (!langs.containsKey(lang)) {
								System.out.println(lang + curationStrs.get("invalidLanguage"));
								e.consume();
								return;
							}
						}

						field.setText(text);

					}
				});

			} else {
				langs = SettingsManager.getSetting("langDefault");
			}

			if (langs.equals("")) {
				System.out.println(curationStrs.get("noLanguage"));
				writeMeta("Languages", "en");
			} else {
				writeMeta("Languages", langs);
			}

		} else {
			System.out.println("Languages" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		
		
		
		
		
		String tags = SettingsManager.getSetting("tagsDefOff").equals("true") ? CommonMethods.correctSeparators(
				MainGUI.input(curationStrs.get("tagPrompt"), requireInput), ";"
			)
			: SettingsManager.getSetting("tagsDefault");
		
		writeMeta("Tags", tags);
		
		

		System.out.println(curationStrs.get("generatingTagCats"));
		String tagCats = Curation.generateTagCats(tags);

		if (tagCats.equals("")) {
			System.out.println(curationStrs.get("noTagCats"));
			writeMeta("Tag Categories", MainGUI.input(curationStrs.get("tagCatPrompt"), requireInput));
		} else {
			writeMeta("Tag Categories", tagCats);
		}
		
		
		
		
		if (readMeta("Source").orElse(null) == null) {
			
			String src = "";
			if (SettingsManager.getSetting("srcDefOff").equals("true")) {

				src = MainGUI.input(curationStrs.get("srcPrompt"), requireInput);
				if (src.equals("")) {
					System.out
							.println();
				} else {

					String[] schemes = { "http", "https" };
					UrlValidator urlValidator = new UrlValidator(schemes);

					if (!urlValidator.isValid(src)) {
						System.out.println(
								curationStrs.get("nonURLSrc"));
					}

				}

			} else {
				src = SettingsManager.getSetting("srcDefault");
			}
			writeMeta("Source", src);
			
		} else {
			System.out.println("Source" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		writeMeta("Platform", "Flash");
		
		
		
		
		
		
		
		
		if (readMeta("Status").orElse(null) == null) {
			
			String status = "";
			if (SettingsManager.getSetting("statusDefOff").equals("true")) {

				status = MainGUI.input(curationStrs.get("statusPrompt"),
						new KeyAdapter() {
							@Override
							public void keyPressed(KeyEvent e) {

								if (e.getKeyCode() != KeyEvent.VK_ENTER) {
									return;
								}

								JTextField field = ((JTextField) e.getComponent());
								String text = field.getText();
								if (text.isEmpty()) {
									e.consume();
									return;
								}

								text = CommonMethods.correctSeparators(text.replaceAll("pa", "Partial")
										.replaceAll("h", "Hacked").replaceAll("p", "Playable"), ";");

								for (String status : text.split(";")) {

									status = status.trim();
									if (!status.equals("Partial") && !status.equals("Hacked")
											&& !status.equals("Playable")) {
										System.out.println(curationStrs.get("invalidStatus"));
										e.consume();
										return;
									}

								}

								field.setText(text);

							}
						});

			} else {
				status = SettingsManager.getSetting("statusDefault").replaceAll("pa", "Partial")
						.replaceAll("h", "Hacked").replaceAll("p", "Playable");
			}
			

			if (status.equals("")) {
				System.out.println(curationStrs.get("noStatus"));
				writeMeta("Status", "Playable");
			} else {
				writeMeta("Status", status);
			}
			
		} else {
			System.out.println("Status" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		
		
		
		
		writeMeta("Application Path", "FPSoftware\\Flash\\flashplayer_32_sa.exe");
		writeMeta("Launch Command", launchCommand);
		
		
		
		
		
		
		if (readMeta("Game Notes").orElse(null) == null) {
			String gameNotes = SettingsManager.getSetting("gameNotesDefOff").equals("true") ? MainGUI.input(curationStrs.get("gameNotesPrompt"), null) : SettingsManager.getSetting("gameNotesDefault");
			writeMeta("Game Notes", gameNotes);
		} else {
			System.out.println("Game Notes" + curationStrs.get("fieldAlreadyPresent"));
		}
		
		if (readMeta("Original Description").orElse(null) == null) {
			String desc = SettingsManager.getSetting("descDefOff").equals("true") ? MainGUI.input(curationStrs.get("descPrompt"), null) : SettingsManager.getSetting("descDefault");
			writeMeta("Original Description", desc);
		} else {
			System.out.println("Original Description" + curationStrs.get("fieldAlreadyPresent"));
		}

		writeMeta("Curation Notes", "");
		writeMeta("Mount Parameters", "");
		writeMeta("Additional Applications", "{}");
		
		
		
		
		String ssConfirm = SettingsManager.getSetting("settingsDefOff").equals("true") ? 
				MainGUI.input(curationStrs.get("ssPrompt"), null)
				: SettingsManager.getSetting("ssDefault");
		
		
		
		
		switch (ssConfirm.toLowerCase()) {
		
			case "yes":
			case "y":
			case "": {
				try {
					ImageIO.write(Screenshot.takeScreenshot(), "png", new File(this.curFolder.getAbsolutePath() + "/ss.png"));
				} catch (IOException e) {
					new ErrorDialog(e);
				}
				break;
				
			}
			default: {
				System.out.println(curationStrs.get("skipSS"));
			}
				
		}
		
		
		if (SettingsManager.getSetting("promptZipDefOff").equals("true")) {
			MainGUI.input(curationStrs.get("zipPrompt"), null);
		}
		System.out.println(curationStrs.get("currentlyZipping"));
		
		
		String out = SettingsManager.getSetting("zippedCurations") + "/" + this.curationId + ".7z";
//		zipCuration();
		CommonMethods.runExecutable("programs/7za.exe", "a \"" + out + "\" \"" + curFolder.getAbsolutePath() + "\"/*", true, true);
		
		
		System.out.println(curationStrs.get("doneZipping"));
		System.out.println(curationStrs.get("closingCuration"));
		closeCuration(shouldCloseCurationView);

		
	}








	
	
	
	
	
	
	
	public String getCurationId() {
		return this.curationId;
	}
	
	public File getMetaYaml() {
		return this.metaYAML;
	}
	
	public File getCurFolder() {
		return this.curFolder;
	}
	
	public File getSWF() {
		return this.swfPath;
	}

}






//record TagElement(String[] aliases, String category) {}
// i would have used records, but due to all fields in a record being final, well...
class TagElement {
	private ArrayList<String> aliases;
	private String category;
	
	public TagElement(ArrayList<String> aliases, String category) {
		this.aliases = aliases;
		this.category = category;
	}
	
	public ArrayList<String> getAliases() {
		return aliases;
	}
	public String getCategory() {
		return category;
	}
}