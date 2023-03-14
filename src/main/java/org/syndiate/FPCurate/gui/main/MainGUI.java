package org.syndiate.FPCurate.gui.main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.syndiate.FPCurate.CommonMethods;
import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.gui.MainWindow;
import org.syndiate.FPCurate.gui.SettingsWindow;
import org.syndiate.FPCurate.gui.common.CommonGUI;
import org.syndiate.FPCurate.gui.common.dialog.ConfirmDialog;
import org.syndiate.FPCurate.gui.common.dialog.ConfirmationListener;
import org.syndiate.FPCurate.gui.common.dialog.GenericDialog;

public class MainGUI {
	
	
	
	

	public static JScrollPane createConsoleOutput() {

		JTextArea consoleTextArea = new JTextArea();
		consoleTextArea.setEditable(false);
		consoleTextArea.setLineWrap(true);
		consoleTextArea.setWrapStyleWord(true);
		consoleTextArea.setBackground(Color.BLACK);
		consoleTextArea.setForeground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(consoleTextArea);
		System.setOut(new PrintStream(new ConsoleOutput(consoleTextArea)));

		return scrollPane;

	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * methods for main window init
	 */
	
	public static JMenuBar initMenuBar() {

		
		
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.setOpaque(true);
		menuBar.setBackground(Color.WHITE);
		

		Map<String, String> menuBarStrings = I18N.getStrings("main/menu_bar");
		Map<String, String> fileMenuItemStrings = I18N.getStrings("main/menu_bar/popups/file");
		Map<String, String> dialogMsgs = I18N.getStrings("dialog/messages");
		
		

		JMenu fileMenu = new JMenu(menuBarStrings.get("file"));

		
		
		JMenuItem openItem = new JMenuItem(fileMenuItemStrings.get("open"));
		openItem.addActionListener((ActionEvent e) -> {

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int result = fileChooser.showOpenDialog(null);
			if (result != JFileChooser.APPROVE_OPTION)
				return;

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
		fileMenu.add(restart);
		
		

		
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
		fileMenu.add(exit);
		
		
		
		
		menuBar.add(fileMenu);
		return menuBar;
	}

}

class ConsoleOutput extends OutputStream {

	private JTextArea consoleTextArea;

	public ConsoleOutput(JTextArea consoleTextArea) {
		this.consoleTextArea = consoleTextArea;
	}

	public void write(int b) throws IOException {
		consoleTextArea.append(String.valueOf((char) b));
		consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
	}
}