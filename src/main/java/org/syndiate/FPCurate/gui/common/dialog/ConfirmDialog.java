package org.syndiate.FPCurate.gui.common.dialog;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

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
		int confirmationResult = JOptionPane.showConfirmDialog(
			    null,
			    confirmationMsg,
			    "Confirmation",
			    JOptionPane.OK_CANCEL_OPTION
			);
		
		
		if (confirmationResult == JOptionPane.OK_OPTION) {
		    // Handle "OK" button click
		} else {
		    // Handle "Cancel" button click
		}
		
		this.setVisible(true);
	}

}
