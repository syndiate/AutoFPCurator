package org.syndiate.FPCurate;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.User32Util;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinUser;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;

@SuppressWarnings("unused")
public class Screenshot {
	
	
	
	public static HWND findWindowHandle(String windowHandle) {
		
		
        // Find any given window
        final HWND[] windowHandles = new HWND[1];
        
		User32.INSTANCE.EnumWindows((HWND hWnd, Pointer data) -> {
			
			char[] windowText = new char[512];
			User32.INSTANCE.GetWindowText(hWnd, windowText, 512);
			String wText = Native.toString(windowText);

			
			if (wText.contains(windowHandle)) {
				windowHandles[0] = hWnd;
				return false;
			}
			return true;
			
		}, null);
		
        
        return windowHandles[0];
        
    }
	
	
	// TODO: account for extraneous flash player window locations
	public static BufferedImage takeScreenshot() {

		HWND flashWindow = Screenshot.findWindowHandle("Adobe Flash Player");

		if (flashWindow == null) {
			System.out.println("Flash Player window not found");
			return null;
		}

		User32.INSTANCE.SetForegroundWindow(flashWindow);
		// Get the dimensions of the Flash Player projector window
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(flashWindow, rect);
		int titleBarHeight = User32.INSTANCE.GetSystemMetrics(WinUser.SM_CYCAPTION);
		int tabBarHeight = User32.INSTANCE.GetSystemMetrics(WinUser.SM_CYMENU);
		rect.top += titleBarHeight + tabBarHeight; // adjust top position to exclude title bar and tab bar
		int width = rect.right - rect.left;
		int height = rect.bottom - rect.top;
		

		// Take a screenshot of the Flash Player projector window
		// TODO: ADJUST THIS FOR DIFFERENT WINDOW POSITIONS, LOCATIONS, AND SIZES
		BufferedImage screenshot = null;
		try {
			screenshot = new Robot().createScreenCapture(new Rectangle(rect.left + 8, rect.top + 8, width - 15, height - 15));
		} catch (AWTException e) {
			new ErrorDialog(e);
		}

		return screenshot;
	}

	
	

}