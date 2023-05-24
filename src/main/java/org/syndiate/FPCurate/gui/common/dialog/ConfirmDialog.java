package org.syndiate.FPCurate.gui.common.dialog;

import java.util.Map;
import javax.swing.JOptionPane;
import org.syndiate.FPCurate.I18N;


public class ConfirmDialog {


	private static final Map<String, String> confirmDialogExStrs = I18N.getStrings("exceptions/dialog/confirm");
	
	
	
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
			new ErrorDialog(new NullPointerException(confirmDialogExStrs.get("nullMessage")));
			return;
		}
		if (listener == null) {
			new ErrorDialog(new NullPointerException(confirmDialogExStrs.get("nullListener")));
			return;
		}
		
		
		int confirmationResult = JOptionPane.showConfirmDialog(
			    null,
			    confirmationMsg,
			    dialogStrs.get("confirmationTitle"),
			    JOptionPane.OK_CANCEL_OPTION
			);
		

		if (confirmationResult == JOptionPane.OK_OPTION) {
		    listener.onConfirm();
		} else {
		    listener.onCancel();
		}
		
	}

}
