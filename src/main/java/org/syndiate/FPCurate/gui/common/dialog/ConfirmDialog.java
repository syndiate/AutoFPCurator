package org.syndiate.FPCurate.gui.common.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class ConfirmDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6942330187588902749L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ConfirmDialog dialog = new ConfirmDialog("Sample dialog");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ConfirmDialog(String confirmationMsg) {
		
		setTitle("Confirmation");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		
		JLabel label = new JLabel(confirmationMsg);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setAlignmentY(Component.CENTER_ALIGNMENT);

		getContentPane().add(label, BorderLayout.CENTER);

		
		JPanel buttonPane = new JPanel();
		FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.CENTER);
		buttonPane.setLayout(fl_buttonPane);
		getContentPane().add(buttonPane);
		
		
			
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
			
			
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		
		
		this.setVisible(true);
		
	}

}
