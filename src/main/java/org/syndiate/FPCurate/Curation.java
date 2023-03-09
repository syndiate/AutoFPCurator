package org.syndiate.FPCurate;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonObject;

public class Curation {
	
	private static final String fpCurRoot = "C:/Users/syndi/Downloads/epic flashpoint games/Flashpoint Core 11/Curations/Working/";
	
	
	@SuppressWarnings("resource")
	private static String input(String prompt) {
		
		Scanner sc = new Scanner(System.in);
		System.out.print(prompt);
		
		String input = sc.nextLine();
		return input;
		
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
	
	
	
	
	
	
	
	
	
	

	
	public static void closeCuration(String curationId, boolean endProgram) {
		
		try {
			FileUtils.deleteDirectory(new File(fpCurRoot + curationId));
			Runtime.getRuntime().exec("taskkill /f /im flashplayer_32_sa.exe & taskkill /f /im cmd.exe");
		} catch (IOException ex) {
			System.out.println("An unknown error occurred.");
			System.exit(0);
		}
		
		
		if (endProgram) {
			System.exit(0);
		}
	}
	
	
	public Curation(String swfPath) throws IOException {
		
		
		String curationId = new File(fpCurRoot).list()[0];
		
		Desktop.getDesktop().open(new File(swfPath));
		
		JsonObject metadata = new JsonObject();
		
		String title = input("Title:").replaceAll(" ", "+");
		
		if (title == "CURATE_TERMINATE") {
			closeCuration(curationId, true);
		}
		
		metadata.addProperty("series", input("Series:"));
		metadata.addProperty("alt", input("Alternate title:"));
		metadata.addProperty("publisher", input("Publisher:"));
		metadata.addProperty("dev", input("Developer:"));
		metadata.addProperty("release", input("Release date:"));
		metadata.addProperty("ver", input("Version:"));
		metadata.addProperty("lang", input("Languages:"));
		metadata.addProperty("tag", input("Tag(s):"));
//		metadata.addProperty("tagcat", input("Tag Categories:"));
		metadata.addProperty("desc", input("Description:"));
		
		
		
	}
	
	
	/*
	public static void main(String[] args) throws IOException {
		
		
		
		
		
	}*/

}
