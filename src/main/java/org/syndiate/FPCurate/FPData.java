package org.syndiate.FPCurate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.syndiate.FPCurate.gui.common.ErrorDialog;

public class FPData {
	
	private static final String fpDataDomain = "https://flashpoint-search.unstable.life";
	private static final String searchDataEndpoint = fpDataDomain + "/search.php";
	private static final String curationDataEndpoint = fpDataDomain + "/view.php";
	
	
	
	
	public static String getSearchData(String title) {
		
		if (title == null) {
			return "";
		}
		
		String searchData = "";
		
		try {
			
			
			URL url = new URL(FPData.searchDataEndpoint);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        connection.setDoOutput(true);

	        String jsonInputString = "q=" + title.replaceAll(" ", "+") + "&by=Best+match";

	        
	        try (OutputStream os = connection.getOutputStream()) {
	            byte[] input = jsonInputString.getBytes("utf-8");
	            os.write(input, 0, input.length);           
	        }
	        int responseCode = connection.getResponseCode();
	        
	        if (responseCode != 200) {
	        	throw new Exception("Failed to retrieve search data. Is flashpoint-search.unstable.life down?");
	        }
	        		
	        
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
	        
	        String currentLine = "";
	        StringBuilder responseLine = new StringBuilder();
	        
	        while ((currentLine = br.readLine()) != null) {
	            responseLine.append(currentLine.trim());
	        }
	        
	        searchData = responseLine.toString();
	        
	        
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return searchData;
        
	}
	
	
	
	
	
	public static String getCurationData(String UUID) {
		
		if (UUID == null) {
			return "";
		}
		
		String curationData = "";
		
		try {
			
			URL curation = new URL(FPData.curationDataEndpoint + "?id=" + UUID);
			URLConnection curationConn = curation.openConnection();
        
			BufferedReader in = new BufferedReader(
                                	new InputStreamReader(
                                			curationConn.getInputStream()));
        
			String currentLine = "";
			StringBuilder inputLine = new StringBuilder("");

			while ((currentLine = in.readLine()) != null) 
				inputLine.append(currentLine);
            
			in.close();
			
			 curationData = inputLine.toString();
			
		} catch (Exception e) {
			new ErrorDialog(e);
			System.out.println("Unable to retrieve curation data.");
			return "";
		}
		
		return curationData;
        
	}

	
	
	
}
