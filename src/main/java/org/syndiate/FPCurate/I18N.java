package org.syndiate.FPCurate;

import java.util.Map;

import com.google.gson.Gson;



public class I18N {
	
//	public static String language = SettingsManager.getSetting("globalLanguage");
	public static final String language = SettingsManager.getSetting("globalLanguage");
	
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getStrings(String namespaceDir) {
		
		String strs = CommonMethods.getResource("i18n/" + namespaceDir + "/" + language + ".json");
		Map<String,String> map = new Gson().fromJson(strs, Map.class);
		return map;
		
	}
	
	
	
	

}
