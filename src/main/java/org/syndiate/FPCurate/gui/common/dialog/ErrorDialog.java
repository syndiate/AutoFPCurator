package org.syndiate.FPCurate.gui.common.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class ErrorDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4161909116877642736L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ErrorDialog dialog = new ErrorDialog(new IOException());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ErrorDialog(Exception ex) {
		
		setTitle("An error occurred!");
		setSize(450, 300);
		setLocationRelativeTo(null);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel(ex.getClass().getSimpleName(), JLabel.CENTER);
		lblNewLabel.setBounds(191, 11, lblNewLabel.getPreferredSize().width, lblNewLabel.getPreferredSize().height);
		contentPanel.add(lblNewLabel);
		
		
		StringBuilder sb = new StringBuilder("");
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
		
        JTextArea stackTrace = new JTextArea(sb.toString());
        stackTrace.setEditable(false);
        stackTrace.setLineWrap(true);
        stackTrace.setWrapStyleWord(true);
        stackTrace.setText(sb.toString());
        
        
		JScrollPane scrollPane = new JScrollPane(stackTrace);
		scrollPane.setBounds(10, 38, 414, 179);
		contentPanel.add(scrollPane);
		
		JButton okButton = new JButton("OK");
		okButton.setBounds(194, 228, 63, 23);
		contentPanel.add(okButton);
		okButton.setActionCommand("OK");
		okButton.addActionListener((ActionEvent e) -> {
			dispose();
		});
		getRootPane().setDefaultButton(okButton);
		
		
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		
		this.setVisible(true);
		
		
	}
}
