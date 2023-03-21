package org.syndiate.FPCurate;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import com.sun.jna.platform.win32.WinDef.HWND;

class ScreenshotTest {

	@Test
	void findFlashWindow() {
		assertInstanceOf(HWND.class, Screenshot.findWindowHandle("Adobe Flash Player 32"));
	}
	
	@Test
	void takeSS() {
		assertInstanceOf(BufferedImage.class, Screenshot.takeScreenshot());
	}

}
