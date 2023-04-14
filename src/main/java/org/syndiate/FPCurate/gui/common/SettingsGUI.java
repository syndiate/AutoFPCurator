package org.syndiate.FPCurate.gui.common;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.syndiate.FPCurate.SettingsManager;
import org.syndiate.FPCurate.gui.settings.SettingsWindow;



public class SettingsGUI {
	
	
	
	
	
	
	
	public static JTextField createTextField(String settingsId) {
		
		JTextField textField = new JTextField();
		textField.setText(SettingsManager.getSetting(settingsId));
		textField.putClientProperty("settingsId", settingsId);
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
	        public void insertUpdate(DocumentEvent e) {
	            SettingsWindow.queueSetting(textField.getClientProperty("settingsId").toString(), textField.getText());
	        }
	        @Override
	        public void removeUpdate(DocumentEvent e) {
	            SettingsWindow.queueSetting(textField.getClientProperty("settingsId").toString(), textField.getText());
	        }
	        @Override
	        public void changedUpdate(DocumentEvent e) {
	            SettingsWindow.queueSetting(textField.getClientProperty("settingsId").toString(), textField.getText());
	        }
		});
		
		return textField;
		
	}
	
	
	public static JTextField createTextField(String settingsId, DocumentListener listener) {
		
		JTextField textField = new JTextField();
		textField.setText(SettingsManager.getSetting(settingsId));
		textField.getDocument().putProperty("settingsId", settingsId);
		textField.getDocument().putProperty("parent", textField);
		textField.getDocument().addDocumentListener(listener);
		
		return textField;
		
	}
	
	
	/*
	public static JTextField addCustomFieldListener(JTextField field, DocumentListener listener) {
		
		for(DocumentListener listen : field.getDocument().get) {
	        currentButton.removeActionListener( al );
	    }
		
		field.getDocument().removeDocumentListener(new DocumentListener() {
			@Override
            public void insertUpdate(DocumentEvent e) {
                SettingsWindow.queueSetting(settingsId, textField.getText());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                SettingsWindow.queueSetting(settingsId, textField.getText());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                SettingsWindow.queueSetting(settingsId, textField.getText());
            }
		});
	}
	*/
	
	
	public static JComboBox<String> createDropdown(String[] dropdownItems, String selectedItem, ItemListener listener) {
		
		JComboBox<String> dropdown = new JComboBox<>(dropdownItems);
		dropdown.setSelectedItem(selectedItem);
		dropdown.addItemListener(listener);
		return dropdown;
		
		
	}
	
	
	public static JCheckBox createCheckBox(String settingsId) {
		
		JCheckBox box = new JCheckBox();
		box.putClientProperty("settingsId", settingsId);
		box.setSelected(Boolean.parseBoolean(SettingsManager.getSetting(settingsId)));
		box.addItemListener((ItemEvent e) -> SettingsWindow.queueSetting(settingsId, box.isSelected()));
		return box;
		
		
	}
	
	


}
