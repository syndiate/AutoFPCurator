package org.syndiate.FPCurate;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.syndiate.FPCurate.gui.MainWindow;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;
import org.syndiate.FPCurate.gui.main.MainGUI;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;





public class Curation {
	
	
	private final File metaYAML;
	private final File curFolder;
	private final String curationId;
	
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
			new ErrorDialog(new IOException("Could not parse/write to the meta.yaml file.", e));
		}
		
	}

	
	
	
	
	
	
	
	
	
	public static String generateTagCats(String tagsStr) {
		
		boolean manualTagCats = false;
		String[] tags = tagsStr.split(";");
		ArrayList<String> tagCats = new ArrayList<>();
		TagElement[] elements = new Gson().fromJson(CommonMethods.getResource("tags.json"), TagElement[].class);
		
		
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
			
			
			System.out.println("A specific tag you entered, " + tag + " appears to be invalid.");
			manualTagCats = true;
			
		}
		
		if (manualTagCats == true) {
			return "";
		}
		
		return String.join("; ", tagCats);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean dupeCheck(String title) {
		
		
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
			
			boolean shouldEndCuration = Curation.isDupe(gameTitle, UUID);
			
			
			if (shouldEndCuration) {
				closeCuration(UUID, false);
				return true;
			} else {
				continue;
			}
			
			
			
		}
		
		return false;
		
	}
	
	
	
	
	
	
	public static boolean isDupe(String gameTitle, String UUID) {
		
		
		System.out.println("Possible dupe.");
        System.out.println("Title in question:" + gameTitle);
        	
        String initialDecision = MainGUI.input("Do you want to terminate the curation (Y/N), or do you want more information on the curation in question (More)?", requireInput).toLowerCase();
        	
        switch(initialDecision) {
        
        	
        	case "y":
        		return true;
        	case "n":
        		return false;
        	case "more": {
        		
        		
        		String curationData = Jsoup.parse(FPData.getCurationData(UUID)).selectFirst("pre").text();
        		System.out.println("Metadata of the curation:");
        		System.out.println(curationData);
        		
        		String finalDecision = MainGUI.input("Do you want to terminate the curation (Y/N)?", requireInput).toLowerCase();
        		
        		
        		if (finalDecision.equals("y")) {
        			return true;
        		}
        		if (finalDecision.equals("n")) {
        			return false;
        		}
        		
        		
        		System.out.println("Invalid option entered. Skipping.");
				return false;
        		
        		
        	}
        	
        	default: {
        		System.out.println("Invalid option entered. Skipping.");
        		return false;
        	}
        		
        			
        }
        
        
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void compressFolder(File folder, SevenZOutputFile archive, String basePath) throws IOException {
		
        String archiveName = basePath + folder.getName() + File.separator;
        SevenZArchiveEntry entry = archive.createArchiveEntry(folder, archiveName);
        
        archive.putArchiveEntry(entry);
        archive.closeArchiveEntry();
        

        for (File file : folder.listFiles()) {
        	
			if (file.isDirectory()) {
				compressFolder(file, archive, archiveName);
				continue;
			}
			

			String filePath = file.getAbsolutePath().substring(folder.getAbsolutePath().length() + 1);
			String entryName = basePath + folder.getName() + File.separator + filePath;
			entry = archive.createArchiveEntry(file, entryName);
			archive.putArchiveEntry(entry);
			

			InputStream input = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) > 0) {
				archive.write(buffer, 0, bytesRead);
			}
			

			archive.closeArchiveEntry();
			input.close();

		}
        
    }

	
	
	
	
	
	
	
	
	
	
	public static void closeCuration(String curationId, boolean endProgram) {
		
		try {
			FileUtils.deleteDirectory(new File(SettingsManager.getSetting("workingCurations") + File.separator + curationId));
			Runtime.getRuntime().exec("taskkill /f /im flashplayer_32_sa.exe & taskkill /f /im cmd.exe");
		} catch (IOException ex) {
			new ErrorDialog(ex);
			return;
		}
		
		
		if (endProgram) {
			System.exit(0);
		}
		
		MainWindow.loadWelcomeScreen();
	}
	
	
	
	
	
	public void zipCuration() {
		String outputPath = SettingsManager.getSetting("zippedCurations") + File.separator + curationId + ".7z";
		
		try {
			SevenZOutputFile archive = new SevenZOutputFile(new File(outputPath));
			compressFolder(curFolder, archive, "");
			archive.close();
		} catch (IOException e) {
			new ErrorDialog(e);
		}
		
	}
	
	
	public void closeCuration() {
		
		try {
			FileUtils.deleteDirectory(this.curFolder);
			Runtime.getRuntime().exec("taskkill /f /im flashplayer_32_sa.exe & taskkill /f /im cmd.exe");
		} catch(IOException ex) {
			new ErrorDialog(ex);
		}
		
		MainWindow.loadWelcomeScreen();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void init(File swfPath) {
		
		
		
		PrintStream customOut = new PrintStream(new OutputStream() {
		    private StringBuilder sb = new StringBuilder();

		    @Override
		    public void write(int b) throws IOException {
		        sb.append((char) b);
		        if ((char) b == '\n') {
		            JLabel label = new JLabel(sb.toString().trim());
		            label.setPreferredSize(new Dimension(100, 30));
		            MainWindow.addComponent(label);
		            sb = new StringBuilder(); // reset the StringBuilder for the next output
		        }
		    }
		});
		System.setOut(customOut);
		
		
		
		
		
		
		
		try {
			metaYAML.createNewFile();
		} catch (IOException ex) {
			new ErrorDialog(new IOException("Cannot create meta.yaml file. Perhaps AutoFPCurator doesn't have sufficient permissions to access the directory?", ex));
		}
		
		try {
			Desktop.getDesktop().open(swfPath);
		} catch (IOException e) {
			new ErrorDialog(new IOException("Cannot open SWF file. You can manually open it here: " + swfPath, e));
		}

		
		
		
		
		
		
		String launchCommand = MainGUI.input("Launch Command:", new KeyAdapter() {
			
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

				
				String[] schemes = {"http"};
				UrlValidator urlValidator = new UrlValidator(schemes);

				if (!urlValidator.isValid(text)) {
					System.out.println("Invalid launch command.");
					e.consume();
					return;
				}
				
				
				
				field.setText(text);
				return;
				
				
				
			}
		});

		
		File lcDir = new File(this.curFolder + "/content/" + launchCommand.replaceAll("//|http|https|:|\\/\\/|/g", ""));
		new File(lcDir.getParent()).mkdirs();
		
		try {
			Files.move(Paths.get(swfPath.toURI()), Paths.get(lcDir.toURI()));
		} catch (IOException e1) {
			new ErrorDialog(new IOException("Failed to move the opened file. You must move it manually.", e1));
		}
		
		
		
		
		
		
		String title = MainGUI.input("Title:", requireInput);
		System.out.println("Checking for dupes...");
		
		
		
		
		
		if (Curation.dupeCheck(title)) {
			 return;
		}
		
		
		writeMeta("Title", title);
		writeMeta("Alternate Titles", MainGUI.input("Alternate Titles:", null));
		
		
		
		
		
		String curType = MainGUI.input("Game or animation? (G/A)", null).toLowerCase();
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
				System.out.println("Invalid option entered. Defaulting to game.");
				writeMeta("Library", "arcade");
				break;
		}
		
		
		
		
		
		writeMeta("Series", MainGUI.input("Series:", null));
		writeMeta("Developer", CommonMethods.correctSeparators(MainGUI.input("Developer (separate with semicolons):", null), ";"));
		writeMeta("Publisher", CommonMethods.correctSeparators(MainGUI.input("Publisher (separate with semicolons):", null), ";"));
		
		
		
		String playMode = MainGUI.input("Play mode (separate with semicolons, s - single player, m - multiplayer):", new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (e.getKeyCode() != KeyEvent.VK_ENTER) {
					return;
				}
				
				JTextField field = ((JTextField) e.getComponent());
				String text = field.getText();
				if (text.isEmpty()) {
					e.consume();
					return;
				}
				
				text = CommonMethods.correctSeparators(text.replaceAll("s", "Single Player").replaceAll("m", "Multiplayer"), ";");
				
				for (String mode : text.split(";")) {
					
					if (!mode.equals("Single Player") && !mode.equals("Multiplayer")) {
						System.out.println("You have inputted an invalid play mode.");
						e.consume();
						return;
					}
					
				}
				
				field.setText(text);
				
				
			}
		});
		writeMeta("Play Mode", playMode);
		
		
		
		
		
		writeMeta("Release Date", MainGUI.input("Release Date:", new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
			
				if (e.getKeyCode() != KeyEvent.VK_ENTER) {
					return;
				}
			
				JTextField field = ((JTextField) e.getComponent());
				String text = field.getText();
				if (text.isEmpty()) {
					return;
				}
			
				
				if (!CommonMethods.isValidDate(text)) {
					System.out.println("Invalid date.");
					e.consume();
					return;
				}
			
			}
		}));
		writeMeta("Version", MainGUI.input("Version:", null));
		
		
		
		
		String langs = CommonMethods.correctSeparators(MainGUI.input("Languages (separate with semicolons):", null), ";");
		if (langs.equals("")) {
			System.out.println("No languages entered. Defaulting to en.");
			writeMeta("Languages", "en");
		} else {
			writeMeta("Languages", langs);
		}
		
		
		
		
		
		String tags = CommonMethods.correctSeparators(MainGUI.input("Tags (separate with semicolons):", requireInput), ";");
		writeMeta("Tags", tags);
		
		
		
		System.out.println("Generating tag categories...");
		String tagCats = Curation.generateTagCats(tags);
		
		if (tagCats.equals("")) {
			writeMeta("Tag Categories", MainGUI.input("Tag Categories:", requireInput));
		} else {
			writeMeta("Tag Categories", tagCats);
		}
		
		
		
		
		
		
		
		String src = MainGUI.input("Source:", requireInput);
		if (src.equals("")) {
			System.out.println("Warning: No source entered. If this was a mistake, please fix it in meta.yaml.");
		} else {
			
			String[] schemes = {"http"};
			UrlValidator urlValidator = new UrlValidator(schemes);

			if (!urlValidator.isValid(src)) {
				System.out.println("Warning: Source is an invalid URL. If this was a mistake, please fix it in meta.yaml.");
			}
			
		}
		writeMeta("Source", src);
		writeMeta("Platform", "Flash");
		
		
		
		
		
		
		
		
		
		
		String status = MainGUI.input("Status (separate with semicolons, p - playable, pa - partial, h - hacked):", new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (e.getKeyCode() != KeyEvent.VK_ENTER) {
					return;
				}
				
				JTextField field = ((JTextField) e.getComponent());
				String text = field.getText();
				if (text.isEmpty()) {
					e.consume();
					return;
				}
				
				text = CommonMethods.correctSeparators(text.replaceAll("pa", "Partial").replaceAll("h", "Hacked").replaceAll("p", "Playable"), ";");
				
				for (String status : text.split(";")) {
					
					if (!status.equals("Partial") && !status.equals("Hacked") && !status.equals("Playable")) {
						System.out.println("You have inputted an invalid status..");
						e.consume();
						return;
					}
					
				}
				
				field.setText(text);
				
				
			}
		});
		
		if (status.equals("")) {
			System.out.println("No status entered. Defaulting to playable.");
			writeMeta("Status", "Playable");
		} else {
			writeMeta("Status", status);
		}
		
		writeMeta("Application Path", "FPSoftware\\Flash\\flashplayer_32_sa.exe");
		writeMeta("Launch Command", launchCommand);
		
		
		
		
		
		
		
		
		writeMeta("Game Notes", MainGUI.input("Game Notes:", null));
		writeMeta("Original Description", MainGUI.input("Original Description:", null));
		writeMeta("Curation Notes", "");
		writeMeta("Mount Parameters", "");
		writeMeta("Additional Applications", "{}");
		
		
		
		
		String ssConfirm = MainGUI.input("Enter Yes/Y/[blank] to take a screenshot (PLEASE HAVE THE GAME OPEN!).", null);
		BufferedImage ss = null;
		switch (ssConfirm.toLowerCase()) {
		
			case "yes":
			case "y":
			case "": {
				
				ss = Screenshot.takeScreenshot();
				try {
					ImageIO.write(ss, "png", new File(this.curFolder.getAbsolutePath() + "/ss.png"));
				} catch (IOException e) {
					new ErrorDialog(e);
				}
				break;
				
			}
			default: {
				System.out.println("Skipping screenshot. Be sure to manually add a screenshot to the curation.");
			}
				
		}
		
		
		
		MainGUI.input("Press enter to zip and close the curation.", null);
		System.out.println("Zipping curation...");
		
		zipCuration();
		
		System.out.println("Zipped curation.");
		System.out.println("Closing curation...");
		closeCuration();

		
	}








	
	
	
	
	
	
	
	
	
	public File getMetaYaml() {
		return this.metaYAML;
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