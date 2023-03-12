package org.syndiate.FPCurate;

import java.util.Map;



public class I18N {
	
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getStrings(String namespaceDir) {
		
		String strs = CommonMethods.getResource("i18n/" + namespaceDir + "/" + I18N.getLanguage() + ".json");
		Map<String,String> map = (Map<String, String>) CommonMethods.parseJSONStr(strs);
		return map;
		
	}
	
	public static String getLanguage() {
		return SettingsManager.getSetting("globalLanguage");
	}
	public static void setLanguage(String isoLanguage) {
		SettingsManager.saveSetting("globalLanguage", "isoLanguage");
	}
	
	
	

}
