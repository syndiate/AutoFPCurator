package org.syndiate.FPCurate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;

import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;



public class CommonMethods {
	
	
	
	public static String getResource(String filePath) {
		
		if (filePath == null) {
			return null;
		}
		
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
		
		if (file == null) {
			return "";
		}
		
		String fileName = file.getName();
		int lastDotIndex = fileName.lastIndexOf('.');
		if (lastDotIndex <= 0) {
			return "";
		}
		

		String fileExtension = fileName.substring(lastDotIndex + 1);
		return fileExtension;
		
	}
	
	
	
	// the string is split and joined to prevent any stray semicolons (separators) at the start or end of the value
	public static String correctSeparators(String input, String delimiter) {
		return String.join(delimiter + " ", new ArrayList<String>(Arrays.asList(input.split(delimiter))));
	}
	
	
	/*
	public static boolean isValidDate(String date) {
		
		if (date == null) {
			return false;
		}
		
	    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
	            .appendPattern("uuuu-MM")
	            .optionalStart()
	            .appendPattern("-dd")
	            .optionalEnd()
	            .toFormatter();
	    try {
	        LocalDate parsedDate = LocalDate.parse(date, formatter);
	        return date.equals(parsedDate.format(formatter));
	    } catch (DateTimeParseException e) {
	        return false;
	    }
	}*/
	public static boolean isValidDate(String date) {
		
		if (date == null) {
			return false;
		}
		
	    DateTimeFormatter[] formatters = {
	            new DateTimeFormatterBuilder().appendPattern("yyyy")
	                    .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
	                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
	                    .toFormatter(),
	            new DateTimeFormatterBuilder().appendPattern("yyyy-MM")
	                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
	                    .toFormatter(),
	            new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd")
	                    .parseStrict().toFormatter() 
	    };
	    
	    for (DateTimeFormatter formatter : formatters) {
	        try {
	            LocalDate.parse(date, formatter);
	            return true;
	        } catch (DateTimeParseException e) {
	        }
	    }
	    return false;
	}

	
	

}
