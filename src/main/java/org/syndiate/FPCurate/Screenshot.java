package org.syndiate.FPCurate;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import org.syndiate.FPCurate.gui.cropper.CropperManager;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser;

public class Screenshot {
	
	
	
	public static void main(String[] args) {
		BufferedImage img = takeScreenshot();
		new CropperManager(img, new File("C:/Test/ss-test.jpeg"));
	}
	
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
	
	
	
	
	public static BufferedImage takeScreenshot() {

		HWND flashWindow = Screenshot.findWindowHandle("Adobe Flash Player");

		if (flashWindow == null) {
			System.out.println(I18N.getStrings("exceptions/curation").get("flashWindowNotFound"));
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
		
//		User32.INSTANCE.GetClientRect(flashWindow, rect);
		
/*
		BufferedImage screenshot = null;
		try {
			System.out.println(rect.left);
			System.out.println(rect.top);
			System.out.println(width);
			System.out.println(height);
			screenshot = new Robot().createScreenCapture(new Rectangle(rect.left + 8, rect.top + 8, Math.abs(width - 15), Math.abs(height - 15)));
		} catch (AWTException e) {
			new ErrorDialog(e);
		}*/
		
		
		
		Map<String, String> screenshotExStrs = I18N.getStrings("exceptions/screenshot");
		// Take a screenshot of the Flash Player projector window
		BufferedImage screenshot = null;
		try {
		    // Get the screen bounds
		    Rectangle screenBounds = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		    Rectangle flashBounds = new Rectangle(rect.left + 8, rect.top + 8, width - 15, height - 15);
		    // Get the visible bounds of the window
		    Rectangle visibleBounds = flashBounds.intersection(screenBounds);

		    // Adjust the visible bounds to exclude overlapping elements like the taskbar
		    Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
		    visibleBounds.y += screenInsets.top;
//		    visibleBounds.height -= (screenInsets.top + screenInsets.bottom);

		    // Check if the visible bounds have valid dimensions
		    if (visibleBounds.width > 0 && visibleBounds.height > 0) {
		        screenshot = new Robot().createScreenCapture(visibleBounds);
		    } else {
		        // Handle the case where the visible bounds have invalid dimensions
		        System.out.println(screenshotExStrs.get("completelyOffScreen"));
		    }
		} catch (AWTException e) {
			System.out.println(screenshotExStrs.get("partiallyOrCompletely"));
//		    new ErrorDialog(e);
		}

		return screenshot;
	}

	
	

}