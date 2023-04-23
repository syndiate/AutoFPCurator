package org.syndiate.FPCurate.gui.common.dialog;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class GenericDialogTest {

	@Before
	void createDialog(String msg) throws InterruptedException {
		new GenericDialog(msg);
		Thread.sleep(4000);
	}

	@Test
	void stringTest() throws InterruptedException {
		createDialog("String test");
	}
	
	@Test
	void nullTest() throws InterruptedException {
		createDialog(null);
	}

}
