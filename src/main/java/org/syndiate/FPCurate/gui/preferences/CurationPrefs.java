package org.syndiate.FPCurate.gui.preferences;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CurationPrefs extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1393840234024191975L;
	
	
	private static final ArrayList<String> askFors = new ArrayList<>(Arrays.asList("Launch Command prefix:",
																				   "Ask whether the curation is a game or animation, default:",
																				   "Ask for series, default:",
																				   "Ask for developer, default:",
																				   "Ask for publisher, default:",
																				   "Ask for play mode, default:",
																				   "Ask for release date, default:",
																				   "Ask for version, default:",
																				   "Ask for languages, default:",
																				   "Ask for tags, default:",
																				   "Ask for source, default:",
																				   "Ask for status, default:",
																				   "Ask for game notes, default:",
																				   "Ask for original description, default:",
																				   "Prompt screenshot, default action (Y/N):",
																				   "Prompt to close curation, default action (Y/N):"
			));
	
	
	
	public CurationPrefs() {

		
		this.setLayout(new GridLayout(askFors.size(), 3));
		
		
		for (String entry : askFors) {
			createAskFor(entry);
		}
		
	}
	
	
	private void createAskFor(String label) {
		
		this.add(new JCheckBox());
		this.add(new JLabel(label));
		
		
		JTextField field = new JTextField();
		field.setEditable(false);
		
		this.add(field);
	}
	

}
