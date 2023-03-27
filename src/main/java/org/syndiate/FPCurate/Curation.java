package org.syndiate.FPCurate;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.syndiate.FPCurate.gui.MainWindow;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;




public class Curation {
	
	
	private File metaYAML;
	private File curFolder;
	private String curationId;
	
	
	public Curation() {
		
		this.curationId = UUID.randomUUID().toString();
		
		this.curFolder = new File(SettingsManager.getSetting("workingCurations") + "/" + curationId);
		this.curFolder.mkdirs();
		this.metaYAML = new File(curFolder + "/meta.yaml");
		
		
		
		
	}
	
	
	
	
	
	
	
	public void init(File swfPath) {
		
//		System.out.println("test");
		
		
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

		
		
		
		String launchCommand = input("Launch Command:");
		if (!launchCommand.startsWith("http://") && !launchCommand.startsWith("https://")) {
	        launchCommand = "http://" + launchCommand;
	    }
		try {
	        new URL(launchCommand);
	    } catch (MalformedURLException e) {
	        System.out.println("Invalid launch command. Please fix in meta.yaml.");
	    }
		
		
		
		
		File lcDir = new File(this.curFolder + "/" + launchCommand.replaceAll("//|http|https|:|\\/\\/|/g", ""));
		new File(lcDir.getParent()).mkdirs();
		
		
		
		try {
			Files.move(Paths.get(swfPath.toURI()), Paths.get(lcDir.toURI()));
		} catch (IOException e1) {
			new ErrorDialog(new IOException("Failed to move the opened file. You must move it manually.", e1));
		}
		
		
		
		
		
		writeMeta("Title", input("Title:"));
		writeMeta("Alternate Titles", input("Alternate Titles:"));
		
		
		String curType = input("Game or animation? (G/A)").toLowerCase();
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
		
		
		writeMeta("Series", input("Series:"));
		writeMeta("Developer", input("Developer (separate with semicolons):"));
		writeMeta("Publisher", input("Publisher (separate with semicolons):"));
		
		String playMode = input("Play mode (s - single player, c - cooperative, m - multiplayer):");
		switch (playMode.toLowerCase()) {
			case "single player":
			case "s":
				writeMeta("Play Mode", "Single Player");
				break;
			case "cooperative":
			case "c":
				writeMeta("Play Mode", "Cooperative");
				break;
			case "multiplayer":
			case "m":
				writeMeta("Play Mode", "Multiplayer");
				break;
			default:
				System.out.println("Invalid option entered. Defaulting to single player.");
				writeMeta("Play Mode", "Single Player");
				break;
		}
		
		
		writeMeta("Release Date", input("Release Date:"));
		writeMeta("Version", input("Version:"));
		
		
		String langs = input("Languages (separate with semicolons:");
		if (langs.equals("")) {
			System.out.println("No languages entered. Defaulting to en.");
			writeMeta("Languages", "en");
		} else {
			writeMeta("Languages", langs);
		}
		
		
		
		String extreme = input("Extreme (Y/N):");
		switch (extreme.toLowerCase()) {
			case "yes":
			case "y":
				writeMeta("Extreme", "Yes");
				break;
			case "no":
			case "n":
				writeMeta("Extreme", "No");
				break;
			default:
				System.out.println("Invalid option entered. Defaulting to no.");
				writeMeta("Extreme", "No");
				break;
		}
		
		
		
		String tags = input("Tags (separate with semicolon):");
		if (tags.equals("")) {
			System.out.println("Warning: No tags entered. If this was a mistake, please fix it directly in the meta.yaml file.");
		}
		writeMeta("Tags", tags);
		
		
		
		String src = input("Source:");
		if (src.equals("")) {
			System.out.println("Warning: No source entered. If this was a mistake, please fix it directly in the meta.yaml file.");
		}
		writeMeta("Source", src);
		writeMeta("Platform", "Flash");
		
		
		
		String status = input("Status (separate with semicolons, p - playable, pa - partial, h - hacked):").replaceAll("pa",  "Partial").replaceAll("h", "Hacked").replaceAll("p", "Playable");
		if (status.equals("")) {
			System.out.println("No status entered. Defaulting to playable.");
			writeMeta("Status", "Playable");
		} else {
			writeMeta("Status", status);
		}
		writeMeta("Application Path", "FPSoftware\\Flash\\flashplayer_32_sa.exe");
		writeMeta("Launch Command", launchCommand);
		
		
		
		
		
		
		writeMeta("Game Notes", input("Game Notes:"));
		writeMeta("Original Description", input("Original Description:"));
		writeMeta("Curation Notes", "");
		writeMeta("Mount Parameters", "");
		writeMeta("Additional Applications", "{}");
		
		
		String ssConfirm = input("Enter Yes/Y/[blank] to take a screenshot (PLEASE HAVE THE GAME OPEN!).");
		BufferedImage ss = null;
		switch (ssConfirm.toLowerCase()) {
			case "yes":
			case "y":
			case "":
				ss = Screenshot.takeScreenshot();
				try {
					ImageIO.write(ss, "png", new File(this.curFolder.getAbsolutePath() + "/ss.png"));
				} catch (IOException e) {
					new ErrorDialog(e);
				}
				break;
			default:
				
		}
		
		
		input("Press enter to zip the curation.");
		try {
			zipCuration();
		} catch (IOException e) {
			new ErrorDialog(e);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	public void writeMeta(String key, String value) {
		
		try {
			
			Yaml yaml = new Yaml();
			FileReader reader = new FileReader(this.metaYAML);
			Map<String, String> meta = yaml.load(reader);
			
			
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

	
	
	
	
	
	
	
	
	private static String input(String prompt) {
		
		CompletableFuture<String> future = new CompletableFuture<>();

		
	    SwingUtilities.invokeLater(() -> {

	        
	    	JPanel row = new JPanel();
	    	row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
	    	row.setBackground(new Color(255, 255, 255));
	    	row.setAlignmentX(Component.LEFT_ALIGNMENT);
	    	
	    	
	        JLabel label = new JLabel(prompt);
	        label.setPreferredSize(new Dimension(100, 20));

	        JTextField field = new JTextField();
	        field.addActionListener((ActionEvent e) -> {
	            future.complete(field.getText());
	        });
	        
	        Dimension textFieldSize = new Dimension(235, 25);
	        field.setMaximumSize(textFieldSize);
	        field.setPreferredSize(textFieldSize);
	        
	        
	        row.add(label);
	        row.add(field);
	        row.add(Box.createRigidArea(new Dimension(0, 10)));
	        MainWindow.mainPanel.add(row);
	        field.requestFocusInWindow();
	        
	        

	        MainWindow.mainPanel.revalidate();
	        MainWindow.mainPanel.repaint();
	    });

	    
	    try {
	        return future.get();
	    } catch (InterruptedException | ExecutionException e) {
	        new ErrorDialog(e);
	    }
	    

	    return "";
		
	}
	
	
	
	
	
	
	public static boolean isDupe(String gameTitle, String UUID) {
		
		
		System.out.println("Possible dupe.");
        System.out.println("Title in question:" + gameTitle);
        	
        String initialDecision = input("Do you want to terminate the curation (Y/N), or do you want more information on the curation in question (More)?").toLowerCase();
        	
        switch(initialDecision) {
        
        	
        	case "y":
        		return true;
        	case "n":
        		return false;
        	case "more": {
        		
        		
        		String curationData = Jsoup.parse(FPData.getCurationData(UUID)).selectFirst("pre").text();
        		System.out.println("Metadata of the curation:");
        		System.out.print(curationData);
        		
        		String finalDecision = input("Do you want to terminate the curation (Y/N)?").toLowerCase();
        		
        		
        		if (finalDecision.equals("y")) return true;
        		if (finalDecision.equals("n")) return false;
        		
        		
        		System.out.println("Invalid option entered. Skipping.");
				return false;
        		
        		
        	}
        	
        	default: {
        		System.out.println("Invalid option entered. Skipping.");
        		return false;
        	}
        		
        			
        }
        
        
	}
	
	public static void dupeCheck(String title) {
		
		
		String searchHTML = FPData.getSearchData(title);
		if (searchHTML == "") return;
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
	        } else {
	        	continue;
	        }
	        
			
			
		}
		
	}
	
	
	
	
	
	
	public void zipCuration() throws IOException {

        String outputPath = SettingsManager.getSetting("zippedCurations") + "/" + curationId + ".7z";

        XZCompressorOutputStream xzOutputStream = new XZCompressorOutputStream(new FileOutputStream(outputPath));
        SevenZOutputFile sevenZOutput = new SevenZOutputFile(curFolder);

        // get all the files and subdirectories in the folder
        Path folder = Paths.get(curFolder.toString());
        Files.walk(folder)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        // create an input stream for the file
                        FileInputStream inputStream = new FileInputStream(file.toFile());

                        // add the file to the compressed archive
                        String fileName = folder.relativize(file).toString();
                        sevenZOutput.putArchiveEntry(sevenZOutput.createArchiveEntry(file.toFile(), fileName));
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = inputStream.read(buffer)) != -1) {
                            xzOutputStream.write(buffer, 0, len);
                        }
                        sevenZOutput.closeArchiveEntry();

                        // close the input stream
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        sevenZOutput.close();
        xzOutputStream.close();
        
        /*
        SevenZOutputFile sevenZOutput = new SevenZOutputFile(new File(outputPath));


        File[] files = curFolder.listFiles();

        for (File file : files) {
        	
        	FileInputStream inputStream = new FileInputStream(file);
            String fileName = file.getName();
            IOUtils.copy(inputStream, sevenZOutput.create);
            sevenZOutput.putArchiveEntry(sevenZOutput.createArchiveEntry(file, fileName));
            sevenZOutput.closeArchiveEntry();
            
        }

        sevenZOutput.close();*/
	}
	
	
	
	
	
	
	
	public static void closeCuration(String curationId, boolean endProgram) {
		
		try {
			FileUtils.deleteDirectory(new File("fpCurRoot" + curationId));
			Runtime.getRuntime().exec("taskkill /f /im flashplayer_32_sa.exe & taskkill /f /im cmd.exe");
		} catch (IOException ex) {
			System.out.println("An unknown error occurred.");
			System.exit(0);
		}
		
		
		if (endProgram) {
			System.exit(0);
		}
	}
	
	
	
	
	/*
	public static void main(String[] args) throws IOException {
		
		
		
		
		
	}*/
	
	
	
	
	public File getMetaYaml() {
		return this.metaYAML;
	}

}
