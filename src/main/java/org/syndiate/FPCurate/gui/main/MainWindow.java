package org.syndiate.FPCurate.gui.main;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;

import org.syndiate.FPCurate.CommonMethods;
import org.syndiate.FPCurate.Curation;
import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.Screenshot;
import org.syndiate.FPCurate.SettingsManager;
import org.syndiate.FPCurate.Updater;
import org.syndiate.FPCurate.gui.common.CommonGUI;
import org.syndiate.FPCurate.gui.common.dialog.ConfirmDialog;
import org.syndiate.FPCurate.gui.common.dialog.ConfirmationListener;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;
import org.syndiate.FPCurate.gui.common.dialog.GenericDialog;
import org.syndiate.FPCurate.gui.cropper.CropperManager;
import org.syndiate.FPCurate.gui.main.startup.ChangelogDialog;
import org.syndiate.FPCurate.gui.manual.ManualWindow;
import org.syndiate.FPCurate.gui.settings.DocumentChangeListener;
import org.syndiate.FPCurate.gui.updater.UpdaterWindow;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;









public class MainWindow {
	
	
	
	

	private static final JFrame frmAPCurator = new JFrame();
	private static JPanel mainPanel = new JPanel(new GridBagLayout());
	private static Curation mainCuration = null;
	private static String unsavedMeta = "";
	
	
	private static final Map<String, String> exStrs = I18N.getStrings("exceptions/main");
	private static final Map<String, String> mainMiscStrs = I18N.getStrings("main/misc");
	private static final Map<String, String> menuBarStrs = I18N.getStrings("main/menu_bar");
	
	

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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			new ErrorDialog(new Exception(exStrs.get("windowsLookAndFeel"), e));
		}
		
		loadWelcomeScreen();
		frmAPCurator.setVisible(true);
		initStartupProcedure();

	}
	
	
	
	
	
	
	
	public static void loadWelcomeScreen() {
		
		Map<String, String> messageStrs = I18N.getStrings("main/message");
		
		
		frmAPCurator.getContentPane().removeAll();
		frmAPCurator.getContentPane().revalidate();
		frmAPCurator.getContentPane().repaint();
		
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(new Color(237, 237, 236));
		mainPanel.removeAll();
		mainPanel.revalidate();
		mainPanel.repaint();
		

		
		frmAPCurator.setTitle("AutoFPCurator [v" + Updater.getAppVersion() + "]");
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

		CommonGUI.setIconImage(frmAPCurator, "logo.png");
		
	}
	
	
	
	
	
	
	
	public static void initStartupProcedure() {
		
		
		Map<String, String> startupStrs = I18N.getStrings("main/startup");
		
		
		if (SettingsManager.getSetting("hasOpenedAutoFPCurator").equals("false")) {
			
			
			CompletableFuture<Boolean> hasSeenManual = new CompletableFuture<>();
			new ConfirmDialog(startupStrs.get("youreNew"), new ConfirmationListener() {
				
				public void onConfirm() {
					new ManualWindow();
					hasSeenManual.complete(true);
				}
				public void onCancel() {
					hasSeenManual.complete(true);
				}
				
			});
			
			try {
				hasSeenManual.get();
			} catch (InterruptedException | ExecutionException e) {
				new ErrorDialog(e);
			}
			
			SettingsManager.saveSetting("hasOpenedAutoFPCurator", "true");

			
		}
		
		
		
		
		if (SettingsManager.getSetting("lastUsedVersion").equals("")) {
			SettingsManager.saveSetting("lastUsedVersion", Updater.getAppVersion());
		}
		if (CommonMethods.compareVersions(Updater.getAppVersion(), SettingsManager.getSetting("lastUsedVersion"))) {
			SettingsManager.saveSetting("hasSeenLatestUpdate", "true");
		}
		
		
		
		if (SettingsManager.getSetting("hasSeenLatestUpdate").equals("false")) {
			new ChangelogDialog();
			SettingsManager.saveSetting("hasSeenLatestUpdate", "true");
		}
		
		
		if (SettingsManager.getSetting("updateCheck").equals("startup")) {
			new UpdaterWindow();
		}
		
	}
	
	
	
	
	
	
	
	
	
	public static void handleFile(File selectedFile) {
		
		if (!selectedFile.isFile()) {
			MainWindow.handleSWFAndFolder(selectedFile);
			return;
		}

		String fileExtension = CommonMethods.getFileExtension(selectedFile);
		switch (fileExtension) {
			case "swf":
				MainWindow.handleSWFAndFolder(selectedFile);
				break;
			case "zip":
//				MainWindow.handleZippedCuration(selectedFile);
				break;
			default:
				new GenericDialog(exStrs.get("swfOrZipFileRequired"));
		}
		return;
		
	}
	
	
	
	
	
	
	
	
	public static void handleSWFAndFolder(File swfFile) {
		
		
		
		String workingCurations = SettingsManager.getSetting("workingCurations");
		String zippedCurations = SettingsManager.getSetting("zippedCurations");
		
		if (workingCurations.equals("") || zippedCurations.equals("")) {
			new GenericDialog(exStrs.get("specifyPaths"));
			return;
		}
		
		
		
		frmAPCurator.getContentPane().remove(MainWindow.mainPanel);
		
		
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	    mainPanel.setBackground(new Color(255, 255, 255));
	    
	    
	    
	    
	    JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
        tabbedPane.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        
        
        JScrollPane mainPanelHost = new JScrollPane(mainPanel);
        mainPanelHost.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        
        tabbedPane.addTab(mainMiscStrs.get("mainTab"), new JScrollPane(mainPanel));
        tabbedPane.addTab("Meta.yaml", new JPanel());
        tabbedPane.addChangeListener((ChangeEvent ev) -> {
        	
        	int index = tabbedPane.getSelectedIndex();
        	Component comp = tabbedPane.getComponentAt(index);
        	
        	switch (index) {
        		case 0: {
        			
        			String title = tabbedPane.getTitleAt(1);
        			if (!title.equals("*Meta.yaml")) {
        				return;
        			}
        			
        			new ConfirmDialog(mainMiscStrs.get("unsavedMetaYAML"), 
        				new ConfirmationListener() {
        					public void onConfirm() {
        						tabbedPane.setTitleAt(1, "Meta.yaml");
        						unsavedMeta = "";
        					}
        					public void onCancel() {
        						tabbedPane.setSelectedIndex(1);
        					}
        				}
        			);
        			break;
        		
        		}
        		case 1:
        			MainWindow.loadMetaPanel((JPanel) comp);
        			break;
        	}
        	
        });
        
        
        
        
        
        
        
        
        
        
        
        
        
        {
        	
        	Map<String, String> curationItems = I18N.getStrings("main/menu_bar/items/curation");
        	
        	JMenu curationMenu = new JMenu(menuBarStrs.get("curation"));

			JMenuItem openCuration = new JMenuItem(curationItems.get("openCuration"));
			openCuration.addActionListener((ActionEvent e) -> {
				try {
					Desktop.getDesktop().open(mainCuration.getCurFolder());
				} catch (IOException ex) {
					new ErrorDialog(ex);
				}
			});
			CommonGUI.setMenuItemShortcut(openCuration, KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK);
			curationMenu.add(openCuration);
			

			
			
			JMenuItem openSWF = new JMenuItem(curationItems.get("openSWF"));
			openSWF.addActionListener((ActionEvent e) -> {
				try {
					Desktop.getDesktop().open(mainCuration.getSWF());
				} catch (IOException ex) {
					new ErrorDialog(ex);
				}
			});
			CommonGUI.setMenuItemShortcut(openSWF, KeyEvent.VK_O, KeyEvent.ALT_DOWN_MASK);
			curationMenu.add(openSWF);
			
			
			
			/*
			JMenuItem openCropper = new JMenuItem(curationItems.get("openLogo"));
			openCropper.addActionListener((ActionEvent ev) -> {
				File logoPath = new File(mainCuration.getCurFolder().getAbsolutePath() + "/logo.png");
				try {
					new CropperManager(logoPath, logoPath);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});
			CommonGUI.setMenuItemShortcut(openCropper, KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK);
			curationMenu.add(openCropper);*/
			
			
			
			
			JMenuItem terminateCuration = new JMenuItem(curationItems.get("delCuration"));
			terminateCuration.addActionListener((ActionEvent e) -> {
				new ConfirmDialog(mainMiscStrs.get("terminateCurationConfirm"), new ConfirmationListener() {
					public void onConfirm() {
						mainCuration.closeCuration(true);
					}
					public void onCancel() {}
				});
			});
			curationMenu.add(terminateCuration);
			
			
			
			
			JMenuItem clearCurationView = new JMenuItem(curationItems.get("clearCurationView"));
			clearCurationView.addActionListener((ActionEvent ev) -> {
				
				int componentCount = mainPanel.getComponentCount();
				// ensure not to clear the last-added component (the current prompt)
				for (int i = 0; i < componentCount - 1; i++) {
				    mainPanel.remove(0);
				}
				mainPanel.revalidate();
				mainPanel.repaint();

			});
			curationMenu.add(clearCurationView);
			
			
			
			
			
			
			
			
			
			
			
			
			{
				JMenu logoSSOptions = new JMenu(curationItems.get("logoSSOptions"));
			
				
				
				JMenuItem openCropper = new JMenuItem(curationItems.get("openLogo"));
				openCropper.addActionListener((ActionEvent ev) -> {
					File logoPath = new File(mainCuration.getCurFolder().getAbsolutePath() + "/logo.png");
					try {
						new CropperManager(logoPath, logoPath);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});
				CommonGUI.setMenuItemShortcut(openCropper, KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK);
				logoSSOptions.add(openCropper);
				
				
				
				
				
				JMenuItem takeSS = new JMenuItem(curationItems.get("takeSS"));
				takeSS.addActionListener((ActionEvent e) -> {
					
					try {
						mainCuration.writeCurImage(Screenshot.takeScreenshot(), "ss");
					} catch (IOException e1) {
						new GenericDialog(I18N.getStrings("exceptions/curation").get("flashWindowNotFound"));
					}
					
				});
				CommonGUI.setMenuItemShortcut(takeSS, KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK);
				logoSSOptions.add(takeSS);
			
				
				
				
				
				
				JMenuItem uploadLogoClipboard = new JMenuItem(curationItems.get("uploadLogoClipboard"));
				uploadLogoClipboard.addActionListener((ActionEvent ev) -> {
					
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		            if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
		            	
		                BufferedImage image;
						try {
							image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
						} catch (UnsupportedFlavorException | IOException imageReadEx) {
							new ErrorDialog(imageReadEx);
							return;
						}
		                
		                try {
							mainCuration.writeCurImage(image, "logo");
						} catch (IOException ioEx) {
							new ErrorDialog(ioEx);
							return;
						}
		                
		                new GenericDialog(curationItems.get("logoClipboardSaved"));
		                return;
		                
		                
		            }
		            new GenericDialog(curationItems.get("noClipboardImage"));
		            
				});
			
				logoSSOptions.add(uploadLogoClipboard);
			
				
				
				
				
				JMenuItem uploadSSClipboard = new JMenuItem(curationItems.get("uploadSSClipboard"));
				uploadSSClipboard.addActionListener((ActionEvent ev) -> {
					
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		            if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
		            	
		            	
		                BufferedImage image;
						try {
							image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
						} catch (UnsupportedFlavorException | IOException imageReadEx) {
							new ErrorDialog(imageReadEx);
							return;
						}
		                
		                try {
							mainCuration.writeCurImage(image, "ss");
						} catch (IOException ioEx) {
							new ErrorDialog(ioEx);
							return;
						}
		                
		                new GenericDialog(curationItems.get("ssClipboardSaved"));
		                return;
		                
		            }
		            new GenericDialog(curationItems.get("noClipboardImage"));
		            
				});
			
				logoSSOptions.add(uploadSSClipboard);
				
				
				curationMenu.add(logoSSOptions);
			}
			


			MainGUI.addMenuBarItem(curationMenu, 1);
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
	    frmAPCurator.getContentPane().add(tabbedPane, BorderLayout.CENTER);

	    frmAPCurator.revalidate();
	    frmAPCurator.repaint();
	    
	    
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
		    @Override
		    protected Void doInBackground() {
		    	
		    	
		    	
		    	if (swfFile.isDirectory()) {

		    		
		    		String dirName = swfFile.getAbsolutePath();
		    		if (new File(dirName + File.separator + "meta.yaml").exists()) {
		    	        	
		    			String curationUUID = Paths.get(dirName).getFileName().toString();
		    	        if (curationUUID.endsWith("/")) {
		    	        	curationUUID = curationUUID.substring(0, curationUUID.length() - 1);
		    	        }
		    	            
		    	        mainCuration = new Curation(curationUUID);
		    	        mainCuration.init(swfFile, true);
		    	        return null;
		    	        
		    	    }
		    		new Curation(swfFile);
		    		
		    		
		    	} else {
		    		mainCuration = new Curation();
		    		mainCuration.init(swfFile, true);
		    	}
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
	/*
	public static void handleZippedCuration(File zippedCuration) {
		
	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void loadMetaPanel(JPanel comp) {
		
		comp.removeAll();
		comp.revalidate();
		comp.repaint();
		
		
		JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, comp);
		
		JLabel loading = new JLabel(mainMiscStrs.get("loading"));
		comp.add(loading);
		comp.setLayout(new BorderLayout());
		
		
		
		JTextArea metaView = new JTextArea();
		metaView.setEditable(true);
		metaView.setLineWrap(true);
		metaView.setWrapStyleWord(true);
		metaView.putClientProperty("changed", "false");
		metaView.setBorder(new LineBorder(Color.BLACK, 1, false));
		
		

		BufferedReader metaReader;
		StringBuilder meta = new StringBuilder("");
		String line;
		
		
		
		// this is for if the user clicks cancel on the confirm dialog (asking to discard meta.yaml changes)
		if (unsavedMeta.equals("")) {
			
			try {
				metaReader = new BufferedReader(new FileReader(mainCuration.getMetaYaml()));
				while ((line = metaReader.readLine()) != null) {
					meta.append(line + "\n");
				}
				metaReader.close();
			} catch (IOException e) {
				meta.append(exStrs.get("unableToReadMeta"));
				metaView.setEditable(false);
			}
			
		} else {
			meta.append(unsavedMeta);
		}
		unsavedMeta = "";

		
		
		
		String previousText = meta.toString();
		if (previousText.equals("")) {
			metaView.setEditable(false);
			return;
		}
		
		
		
		metaView.setText(previousText);
		metaView.getDocument().addDocumentListener(new DocumentChangeListener() {
			public void update(DocumentEvent e) {
				
				String currentText = metaView.getText();
				if (currentText.equals(previousText)) {
					tabbedPane.setTitleAt(1, "Meta.yaml");
					return;
				}
				
				try {
					new Yaml().load(currentText);
				} catch (YAMLException ye) {
					metaView.setBorder(new LineBorder(Color.RED, 1, false));
					metaView.putClientProperty("changed", "false");
					return;
				}
				tabbedPane.setTitleAt(1, "*Meta.yaml");
				metaView.putClientProperty("changed", "true");
				metaView.setBorder(new LineBorder(Color.BLACK, 1, false));
				unsavedMeta = currentText;
				
				
				
			}
		});
		
		
		
		
		metaView.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				if (e.getKeyCode() != KeyEvent.VK_S || !e.isControlDown()) {
					return;
				}
				if (!metaView.getClientProperty("changed").equals("true")) {
					return;
				}
				e.consume();

				try {
					FileWriter writer = new FileWriter(mainCuration.getMetaYaml(), false);
					writer.write(metaView.getText());
					writer.close();
				} catch (IOException ex) {
					new ErrorDialog(ex);
				}
				
				tabbedPane.setTitleAt(1, "Meta.yaml");
			}
		});
		
		

		comp.add(new JScrollPane(metaView), BorderLayout.CENTER);
		comp.remove(loading);
		comp.revalidate();
		comp.repaint();
		
	}
	
	
	
	public static void addComponent(Component comp) {
		mainPanel.add(comp);
		mainPanel.revalidate();
        mainPanel.repaint();
	}
	
	public static void removeComponent(Component comp) {
		mainPanel.remove(comp);
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
			new ErrorDialog(new Exception(exStrs.get("restartApp"), e));
		}
	}
	
	
	
	
	
	
	
	
	

}