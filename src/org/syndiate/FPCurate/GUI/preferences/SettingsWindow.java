package org.syndiate.FPCurate.GUI.preferences;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.syndiate.FPCurate.GUI.preferences.menus.Paths;

public class SettingsWindow extends JFrame {

    public SettingsWindow() {
        this.setTitle("Settings");
        this.setSize(960, 540);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new GridLayout(2, 2));
        generalPanel.add(new JLabel("Setting 1:"));
        generalPanel.add(new JTextField());
        generalPanel.add(new JLabel("Setting 2:"));
        generalPanel.add(new JTextField());

        JPanel advancedPanel = new JPanel();
        advancedPanel.setLayout(new GridLayout(2, 2));
        advancedPanel.add(new JLabel("Setting 3:"));
        advancedPanel.add(new JTextField());
        advancedPanel.add(new JLabel("Setting 4:"));
        advancedPanel.add(new JTextField());

        tabbedPane.addTab("Paths", new Paths());
        tabbedPane.addTab("General", generalPanel);
        tabbedPane.addTab("Advanced", advancedPanel);

        this.add(tabbedPane, BorderLayout.NORTH);
/*
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 2));
        bottomPanel.add(new JLabel("Setting 5:"));
        bottomPanel.add(new JTextField());
        bottomPanel.add(new JLabel("Setting 6:"));
        bottomPanel.add(new JTextField());*/

//        add(bottomPanel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    public static void main(String[] args) {
        new SettingsWindow();
    }
}