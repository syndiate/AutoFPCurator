package org.syndiate.FPCurate.gui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CustomConsole extends JFrame implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7579518087254848342L;
	private JTextField inputField;
    private JTextArea outputArea;
    private JScrollPane textAreaWrapper;

    public CustomConsole() {
        // Create the input field and add an action listener to capture user input
        inputField = new JTextField();
        inputField.addActionListener(this);

        // Create the output area and redirect the console output to it
        outputArea = new JTextArea();
        this.textAreaWrapper = new JScrollPane(outputArea);
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                outputArea.append(String.valueOf((char) b));
            }
        }));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle user input by writing to the console output
        String input = inputField.getText();
        System.out.println(input);

        // Clear the input field
        inputField.setText("");
    }
    
    public JScrollPane getAreaWrapper() {
    	return this.textAreaWrapper;
    }

    public static void main(String[] args) {
        new CustomConsole();
    }
}