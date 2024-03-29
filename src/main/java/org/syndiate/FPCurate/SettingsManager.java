package org.syndiate.FPCurate;

import java.util.prefs.Preferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class SettingsManager {
	
    private static final Preferences prefs = Preferences.userNodeForPackage(SettingsManager.class);

    
    
    
    public static void saveSetting(String key, String value) {
    	if (key == null || value == null) {
    		return;
    	}
    	prefs.put(key, value);
    }
    
    
    public static String getSetting(String key) {
    	if (key == null) {
    		return "";
    	}
    	return prefs.get(key, SettingsManager.getDefaultSetting(key));
    }
    
    
    public static String getDefaultSetting(String key) {
    	JsonObject defaultSettings = new Gson().fromJson(CommonMethods.getResource("defaultSettings.json"), JsonObject.class);

    	if (defaultSettings.get(key) == null) {
    		return "";
    	}
    	return defaultSettings.get(key).getAsString();
    }
    
    
    
    
    
}