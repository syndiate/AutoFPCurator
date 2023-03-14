package org.syndiate.FPCurate.gui.common.dialog;

import java.util.Map;

import javax.swing.JOptionPane;

import org.syndiate.FPCurate.I18N;

public class GenericDialog {
	
	public GenericDialog(String msg) {
		Map<String, String> dialogStrs = I18N.getStrings("dialog");
		JOptionPane.showMessageDialog(null, msg, dialogStrs.get("genericTitle"), JOptionPane.INFORMATION_MESSAGE);
	}

}
