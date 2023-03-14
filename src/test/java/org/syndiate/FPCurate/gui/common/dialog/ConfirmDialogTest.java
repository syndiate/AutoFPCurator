package org.syndiate.FPCurate.gui.common.dialog;

import org.junit.jupiter.api.Test;



class ConfirmDialogTest {

	@Test
	void genericMsg() {
		new ConfirmDialog("msg", new ConfirmationListener() {
			
			public void onConfirm() {
				System.out.println("Test ran on confirm!");
			}
			public void onCancel() {
				System.out.println("Test ran on cancel!");
			}
			
		});
	}
	
	@Test
	void nullMsg() {
		new ConfirmDialog(null, null);
	}

}
