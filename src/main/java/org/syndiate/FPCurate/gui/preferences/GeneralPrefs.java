package org.syndiate.FPCurate.gui.preferences;

import java.awt.GridLayout;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.gui.common.SettingsGUI;

public class GeneralPrefs extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6626305711096079237L;
	private Map<String, String> generalMenuStrs;
	
	public GeneralPrefs() {
		
		this.generalMenuStrs = I18N.getStrings("settings/general");
		
		this.setLayout(new GridLayout(2, 2));
		
		this.add(new JLabel(generalMenuStrs.get("languageDropdown")));
		this.add(SettingsGUI.createDropdown("globalLanguage", generalMenuStrs.get("languageDropdownItems").split(",")));
//		this.add(new JLabel(generalMenuStrs.get("languageDropdown")));
//		this.add(SettingsGUI.createDropdown("globalLanguage", )
	}
	
	
	
	
	
	
	
	
	
	

}
