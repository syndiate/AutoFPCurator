package org.syndiate.FPCurate;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.syndiate.FPCurate.gui.common.dialog.ErrorDialog;

public class ImageCropperGUI extends JFrame implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4175029952479673704L;
	private JPanel mainPanel;
    private JLabel imageLabel;
    private JButton openButton, cropButton;
    private JTextField xField, yField, widthField, heightField;
    private BufferedImage originalImage, croppedImage;

    public ImageCropperGUI() {

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        imageLabel = new JLabel();
        mainPanel.add(imageLabel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        openButton = new JButton("Open Image");
        openButton.addActionListener(this);
        controlPanel.add(openButton);

        cropButton = new JButton("Crop Image");
        cropButton.addActionListener(this);
        controlPanel.add(cropButton);

        xField = new JTextField(4);
        controlPanel.add(new JLabel("X:"));
        controlPanel.add(xField);

        yField = new JTextField(4);
        controlPanel.add(new JLabel("Y:"));
        controlPanel.add(yField);

        widthField = new JTextField(4);
        controlPanel.add(new JLabel("Width:"));
        controlPanel.add(widthField);

        heightField = new JTextField(4);
        controlPanel.add(new JLabel("Height:"));
        controlPanel.add(heightField);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        setTitle("Image Cropper");
        setSize(800, 600);
    }

    public static void main(String[] args) {
        ImageCropperGUI gui = new ImageCropperGUI();

        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    originalImage = ImageIO.read(fileChooser.getSelectedFile());

                    // Display the loaded image in the imageLabel
                    imageLabel.setIcon(new ImageIcon(originalImage));
                } catch (Exception ex) {
                    new ErrorDialog(ex);
                }
            }
        } else if (e.getSource() == cropButton) {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                croppedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                Graphics2D g2d = croppedImage.createGraphics();

                // Draw the selected portion of the original image onto the cropped image
                g2d.drawImage(originalImage, 0, 0, width, height, x, y, x + width, y + height, null);
                g2d.dispose();

                // Display the cropped image in the imageLabel
                imageLabel.setIcon(new ImageIcon(croppedImage));
            } catch (Exception ex) {
                new ErrorDialog(ex);
            }
        }
    }
    
    
}
