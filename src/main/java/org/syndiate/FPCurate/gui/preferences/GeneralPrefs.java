package org.syndiate.FPCurate.gui.preferences;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.gui.SettingsWindow;
import org.syndiate.FPCurate.gui.common.SettingsGUI;

public class GeneralPrefs extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6626305711096079237L;
	private Map<String, String> generalMenuStrs;
	
	public GeneralPrefs() {
		
		this.generalMenuStrs = I18N.getStrings("settings/general");
		
		this.setLayout(new GridLayout(1, 2));
		
		this.add(new JLabel(generalMenuStrs.get("languageDropdown")));
		
		Map<String, String> languages = I18N.getStrings("settings/general/languages");
		ArrayList<String> languageItems = new ArrayList<>();
		
		for (Map.Entry<String, String> entry : languages.entrySet()) {
			languageItems.add(entry.getValue());
		}
		
		this.add(SettingsGUI.createDropdown
				(
						Arrays.stream(languageItems.toArray()).toArray(String[]::new),
						I18N.getStrings("settings/general/languages").get(I18N.getLanguage()),
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
//		this.add(new JLabel(generalMenuStrs.get("languageDropdown")));
//		this.add(SettingsGUI.createDropdown("globalLanguage", )
	}
	
	
	
	
	
	
	
	
	
	

}
