package org.syndiate.FPCurate.gui.settings.preferences;

import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.SettingsManager;
import org.syndiate.FPCurate.gui.common.SettingsGUI;
import org.syndiate.FPCurate.gui.settings.SettingsWindow;

public class GeneralPrefs extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6626305711096079237L;
	
	
	public GeneralPrefs() {
		
		int gridRows = CurationPrefs.getRows();
		this.setLayout(new GridLayout(gridRows, 2));
//		this.setLayout(new GridBagLayout());
		
		Map<String, String> generalMenuStrs = I18N.getStrings("settings/general");
		
		
		
		
		
		
		
		
		// i love you boilerplate yes i do
		Map<String, String> languages = I18N.getStrings("settings/general/languages");
		this.add(new JLabel(generalMenuStrs.get("languageDropdown")));
		this.add(SettingsGUI.createDropdown
				(
						Arrays.stream(SettingsWindow.settingChoicesArr(languages)).toArray(String[]::new),
						languages.get(I18N.getLanguage()),
						e -> {
							@SuppressWarnings("unchecked")
							JComboBox<String> cb = (JComboBox<String>) e.getSource();
							switch (cb.getSelectedIndex()) {
								case 0:
									SettingsWindow.queueSetting("globalLanguage", "en");
									break;
								case 1:
									SettingsWindow.queueSetting("globalLanguage", "es");
									break;
								case 2:
									SettingsWindow.queueSetting("globalLanguage", "fr");
									break;
							}
					
						}
				)
		);
		
		
		
		Map<String, String> updateCheckStrs = I18N.getStrings("settings/general/update_check");
		this.add(new JLabel(generalMenuStrs.get("updateCheck")));
		this.add(SettingsGUI.createDropdown
				(
						Arrays.stream(SettingsWindow.settingChoicesArr(updateCheckStrs)).toArray(String[]::new),
						updateCheckStrs.get(SettingsManager.getSetting("updateCheck")),
						e -> {
							@SuppressWarnings("unchecked")
							JComboBox<String> cb = (JComboBox<String>) e.getSource();
							switch (cb.getSelectedIndex()) {
								case 0:
									SettingsWindow.queueSetting("updateCheck", "never");
									break;
								case 1:
									SettingsWindow.queueSetting("updateCheck", "startup");
									break;
							}
							
						}
				)
		);
						
		
		
		// whitespace below the "real" components so that the "real" components don't take up the whole screen
		// configure i to the amount of fields
		for (int i = 1; i <= gridRows; i++) {
			this.add(Box.createVerticalBox());
		}
//		this.add(new JLabel(generalMenuStrs.get("languageDropdown")));
//		this.add(SettingsGUI.createDropdown("globalLanguage", )
	}
	
	
	
	
	
	
	
	
	
	

}
