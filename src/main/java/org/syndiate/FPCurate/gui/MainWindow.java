package org.syndiate.FPCurate.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import org.syndiate.FPCurate.Curation;
import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.SettingsManager;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;
import org.syndiate.FPCurate.gui.common.dialog.GenericDialog;
import org.syndiate.FPCurate.gui.main.MainGUI;



public class MainWindow {
	
	
	
	

	private static final JFrame frmAPCurator = new JFrame();
	private static JPanel mainPanel = new JPanel(new GridBagLayout());
	private static Curation mainCuration = null;
	
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			new MainWindow();
		});
	}

	
	public MainWindow() {
		

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			new ErrorDialog(new Exception("Could not load Windows look and feel", e));
		}
		
		loadWelcomeScreen();
		frmAPCurator.setVisible(true);

	}
	
	
	
	
	
	
	
	public static void loadWelcomeScreen() {
		
		
		Map<String, String> messageStrs = I18N.getStrings("main/message");

		
		frmAPCurator.getContentPane().removeAll();
		frmAPCurator.getContentPane().revalidate();
		frmAPCurator.getContentPane().repaint();
		
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.removeAll();
		mainPanel.revalidate();
		mainPanel.repaint();
		
		
		
		frmAPCurator.getContentPane().setBackground(new Color(237, 237, 236));
		frmAPCurator.setBackground(new Color(237, 237, 236));
		frmAPCurator.setTitle("AutoFPCurator");
		frmAPCurator.setSize(1280, 720);
		frmAPCurator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAPCurator.setLocationRelativeTo(null);
		frmAPCurator.setJMenuBar(MainGUI.initMenuBar());


		JLabel label = new JLabel(
				"<html>" + messageStrs.get("instructions") + "<br>" + messageStrs.get("iterateThrough") + "<br><br><b>"
						+ messageStrs.get("noticeHeader") + "</b>" + "<br>" + messageStrs.get("notice"));

		label.setFont(label.getFont().deriveFont(14.0f));

		
		
		MainWindow.mainPanel.add(label, new GridBagConstraints());
		
		frmAPCurator.add(mainPanel, BorderLayout.CENTER);
		frmAPCurator.revalidate();
		frmAPCurator.repaint();
		
	}
	
	
	
	
	
	
	public static void handleSWF(File swfFile) {
		
		
		String workingCurations = SettingsManager.getSetting("workingCurations");
		String zippedCurations = SettingsManager.getSetting("zippedCurations");
		
		if (workingCurations.equals("") || zippedCurations.equals("")) {
			new GenericDialog("Please specify what paths you would like AutoFPCurator to store working/zipped curations in in Settings > Paths.");
			return;
		}
		
		
		
		frmAPCurator.getContentPane().remove(MainWindow.mainPanel);
		
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	    mainPanel.setBackground(new Color(255, 255, 255));
	    
	    
	    JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
        tabbedPane.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        
        
        tabbedPane.addTab("Main", new JScrollPane(mainPanel));
        tabbedPane.addTab("Meta.yaml", new JPanel());
        tabbedPane.addChangeListener((ChangeEvent e) -> {
        	
        	int index = tabbedPane.getSelectedIndex();
        	Component comp = tabbedPane.getComponentAt(index);
        	
        	switch (index) {
        		case 1:
        			MainWindow.loadMetaPanel((JPanel) comp);
        			break;
        	}
        	
        });
        
        
        
        
        
	    frmAPCurator.getContentPane().add(tabbedPane, BorderLayout.CENTER);

	    frmAPCurator.revalidate();
	    frmAPCurator.repaint();
	    
	    
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
		    @Override
		    protected Void doInBackground() {
		    	mainCuration = new Curation();
		    	mainCuration.init(swfFile);
		        return null;
		    }

		    @Override
		    protected void done() {
		    	try {
					get();
				} catch (InterruptedException | ExecutionException e) {
					new ErrorDialog(e);
				}
		    }
		};
		worker.execute();
	    
	    
	    
		
	}
	
	public static void handleZippedCuration(File zippedCuration) {
		
	}
	
	
	
	
	
	
	
	private static void loadMetaPanel(JPanel comp) {

		comp.setLayout(new BorderLayout());
		
		JTextArea metaView = new JTextArea();
		metaView.setEditable(true);
		metaView.setLineWrap(true);
		metaView.setWrapStyleWord(true);
		

		BufferedReader metaReader;
		StringBuilder meta = new StringBuilder("");
		String line;
		
		
		try {
			metaReader = new BufferedReader(new FileReader(mainCuration.getMetaYaml()));
			while((line = metaReader.readLine()) != null) {
				meta.append(line);
			}
		} catch (IOException e) {
			meta.append("An error occurred when trying to read the meta.yaml file. Perhaps it was accidentally deleted?");
		}

		
		metaView.setText(meta.toString());

		comp.add(new JScrollPane(metaView), BorderLayout.CENTER);
		comp.revalidate();
		comp.repaint();
		
	}
	

	
	
	
	public static void addComponent(Component comp) {
		mainPanel.add(comp);
		mainPanel.revalidate();
        mainPanel.repaint();
	}
	
	
	
	
	
	
	
	
	
	// https://dzone.com/articles/programmatically-restart-java
	public static void restartApplication(Runnable runBeforeRestart) {
		
		final String SUN_JAVA_COMMAND = "sun.java.command";
		
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
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					Runtime.getRuntime().exec(cmd.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}));
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