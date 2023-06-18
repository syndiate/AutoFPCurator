package org.syndiate.FPCurate.gui.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.syndiate.FPCurate.CommonMethods;
import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;

public class CommonGUI {
	
	
	public static void setMenuItemShortcut(JMenuItem component, int specificKey, int keyMask) {
		component.setMnemonic(specificKey);
		component.setDisplayedMnemonicIndex(0);
        component.setAccelerator(KeyStroke.getKeyStroke(specificKey, keyMask));
	}
	
	
	public static void closeDialog(JDialog dialog) {
		dialog.dispose();
	}
	
	
	
	public static void setIconImage(JFrame frame, String iconPath) {
		try {
			frame.setIconImage(ImageIO.read(new ByteArrayInputStream(CommonMethods.getResourceByte(iconPath))));
		} catch (IOException e) {
			new ErrorDialog(e);
		}
	}


}
