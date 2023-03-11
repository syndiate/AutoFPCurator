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

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
	public MainWindow() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	private void initialize() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		
		
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
			    // Code to open the selected file goes here
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
		
		JLabel label = new JLabel("<html>Navigate to File > Open and select a file/directory to begin. "
				+ "Select a file (SWF or ZIP) if you would to curate a game. "
				+ "Select a directory if you would like AutoFPCurator to"
				+ "<br> iterate through every Flash game in the folder."
				+ "<br><br><b>NOTICE:</b>"
				+ "<br>This tool is only designed FOR SINGLE ASSET GAMES that work in the Flash projector OR in a standard HTML embed. Otherwise, please use Flashpoint Core.");
        label.setFont(label.getFont().deriveFont(16.0f)); // set font size to 64
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(label, new GridBagConstraints());

        frmAPCurator.add(panel, BorderLayout.CENTER);
		
		
	}
	
	
	
	
	// https://dzone.com/articles/programmatically-restart-java
	public static void restartApplication(Runnable runBeforeRestart) throws IOException {
		
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
			// something went wrong
//			throw new IOException("Error while trying to restart the application", e);
			new ErrorDialog(e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	

}