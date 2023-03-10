package org.syndiate.FPCurate.gui.common;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class CommonGUI {
	
	
	public static void setMenuItemShortcut(JMenuItem component, int specificKey, int keyMask) {
		component.setMnemonic(specificKey);
		component.setDisplayedMnemonicIndex(0);
        component.setAccelerator(KeyStroke.getKeyStroke(specificKey, keyMask));
	}


}
