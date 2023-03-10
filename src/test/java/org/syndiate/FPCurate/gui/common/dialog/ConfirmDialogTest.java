package org.syndiate.FPCurate.gui.common.dialog;

import org.junit.jupiter.api.Test;

class ConfirmDialogTest {

	@Test
	void genericMsg() {
		new ConfirmDialog("msg");
	}
	
	@Test
	void nullMsg() {
		new ConfirmDialog(null);
	}

}
