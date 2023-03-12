package org.syndiate.FPCurate.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.SettingsManager;
import org.syndiate.FPCurate.gui.common.CommonGUI;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;



public class MainWindow {

	private JFrame frmAPCurator;
	public static final String SUN_JAVA_COMMAND = "sun.java.command";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
//		SettingsManager.saveSetting("globalLanguage", "en");
		EventQueue.invokeLater(() -> {
			try {
				MainWindow window = new MainWindow();
				window.frmAPCurator.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}

	/**
	 * Create the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @wbp.parser.entryPoint
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	private void initialize() {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			new ErrorDialog(new Exception("Could not load Windows look and feel", e));
		}
		
		
		
		frmAPCurator = new JFrame();
		frmAPCurator.getContentPane().setBackground(new Color(255, 255, 255));
		frmAPCurator.setBackground(new Color(255, 255, 255));
		frmAPCurator.setTitle("AutoFPCurator");
		frmAPCurator.setBounds(100, 100, 1280, 720);
		frmAPCurator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAPCurator.setLocationRelativeTo(null);
		
		
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setBackground(Color.WHITE);
		frmAPCurator.setJMenuBar(menuBar);
		
		

		JMenu fileMenu = new JMenu("File");
		
		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener((ActionEvent e) -> {
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
			    File selectedFile = fileChooser.getSelectedFile();
			}
			
		});
		CommonGUI.setMenuItemShortcut(openItem, KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
		fileMenu.add(openItem);
		
		
		
		JMenuItem preferences = new JMenuItem("Preferences");
		preferences.addActionListener((ActionEvent e) -> {
			new SettingsWindow();
		});
		CommonGUI.setMenuItemShortcut(preferences, KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK);
		fileMenu.add(preferences);
		
		
		menuBar.add(fileMenu);
		
		Map<String, String> messageStrs = I18N.getStrings("main/message");
		
		JLabel label = new JLabel("<html>" + messageStrs.get("instructions")
				+ "<br>" + messageStrs.get("iterateThrough")
				+ "<br><br><b>" + messageStrs.get("noticeHeader") + "</b>"
				+ "<br>" + messageStrs.get("notice"));
		
        label.setFont(label.getFont().deriveFont(14.0f));
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(label, new GridBagConstraints());

        frmAPCurator.add(panel, BorderLayout.CENTER);
		
		
	}
	
	
	
	
	// https://dzone.com/articles/programmatically-restart-java
	public static void restartApplication(Runnable runBeforeRestart) {
		
		try {
			// java binary
			String java = System.getProperty("java.home") + "/bin/java";
			// vm arguments
			List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
			
			StringBuilder vmArgsOneLine = new StringBuilder();
			for (String arg : vmArguments) {
				if (arg.contains("-agentlib")) continue;

				vmArgsOneLine.append(arg);
				vmArgsOneLine.append(" ");
			}
			// init the command to execute, add the vm args
			final StringBuilder cmd = new StringBuilder("\"" + java + "\" " + vmArgsOneLine);

			// program main and program arguments
			String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
			// program main is a jar
			if (mainCommand[0].endsWith(".jar")) {
				// if it's a jar, add -jar mainJar
				cmd.append("-jar " + new File(mainCommand[0]).getPath());
			} else {
				// else it's a .class, add the classpath and mainClass
				cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
			}
			// finally add program arguments
			for (int i = 1; i < mainCommand.length; i++) {
				cmd.append(" ");
				cmd.append(mainCommand[i]);
			}
			// execute the command in a shutdown hook, to be sure that all the
			// resources have been disposed before restarting the application
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						Runtime.getRuntime().exec(cmd.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			// execute some custom code before restarting
			if (runBeforeRestart != null) {
				runBeforeRestart.run();
			}
			// exit
			System.exit(0);
		} catch (Exception e) {
			new ErrorDialog(new Exception("Error while trying to restart the application", e));
		}
	}
	
	
	
	
	
	
	
	
	
	
	

}