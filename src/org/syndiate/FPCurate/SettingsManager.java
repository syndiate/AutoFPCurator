package org.syndiate.FPCurate;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SettingsManager {

    private static final String APP_FOLDER_NAME = "AutoFPCurator";
    private static String SETTINGS_FILE_NAME = "settings.json";
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File appFolder = new File(System.getenv("APPDATA"), APP_FOLDER_NAME);

    public static void saveSetting(String key, String value) throws IOException {
    	
        appFolder.mkdirs();
        File settingsFile = new File(appFolder, SETTINGS_FILE_NAME);

        FileReader reader = new FileReader(settingsFile);
        JsonObject jsonObject = settingsFile.exists() ? GSON.fromJson(reader, JsonObject.class) : new JsonObject();

        jsonObject.addProperty(key, value);

        try (FileWriter writer = new FileWriter(settingsFile)) {
            writer.write(GSON.toJson(jsonObject));
        }
    }
    
    
    

    public static String getSetting(String key) throws IOException {
        File appFolder = new File(System.getenv("APPDATA"), APP_FOLDER_NAME);
        File settingsFile = new File(appFolder, SETTINGS_FILE_NAME);

        if (!settingsFile.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(settingsFile)) {
            JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
            JsonElement valueElement = jsonObject.get(key);
            if (valueElement != null) {
                return valueElement.getAsString();
            }
        }

        return null;
    }
}