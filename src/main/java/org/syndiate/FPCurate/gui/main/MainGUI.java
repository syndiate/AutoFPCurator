package org.syndiate.FPCurate.gui.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.syndiate.FPCurate.CommonMethods;
import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.gui.common.CommonGUI;
import org.syndiate.FPCurate.gui.common.dialog.ConfirmDialog;
import org.syndiate.FPCurate.gui.common.dialog.ConfirmationListener;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;
import org.syndiate.FPCurate.gui.common.dialog.GenericDialog;
import org.syndiate.FPCurate.gui.settings.SettingsWindow;





public class MainGUI {
	

	
	

	/**
	 * methods for main window init
	 */
	
	public static JMenuBar initMenuBar() {
		

		Map<String, String> menuBarStrings = I18N.getStrings("main/menu_bar");
		Map<String, String> fileMenuItemStrings = I18N.getStrings("main/menu_bar/popups/file");
		Map<String, String> dialogMsgs = I18N.getStrings("dialog/messages");
		
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setBackground(Color.WHITE);

		

		JMenu fileMenu = new JMenu(menuBarStrings.get("file"));

		
		
		JMenuItem openItem = new JMenuItem(fileMenuItemStrings.get("open"));
		openItem.addActionListener((ActionEvent e) -> {
			

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int result = fileChooser.showOpenDialog(null);
			if (result != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File selectedFile = fileChooser.getSelectedFile();
			

			if (selectedFile.isFile()) {

				String fileExtension = CommonMethods.getFileExtension(selectedFile);
				switch (fileExtension) {
					case "swf":
						MainWindow.handleSWF(selectedFile);
						break;
					case "zip":
						MainWindow.handleZippedCuration(selectedFile);
						break;
					default:
						new GenericDialog("File must be of type SWF or ZIP.");
				}
				return;
			}
			

		});
		CommonGUI.setMenuItemShortcut(openItem, KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
		fileMenu.add(openItem);
		
		

		
		JMenuItem preferences = new JMenuItem(fileMenuItemStrings.get("preferences"));
		preferences.addActionListener((ActionEvent e) -> {
			new SettingsWindow();
		});
		CommonGUI.setMenuItemShortcut(preferences, KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK);
		fileMenu.add(preferences);
		
		
		
		
		JMenuItem restart = new JMenuItem(fileMenuItemStrings.get("restart"));
		restart.addActionListener((ActionEvent e) -> {

			new ConfirmDialog(dialogMsgs.get("restartConfirmation"), new ConfirmationListener() {

				public void onConfirm() {
					MainWindow.restartApplication(null);
				}

				public void onCancel() {
				}
			});

		});
		
		

		
		JMenuItem exit = new JMenuItem(fileMenuItemStrings.get("exit"));
		exit.addActionListener((ActionEvent e) -> {

			new ConfirmDialog(dialogMsgs.get("exitConfirmation"), new ConfirmationListener() {

				public void onConfirm() {
					System.exit(0);
				}
				public void onCancel() {

				}
			});

		});
		
		
		fileMenu.add(restart);
		fileMenu.add(exit);
		
		menuBar.add(fileMenu);
		return menuBar;
	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	public static String input(String prompt, KeyAdapter event) {
		
		CompletableFuture<String> future = new CompletableFuture<>();
		
	    SwingUtilities.invokeLater(() -> {

	    	JPanel row = new JPanel();
	    	row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
	    	row.setBackground(new Color(255, 255, 255));
	    	row.setAlignmentX(Component.LEFT_ALIGNMENT);
	    	
	    	
	        JLabel label = new JLabel(prompt);
	        label.setPreferredSize(new Dimension(100, 20));

	        JTextField field = new JTextField();
	        field.addActionListener((ActionEvent e) -> {
	            future.complete(field.getText());
	        });
	        field.addKeyListener(event);

	        
			Dimension textFieldSize = new Dimension(235, 25);
	        field.setMaximumSize(textFieldSize);
	        field.setPreferredSize(textFieldSize);
	        
	        
	        row.add(label);
	        row.add(field);
	        row.add(Box.createRigidArea(new Dimension(0, 10)));
	        MainWindow.addComponent(row);
	        field.requestFocusInWindow();

	        
	    });

	    try {
	        return future.get();
	    } catch (InterruptedException | ExecutionException e) {
	        new ErrorDialog(e);
	    }
	    

	    return "";
		
	}

}













