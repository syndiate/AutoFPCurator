package org.syndiate.FPCurate;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LayoutSwitchingExample {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel contentPanel = new JPanel(new BorderLayout());
        JPanel cardPanel = new JPanel(new CardLayout());

        // Create a panel with a GridLayout
        JPanel gridPanel = new JPanel(new GridLayout(3, 3));
        for (int i = 1; i <= 9; i++) {
            gridPanel.add(new JButton("Button " + i));
        }
        cardPanel.add(gridPanel, "grid");

        // Create a panel with a FlowLayout
        JPanel flowPanel = new JPanel(new FlowLayout());
        for (int i = 1; i <= 5; i++) {
            flowPanel.add(new JButton("Button " + i));
        }
        cardPanel.add(flowPanel, "flow");

        // Create a panel with a BorderLayout
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(new JLabel("North"), BorderLayout.NORTH);
        borderPanel.add(new JLabel("South"), BorderLayout.SOUTH);
        borderPanel.add(new JLabel("East"), BorderLayout.EAST);
        borderPanel.add(new JLabel("West"), BorderLayout.WEST);
        borderPanel.add(new JLabel("Center"), BorderLayout.CENTER);
        cardPanel.add(borderPanel, "border");

        // Create a panel with buttons to switch between cards
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
        JButton gridButton = new JButton("Grid");
        gridButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "grid");
        });
        JButton flowButton = new JButton("Flow");
        flowButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "flow");
        });
        JButton borderButton = new JButton("Border");
        borderButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "border");
        });
        buttonPanel.add(gridButton);
        buttonPanel.add(flowButton);
        buttonPanel.add(borderButton);

        // Add the card panel and button panel to the content panel
        contentPanel.add(cardPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);

        // Set up the frame
        frame.setContentPane(contentPanel);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}