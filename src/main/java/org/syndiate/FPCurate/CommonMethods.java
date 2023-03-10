package org.syndiate.FPCurate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;



public class CommonMethods {
	
	public static String getResource(String filePath) {
		
		InputStream is = I18N.class.getClassLoader().getResourceAsStream(filePath);
		if (is == null) {
			return null;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String currentLine = "";
		StringBuilder resourceContents = new StringBuilder("");
		
		try {
			while ((currentLine = br.readLine()) != null) {
				resourceContents.append(currentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return resourceContents.toString();
		
	}
	
	
	
	public static Object parseJSONStr(String json) {
		
		Object jsonObj;
		
		try {
			jsonObj = new Gson().fromJson(json, Object.class);
		} catch (JsonSyntaxException e) {
			new ErrorDialog(e);
			return null;
		}
		
		return jsonObj;
		
	}
	
	
	public static String getFileExtension(File file) {
		
		String fileName = file.getName();
		int lastDotIndex = fileName.lastIndexOf('.');
		if (lastDotIndex <= 0) return "";
		

		String fileExtension = fileName.substring(lastDotIndex + 1);
		return fileExtension;
		
	}
	

	
	

}
