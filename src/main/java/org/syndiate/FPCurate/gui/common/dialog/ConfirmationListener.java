package org.syndiate.FPCurate.gui.common.dialog;

import javax.swing.JDialog;

public interface ConfirmationListener {
	void onConfirm(JDialog dialog);
	void onCancel(JDialog dialog);
}