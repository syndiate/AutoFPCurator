package org.syndiate.FPCurate.gui.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.syndiate.FPCurate.CommonMethods;
import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.SettingsManager;
import org.syndiate.FPCurate.gui.common.dialog.ConfirmDialog;
import org.syndiate.FPCurate.gui.common.dialog.ConfirmationListener;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;
import org.syndiate.FPCurate.gui.main.MainWindow;
import org.syndiate.FPCurate.gui.settings.preferences.*;



public class SettingsWindow extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3875324643848537471L;
	private static final Map<String, String> queuedSettings = new HashMap<>();
	private static final ArrayList<String> restartSettings = new ArrayList<String>(Arrays.asList("globalLanguage"));

	

	public SettingsWindow() {
		
		SettingsWindow.queuedSettings.clear();
		
		Map<String, String> settingsMenuStrs = I18N.getStrings("settings");
		Map<String, String> dialogStrs = I18N.getStrings("dialog");
		Map<String, String> dialogMsgStrs = I18N.getStrings("dialog/messages");
		
		
        this.setTitle(settingsMenuStrs.get("windowTitle"));
        this.setSize(960, 540);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
		{
			tabbedPane.addTab(settingsMenuStrs.get("generalTab"), new GeneralPrefs());
			tabbedPane.addTab(settingsMenuStrs.get("pathsTab"), new PathsPrefs());
			tabbedPane.addTab(settingsMenuStrs.get("curationTab"), new CurationPrefs());
			this.add(tabbedPane, BorderLayout.NORTH);
		}
        
        
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));


		JButton okButton = new JButton(dialogStrs.get("okButton"));
		okButton.setActionCommand("OK");
		okButton.addActionListener((ActionEvent e) -> {
			SettingsWindow.saveSettings();
			dispose();
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);


		
		
		JButton cancelButton = new JButton(dialogStrs.get("cancelButton"));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener((ActionEvent e) -> {
			
			if (SettingsWindow.queuedSettings.size() <= 0) {
				dispose();
				return;
			}
				
			new ConfirmDialog(dialogMsgStrs.get("unsavedChanges") + dialogMsgStrs.get("exitConfirmation"),
				new ConfirmationListener() {
					public void onConfirm() {
						SettingsWindow.this.dispose();
					}

					public void onCancel() {}
				}
			);
			
		});
		

		buttonPane.add(cancelButton);
		
//		getContentPane().add(list, BorderLayout.NORTH);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		

		
		try {
			this.setIconImage(ImageIO.read(new ByteArrayInputStream(CommonMethods.getResourceByte("logo.png"))));
		} catch (IOException e) {
			new ErrorDialog(e);
		}
		
        this.setVisible(true);
    }
	
	
	
	
	
	
	
	
	
	public static void queueSetting(String key, String value) {
		if (SettingsManager.getSetting(key).equals(value)) {
			SettingsWindow.queuedSettings.remove(key);
			return;
		}
		SettingsWindow.queuedSettings.put(key, value);
	}
	
	public static void queueSetting(String key, boolean value) {
		SettingsWindow.queueSetting(key, String.valueOf(value));
	}
	
	public static void unqueueSetting(String key) {
		SettingsWindow.queuedSettings.remove(key);
	}
	
	
	
	
	
	public static void saveSettings() {
		
		boolean restart = false;
		
		for (Map.Entry<String, String> entry : SettingsWindow.queuedSettings.entrySet()) {
			if (SettingsWindow.restartSettings.contains(entry.getKey())) {
				restart = true;
			}
			SettingsManager.saveSetting(entry.getKey(), entry.getValue());
		}
		
		
		if (!restart) {
			return;
		}
		
		new ConfirmDialog(I18N.getStrings("settings").get("restartDialog"), new ConfirmationListener() {
			public void onConfirm() {
				MainWindow.restartApplication(null);
			}
			public void onCancel() {}
			
		});
	}
	
	
	
	
	
	

    public static void main(String[] args) {
        new SettingsWindow();
    }
}