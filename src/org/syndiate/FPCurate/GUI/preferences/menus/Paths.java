package org.syndiate.FPCurate.GUI.preferences.menus;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Paths extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4524266154820670769L;

	public Paths() {

		this.setLayout(new GridLayout(2, 2));
		createPathField("Working curations:");
		createPathField("Zipped curations:");
        
	}
	
	
	private void createPathField(String label) {
		
		this.add(new JLabel(label));
		
		JTextField absolutePathField = new JTextField();
		
		JButton fileChooser = new JButton("...");
		fileChooser.setPreferredSize(new Dimension(5, absolutePathField.getPreferredSize().height));
		
        fileChooser.addActionListener((ActionEvent e) -> {
        	
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    absolutePathField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
        });
        
        this.add(absolutePathField);
        this.add(fileChooser);
        
        
	}

}
