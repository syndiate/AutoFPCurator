package org.syndiate.FPCurate;

import java.util.Map;



public class I18N {
	
	private static final String language = SettingsManager.getSetting("globalLanguage");
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getStrings(String namespaceDir) {
		
		String strs = CommonMethods.getResource("i18n/" + namespaceDir + "/" + language + ".json");
		Map<String,String> map = (Map<String, String>) CommonMethods.parseJSONStr(strs);
		return map;
		
	}
	
	
	
	

}
