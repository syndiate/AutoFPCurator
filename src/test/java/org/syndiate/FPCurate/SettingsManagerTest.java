package org.syndiate.FPCurate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SettingsManagerTest {

	@Test
	void getSetting() {
		assertNotNull(SettingsManager.getSetting("workingCurations"));
	}
	
	@Test
	void getNullSetting() {
		assertNotNull(SettingsManager.getSetting(null));
	}
	
	@Test
	void getNonexistentSetting() {
		assertNotNull(SettingsManager.getSetting("this_setting_does_not_exist"));
	}
	
	@Test
	void getDefaultSetting() {
		assertNotNull(SettingsManager.getDefaultSetting("globalLanguage"));
	}
	
	@Test
	void getDefaultSettingNull() {
		assertNotNull(SettingsManager.getDefaultSetting(null));
	}
	
	@Test
	void getDefaultSettingNonexistent() {
		assertNotNull(SettingsManager.getDefaultSetting("this_setting_does_not_exist"));
	}
	
	
	@Test
	void setSetting() {
		SettingsManager.saveSetting("testSetting", "testSetting");
	}
	
	@Test
	void setNullSetting() {
		SettingsManager.saveSetting(null, null);
	}
	

}
