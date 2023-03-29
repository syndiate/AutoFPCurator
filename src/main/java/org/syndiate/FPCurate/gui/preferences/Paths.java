package org.syndiate.FPCurate.gui.preferences;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.gui.common.SettingsGUI;

public class Paths extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4524266154820670769L;
	private Map<String, String> pathsMenuStrs;

	
	
	public Paths() {

		this.pathsMenuStrs = I18N.getStrings("settings/paths");
		
		this.setLayout(new GridLayout(2, 2));
		this.createPathField("workingCurations");
		this.createPathField("zippedCurations");
        
	}
	
	
	
	
	private void createPathField(String settingsId) {
		
		this.add(new JLabel(this.pathsMenuStrs.get(settingsId)));
		
		
		JTextField absolutePathField = SettingsGUI.createTextField(settingsId);
		JButton fileChooser = new JButton("...");
		fileChooser.setPreferredSize(new Dimension(5, absolutePathField.getPreferredSize().height));
		
        fileChooser.addActionListener((ActionEvent e) -> {
        	
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                int result = chooser.showOpenDialog(null);
                if (result != JFileChooser.APPROVE_OPTION) {
                	return;
                }
                absolutePathField.setText(chooser.getSelectedFile().getAbsolutePath());
                
        });
        
        
        this.add(absolutePathField);
        this.add(fileChooser);
        
        
	}

}
