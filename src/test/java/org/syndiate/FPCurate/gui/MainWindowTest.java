package org.syndiate.FPCurate.gui;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class MainWindowTest {

	@Test
	void restartTest() throws IOException {
		MainWindow.restartApplication(null);
	}

}
