package org.syndiate.FPCurate.gui.settings;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentChangeListener implements DocumentListener {
	
	@Override
    public void insertUpdate(DocumentEvent e) {
        update(e);
    }
    @Override
    public void removeUpdate(DocumentEvent e) {
        update(e);
    }
    @Override
    public void changedUpdate(DocumentEvent e) {
        update(e);
    }
    
    public abstract void update(DocumentEvent e);

}
