package org.syndiate.FPCurate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;





public class Updater {
	
	
	
	
	private static final String releasesEndpoint = "https://api.github.com/repos/syndiate/AutoFPCurator/releases";
	@SuppressWarnings("unchecked")
	private static final Map<String, String> appMetadata = (Map<String, String>) CommonMethods.parseJSONStr(CommonMethods.getResource("updater_metadata.json"));
	private static final Map<String, String> exStrs = I18N.getStrings("exceptions/updater");
	
	
	
	
	public static boolean checkForUpdates(JsonArray releaseData) {
        
        JsonObject latestRelease = releaseData.get(0).getAsJsonObject();
        String latestVer = latestRelease.get("tag_name").getAsString();
        String currVer = getAppVersion();
        
        
        if (!CommonMethods.compareVersions(latestVer, currVer)) {
        	return false;
        }
        if (latestVer.equals(currVer)) {
        	return false;
        }
        
        return true;
		
	}
	
	
	
	
	
	
	public static void downloadLatestUpdate(JsonArray releaseData, File savePath) throws IOException, URISyntaxException {
		
		
		JsonObject latestRelease = releaseData.get(0).getAsJsonObject();
		JsonArray releaseAssets = latestRelease.get("assets").getAsJsonArray();
		
		
		AtomicReference<String> jarDownloadRef = new AtomicReference<>("");
		AtomicReference<String> jarDownloadName = new AtomicReference<>("");
		releaseAssets.forEach((JsonElement asset) -> {
			
			if (!jarDownloadRef.get().equals("")) {
				return;
			}
			
			JsonObject latestReleaseObj = asset.getAsJsonObject();
			
			String downloadURL = latestReleaseObj.get("browser_download_url").getAsString();
			if (!downloadURL.contains(".jar")) {
				return;
			}
			
			jarDownloadRef.set(downloadURL);
			jarDownloadName.set(latestReleaseObj.get("name").getAsString());
			
		});
		
		File newSavePath = new File(savePath.getAbsolutePath() + "/" + jarDownloadName);
		
		InputStream in = new URL(jarDownloadRef.get()).openStream();
		Files.copy(in, Paths.get(newSavePath.toURI()), StandardCopyOption.REPLACE_EXISTING);
		
		
	}
	
	
	
	
	
	
	
	public static JsonArray getReleaseData() throws IOException {
		
		
		URL url = new URL(releasesEndpoint);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("GET");

	    
	    if (con.getResponseCode() != 200) {
	    	throw new IOException(exStrs.get("githubApiErr"));
	    }
	    
	    String responseBody = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            responseBody = reader.lines().collect(Collectors.joining());
        }
        
        return new Gson().fromJson(responseBody, JsonArray.class);
        
        
	}
	
	
	
	
	
	
	
	public static String getAppVersion() {
		return appMetadata.get("appVersion");
	}
	
	
	public static String getChangelog() {
		return appMetadata.get("changelog");
	}
}