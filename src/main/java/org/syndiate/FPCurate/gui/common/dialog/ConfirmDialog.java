package org.syndiate.FPCurate.gui.common.dialog;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.syndiate.FPCurate.gui.common.CommonGUI;


public class ConfirmDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6942330187588902749L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
			
		new ConfirmDialog("Sample dialog", new ConfirmationListener() {
			public void onConfirm(JDialog dialog) {
				CommonGUI.closeDialog(dialog);
			}
			public void onCancel(JDialog dialog) {
				CommonGUI.closeDialog(dialog);
			}
		});
		
	}

	/**
	 * Create the dialog.
	 */
	public ConfirmDialog(String confirmationMsg, ConfirmationListener listener) {
		
		int confirmationResult = JOptionPane.showConfirmDialog(
			    null,
			    confirmationMsg,
			    "Confirmation",
			    JOptionPane.OK_CANCEL_OPTION
			);
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		
		if (confirmationResult == JOptionPane.OK_OPTION) {
		    listener.onConfirm(this);
		} else {
		    listener.onCancel(this);
		}
	}
	

	public void closeDialog() {
		this.dispose();
	}

}
