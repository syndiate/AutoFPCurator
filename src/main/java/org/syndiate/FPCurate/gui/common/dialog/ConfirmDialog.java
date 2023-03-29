package org.syndiate.FPCurate.gui.common.dialog;

import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.syndiate.FPCurate.I18N;
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
			public void onConfirm() {
			}
			public void onCancel() {
			}
		});
		
	}

	/**
	 * Create the dialog.
	 */
	public ConfirmDialog(String confirmationMsg, ConfirmationListener listener) {
		
		
		Map<String, String> dialogStrs = I18N.getStrings("dialog");
		
		if (confirmationMsg == null) {
			new ErrorDialog(new NullPointerException("Confirmation message is null"));
			return;
		}
		if (listener == null) {
			new ErrorDialog(new NullPointerException("Confirmation listener is null"));
			return;
		}
		
		int confirmationResult = JOptionPane.showConfirmDialog(
			    null,
			    confirmationMsg,
			    dialogStrs.get("confirmationTitle"),
			    JOptionPane.OK_CANCEL_OPTION
			);

		
		if (confirmationResult == JOptionPane.OK_OPTION) {
			CommonGUI.closeDialog(this);
		    listener.onConfirm();
		} else {
			CommonGUI.closeDialog(this);
		    listener.onCancel();
		}
		
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
	}
	

	public void closeDialog() {
		this.dispose();
	}

}
