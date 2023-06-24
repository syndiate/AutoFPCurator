package org.syndiate.FPCurate.gui.updater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.syndiate.FPCurate.CommonMethods;
import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.SettingsManager;
import org.syndiate.FPCurate.Updater;
import org.syndiate.FPCurate.gui.common.CommonGUI;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;

import com.google.gson.JsonArray;




public class UpdaterWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8592221804138374246L;
	
	
	private static final Map<String, String> commonStrs = I18N.getStrings("common");
	private static final Map<String, String> updaterStrs = I18N.getStrings("updater");
	private static final Map<String, String> updaterExStrs = I18N.getStrings("exceptions/updater");
	
	
	private static final JPanel updaterPanel = new JPanel();
	
	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static final int defaultTempPanelWidth = 20;
	
	private JPanel tempPanel = new JPanel();
	private boolean tempInUpdater = false;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void init() {
		
		
		
		
		
		{
			
			this.setTitle(updaterStrs.get("windowTitle"));
			this.setSize(960, 540);
			this.setLocationRelativeTo(null);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setBackground(new Color(255, 255, 255, 255));
			this.setLayout(new BorderLayout());
			CommonGUI.setIconImage(this, "logo.png");
			this.setVisible(true);
        
		}
        
        
        
		{
			JLabel logo = new JLabel();
			logo.setIcon(new ImageIcon(CommonMethods.getResourceByte("logo_hq.png")));
        
        	JPanel logoPanel = new JPanel(new BorderLayout());
        	logoPanel.add(logo);
        	this.getContentPane().add(logoPanel, BorderLayout.WEST);
		}
        
        
		{
			JPanel rootUpdaterPanel = new JPanel();
			rootUpdaterPanel.setLayout(new BoxLayout(rootUpdaterPanel, BoxLayout.X_AXIS));
			rootUpdaterPanel.setBackground(new Color(255, 255, 255, 255));
			rootUpdaterPanel.add(updaterPanel);
			this.getContentPane().add(rootUpdaterPanel, BorderLayout.CENTER);
		}
        
        
		{
			updaterPanel.setFocusable(false);
			updaterPanel.setSize(screenSize);
			updaterPanel.setMaximumSize(screenSize);
			updaterPanel.setMinimumSize(screenSize);
			updaterPanel.setPreferredSize(screenSize);
			updaterPanel.setLayout(new BoxLayout(updaterPanel, BoxLayout.Y_AXIS));
			updaterPanel.setBackground(new Color(255, 255, 255, 255));
		}
        
        
        refreshTempPanel();
        this.initUpdater();

		
        
	}
	
	
	
	
	
	
	public UpdaterWindow() {
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
		    @Override
		    protected Void doInBackground() {
		    	init();
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
	
	
	
	public static void main(String[] args) {
        new UpdaterWindow();
    }
	
	
	
	
	
	
	
	
	
	
	
	
	private void initUpdater() {
		
		
		addComponent(new JLabel(updaterStrs.get("checkingForUpdates")));
		createLineBreak();
		
		
		JsonArray releaseData;
		try {
			releaseData = Updater.getReleaseData();
		} catch (IOException e) {
			handleException(e, updaterExStrs.get("githubApiErr"));
			return;
		}
		
		
		
		boolean updatesAvailable = Updater.checkForUpdates(releaseData);
		if (!updatesAvailable) {
			
			addComponent(new JLabel(updaterStrs.get("noUpdates")));
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				new ErrorDialog(e);
			}
			
			this.dispose();
			return;
			
		}
		
		
		
		addComponent(new JLabel(updaterStrs.get("newUpdate")));
		// for some reason, after disposing of the window, the code still continues...now i have to add this boilerplate if conditional.
		if (!confirmUpdateContinue()) {
			return;
		}
		createLineBreak();
		
		
		
		
		addComponent(new JLabel(updaterStrs.get("choosePath")));
		CompletableFuture<File> savePathRef = new CompletableFuture<>();
		
		JButton choosePath = new JButton("Choose Path");
		choosePath.addActionListener((ActionEvent ev) -> {
			savePathRef.complete(promptSaveDir());
		});
		addComponent(choosePath);
		
		
		
		
		
		
		File savePath;
		try {
			savePath = savePathRef.get();
		} catch (InterruptedException | ExecutionException interruptEx) {
			handleException(interruptEx, updaterExStrs.get("choosePathRetrievalError"));
			return;
		}
		
		
		
		
		
		try {
			
			addSingleLineComponent(new JLabel(updaterStrs.get("downloadingUpdate")));
			Updater.downloadLatestUpdate(releaseData, savePath);
			
		} catch (IOException ioEx) {
			handleException(ioEx, updaterExStrs.get("downloadFail"));
			return;
		} catch (URISyntaxException uriEx) {
			handleException(uriEx, updaterExStrs.get("invalidDir"));
			return;
		} 
		
		
		
		addComponent(new JLabel(updaterStrs.get("openUpdatePrompt")));
		if (!confirmUpdateContinue()) {
			return;
		}
		
		
		
		addSingleLineComponent(new JLabel(updaterStrs.get("openUpdate")));
		
		SettingsManager.saveSetting("hasSeenLatestUpdate", "false");
		SettingsManager.saveSetting("lastUsedVersion", Updater.getAppVersion());
		
		
		
		try {
			Desktop.getDesktop().open(savePath);
		} catch (Exception openEx) {
			handleException(openEx, updaterExStrs.get("openUpdateFail") + savePath);
			return;
		}
		
		
		System.exit(0);
		
		
		
		
	}
	
	
	
	

	
	

	
	private File promptSaveDir() {
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = fileChooser.showOpenDialog(null);
		if (result != JFileChooser.APPROVE_OPTION) {
			
			addSingleLineComponent(new JLabel(updaterStrs.get("choosePath")));
			return promptSaveDir();
			
		}
		
		return fileChooser.getSelectedFile();
		
	}
	
	

	
	
	
	
	
	private void handleException(Exception ex, String msg) {
		
		if (tempPanel.getComponentCount() > 0) {
			createLineBreak();
		}
		addComponent(new JLabel(msg));
		
		
		JButton openStackTrace = new JButton(updaterStrs.get("openStackTrace"));
		openStackTrace.addActionListener((ActionEvent ev) -> {
			new ErrorDialog(ex);
		});
		
		addComponent(openStackTrace);
		
	}
	
	
	
	
	
	
	
	private boolean confirmUpdateContinue() {
		
		
		CompletableFuture<Boolean> confirmation = new CompletableFuture<>();
		
		SwingUtilities.invokeLater(() -> {
			
			JButton confirmBtn = new JButton(commonStrs.get("yes"));
			confirmBtn.addActionListener((ActionEvent e) -> {
				confirmation.complete(true);
			});
		
			JButton denyBtn = new JButton(commonStrs.get("no"));
			denyBtn.addActionListener((ActionEvent e) -> {
				confirmation.complete(false);
			});
		
		
			addComponent(confirmBtn);
			addComponent(denyBtn);
			createLineBreak();
			
		});
		
		
		try {
			
			if (confirmation.get()) {
				return true;
			}
			this.dispose();
			
		} catch (InterruptedException | ExecutionException e) {
			new ErrorDialog(e);
		}
		
		return false;
		
		
	}

	
	
	
	
	
	
	
	
	
	
	private void addComponent(Component comp) {
		
		
		if (!tempInUpdater) {
			updaterPanel.add(tempPanel);
			updaterPanel.revalidate();
			updaterPanel.repaint();
			tempInUpdater = true;
		}
		
		tempPanel.add(comp);
		tempPanel.revalidate();
		tempPanel.repaint();
		
		
		int compHeight = (int) Math.ceil(comp.getPreferredSize().getHeight());
		if (compHeight > tempPanel.getHeight()) {
			
			final Dimension newTempPanelSize = new Dimension(tempPanel.getWidth(), compHeight + 5);
			tempPanel.setSize(newTempPanelSize);
			tempPanel.setMinimumSize(newTempPanelSize);
			tempPanel.setMaximumSize(newTempPanelSize);
			tempPanel.setPreferredSize(newTempPanelSize);
			tempPanel.revalidate();
			tempPanel.repaint();
			
		}
		
		
	}
	
	
	private void addSingleLineComponent(Component comp) {
		createLineBreak();
		addComponent(comp);
	}
	
	
	private void createLineBreak() {
		refreshTempPanel();
		addComponent(Box.createVerticalStrut(3));
	}
	
	
	
	
	
	
	
	
	@SuppressWarnings("unused")
	private void removeComponent(Component comp) {
		updaterPanel.remove(comp);
		updaterPanel.revalidate();
		updaterPanel.repaint();
	}
	
	
	
	
	
	
	
	
	private void refreshTempPanel() {
		tempPanel = new JPanel();
		tempPanel.setBackground(new Color(255, 255, 255, 255));
		tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		final Dimension tempPanelSize = new Dimension(screenSize.width, defaultTempPanelWidth);
		tempPanel.setSize(tempPanelSize);
		tempPanel.setMaximumSize(tempPanelSize);
		tempPanel.setMinimumSize(tempPanelSize);
		tempPanel.setPreferredSize(tempPanelSize);
		
        tempInUpdater = false;
	}
	
	
	

}
