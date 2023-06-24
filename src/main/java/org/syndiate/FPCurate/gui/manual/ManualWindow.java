package org.syndiate.FPCurate.gui.manual;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.syndiate.FPCurate.CommonMethods;
import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.gui.common.CommonGUI;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;

public class ManualWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8315472187326048066L;
	private static final Map<String, String> manualStrs = I18N.getStrings("manual");
	
	private final JButton backButton = new JButton("< " + manualStrs.get("backBtnText"));
	private final JButton nextButton = new JButton(manualStrs.get("nextBtnText") + " >");
	
	private final JPanel pgContainer = new JPanel();
	private int currentPgNum = 0;
	
	// configure as pages are added/removed
	private final int maxPgNum = 7;
	
	
	
	
	public ManualWindow() {

		this.setTitle(manualStrs.get("windowTitle"));
		this.setSize(1026, 891);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		this.setResizable(false);
		CommonGUI.setIconImage(this, "logo.png");
		
		
		pgContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		pgContainer.setAlignmentY(Component.TOP_ALIGNMENT);
		this.getContentPane().add(pgContainer);
		
		loadPage(currentPgNum);
		
		

		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

			backButton.setFocusable(false);
			nextButton.setFocusable(false);

			buttonPane.add(backButton);
			buttonPane.add(nextButton);
			
			
			backButton.addActionListener((ActionEvent e) -> {
				nextButton.setVisible(true);
				loadPage(currentPgNum - 1);
			});
			
			nextButton.addActionListener((ActionEvent ev) -> {
				backButton.setVisible(true);
				loadPage(currentPgNum + 1);
			});
			
			this.getContentPane().add(buttonPane, BorderLayout.SOUTH);
		}
		
		
		
		
		this.setVisible(true);
	}
	
	
	
	public static void main(String[] args) {
		new ManualWindow();
	}
	
	
	
	
	
	
	public void loadPage(int pgNum) {
		
		pgContainer.removeAll();
		String pgIndex = "pg" + String.valueOf(pgNum);

		
		
		JLabel imgRenderer = new JLabel();
		byte[] pgImgByteStream = CommonMethods.getResourceByte("manual/" + pgIndex + ".png");
				
		if (pgImgByteStream != null) {
			
			Image pgImg;
			try {
				pgImg = ImageIO.read(new ByteArrayInputStream(pgImgByteStream));
			} catch (IOException e) {
				new ErrorDialog(e);
				return;
			}
			pgImg = pgImg.getScaledInstance(945, -1, Image.SCALE_SMOOTH);
	        imgRenderer = new JLabel(new ImageIcon(pgImg));
			
		}
		pgContainer.add(imgRenderer);
		
		
		
		
		
		JLabel instructions = new JLabel("<html><body style='width: 560px'>" + manualStrs.get(pgIndex) + "</body</html>");
		pgContainer.add(instructions);
		
		
		currentPgNum = pgNum;
		if (currentPgNum == 0) {
			backButton.setVisible(false);
		}
		if (currentPgNum == maxPgNum) {
			nextButton.setVisible(false);
		}
		
		
		
		pgContainer.revalidate();
		pgContainer.repaint();
	}

}
