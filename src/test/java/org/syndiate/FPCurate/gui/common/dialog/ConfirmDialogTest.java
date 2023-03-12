package org.syndiate.FPCurate.gui.common.dialog;

import javax.swing.JDialog;

import org.junit.jupiter.api.Test;
import org.syndiate.FPCurate.gui.common.CommonGUI;

class ConfirmDialogTest {

	@Test
	void genericMsg() {
		new ConfirmDialog("msg", new ConfirmationListener() {
			
			public void onConfirm(JDialog dialog) {
				System.out.println("Test ran on confirm!");
				CommonGUI.closeDialog(dialog);
			}
			public void onCancel(JDialog dialog) {
				System.out.println("Test ran on cancel!");
				CommonGUI.closeDialog(dialog);
			}
			
		});
	}
	
	@Test
	void nullMsg() {
		new ConfirmDialog(null, null);
	}

}
