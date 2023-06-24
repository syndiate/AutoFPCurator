package org.syndiate.FPCurate.gui.main.startup;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.Updater;
import org.syndiate.FPCurate.gui.common.CommonGUI;



public class ChangelogDialog extends JFrame {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5467761795084007031L;
	private static final Map<String, String> whatsNewStrs = I18N.getStrings("main/startup/whatsnew");
	private static final Map<String, String> dialogStrs = I18N.getStrings("dialog");
	

	public ChangelogDialog() {
		
		
		this.setTitle(whatsNewStrs.get("windowTitle"));
		this.setSize(1026, 691);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		
		
		
		
		JLabel changelogHeader = new JLabel(whatsNewStrs.get("version") + Updater.getAppVersion() + whatsNewStrs.get("changelog"));
		changelogHeader.setHorizontalAlignment(SwingConstants.LEFT);
        this.getContentPane().add(changelogHeader, BorderLayout.NORTH);
		  
		
        
		
		JTextArea stackTrace = new JTextArea();
		stackTrace.setEditable(false);
		stackTrace.setLineWrap(true);
		stackTrace.setWrapStyleWord(true);
		stackTrace.setText(Updater.getChangelog());
		

		
		
		JScrollPane scrollPane = new JScrollPane(stackTrace);
		scrollPane.setBounds(10, 8, 414, 179);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                scrollPane.getBorder()
        ));
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		

		
		
		JButton okButton = new JButton(dialogStrs.get("okButton"));
		okButton.setBounds(194, 198, 63, 23);
		okButton.addActionListener((ActionEvent e) -> dispose());
		
		

		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(okButton);
		this.getContentPane().add(buttonPane, BorderLayout.SOUTH);
		this.getRootPane().setDefaultButton(okButton);
		
		
		
		CommonGUI.setIconImage(this, "logo.png");
		this.setVisible(true);
		
		
	}
	
	
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new ChangelogDialog();
		});
	}
}
