package org.syndiate.FPCurate.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;
import org.syndiate.FPCurate.gui.main.MainGUI;



public class MainWindow {

	private JFrame frmAPCurator;
	public static final String SUN_JAVA_COMMAND = "sun.java.command";
	private static JPanel mainPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
//		SettingsManager.saveSetting("globalLanguage", "en");
		EventQueue.invokeLater(() -> {
			new MainWindow();
		});
		
	}

	
	public MainWindow() {
		initialize();
	}
	

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
		
		
		frmAPCurator.setJMenuBar(MainGUI.initMenuBar());
		
		
		
		
		Map<String, String> messageStrs = I18N.getStrings("main/message");
		
		JLabel label = new JLabel("<html>" + messageStrs.get("instructions")
				+ "<br>" + messageStrs.get("iterateThrough")
				+ "<br><br><b>" + messageStrs.get("noticeHeader") + "</b>"
				+ "<br>" + messageStrs.get("notice"));
		
        label.setFont(label.getFont().deriveFont(14.0f));
        
        
        MainWindow.mainPanel = new JPanel(new GridBagLayout());
        MainWindow.mainPanel.add(label, new GridBagConstraints());

        frmAPCurator.add(MainWindow.mainPanel, BorderLayout.CENTER);
        frmAPCurator.setVisible(true);
		
	}
	
	public static void handleSWF(File swfFile) {
		
	}
	public static void handleZippedCuration(File zippedCuration) {
		
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
			System.exit(0);
			
		} catch (Exception e) {
			new ErrorDialog(new Exception("Error while trying to restart the application", e));
		}
	}
	
	
	
	
	
	
	
	
	
	
	

}