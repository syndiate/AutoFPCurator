package org.syndiate.FPCurate.gui;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.syndiate.FPCurate.gui.main.MainWindow;

class MainWindowTest {

	@Test
	void restartTest() throws IOException {
		MainWindow.restartApplication(null);
	}

}
