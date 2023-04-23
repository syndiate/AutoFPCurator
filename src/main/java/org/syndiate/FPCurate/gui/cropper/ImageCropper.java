package org.syndiate.FPCurate.gui.cropper;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

import org.syndiate.FPCurate.CommonMethods;
import org.syndiate.FPCurate.gui.common.dialog.GenericDialog;

//https://github.com/lewiswhitaker1/ImageCropToSquare/blob/main/src/me/lewis/cropper/ImageCropper.java

// i replaced the mouse listeners from lewiswhitaker's imgae cropper with the listeners from this code: https://github.com/Wandonium/ImageCropper/blob/master/src/main/java/com/wandonium/imagecropper/DrawOnImage.java
// i was originally going to use https://github.com/imgeself/JavaFX-ImageCropper/ but trying to get javafx working in my project was too much of a pain

// TODO: resizable and draggable selection rectangle
public class ImageCropper extends JPanel implements MouseListener, MouseMotionListener {

	
	
	
	private static final long serialVersionUID = 4951011590900776611L;
	private BufferedImage image;
	private Rectangle cropBox;
	private JButton saveButton;
	private JButton rotateButton;
	private Point startPoint;
	private boolean mouseInWindow = true;
	private File saveLocation;
	
	
	

	public ImageCropper(BufferedImage image, File saveLocation) {
		this.image = image;
		this.saveLocation = saveLocation;

		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		setOpaque(true);
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(this);

		rotateButton = new JButton("Rotate");
		rotateButton.addActionListener((ActionEvent e) -> {

			BufferedImage rotatedImage = rotateImage(image, 90);
			ImageCropper cropper = new ImageCropper(rotatedImage, saveLocation);

			Window[] windows = Window.getWindows();
			for (Window window : windows) {
				if (window instanceof JFrame) {
					JFrame frame = (JFrame) window;
					if (frame.getName().equalsIgnoreCase("cropper")) {
						frame.setContentPane(cropper);
						frame.pack();
						repaint();
					}
				}
			}
		});
		saveButton = new JButton("Save");
		saveButton.addActionListener((ActionEvent e) -> saveImage());

		buttonDesign(saveButton);
		buttonDesign(rotateButton);
		add(saveButton);
		add(rotateButton);
	}
	
	
	
	
	
	

	private BufferedImage rotateImage(BufferedImage image, double angle) {
		double radians = Math.toRadians(angle);
		double sin = Math.abs(Math.sin(radians));
		double cos = Math.abs(Math.cos(radians));
		
		int w = image.getWidth();
		int h = image.getHeight();
		int newWidth = (int) Math.floor(w * cos + h * sin);
		int newHeight = (int) Math.floor(h * cos + w * sin);
		
		BufferedImage rotated = new BufferedImage(newWidth, newHeight, image.getType());
		Graphics2D g2d = rotated.createGraphics();
		
		AffineTransform at = new AffineTransform();
		at.translate((newWidth - w) / 2, (newHeight - h) / 2);
		at.rotate(radians, w / 2, h / 2);
		
		g2d.setTransform(at);
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		
		return rotated;
	}
	
	
	
	public void saveImage() {

		if (cropBox == null) {
			System.out.println("No crop box selected");
			return;
		}

		try {
			BufferedImage croppedImage = image.getSubimage(cropBox.x, cropBox.y, cropBox.width, cropBox.height);
			ImageIO.write(croppedImage, CommonMethods.getFileExtension(saveLocation), saveLocation);
//			System.out.println("Image saved: " + saveLocation.getAbsolutePath());
			Cropper.closeGUI();
		} catch (RasterFormatException e) {
			new GenericDialog("Invalid crop box.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	
	

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null) {
			return;
		}

		g.drawImage(image, 0, 0, this);

		if (cropBox == null) {
			return;
		}
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(0, 0, cropBox.x, getHeight());
		g.fillRect(cropBox.x + cropBox.width, 0, getWidth() - cropBox.x - cropBox.width, getHeight());
		g.fillRect(cropBox.x, 0, cropBox.width, cropBox.y);
		g.fillRect(cropBox.x, cropBox.y + cropBox.height, cropBox.width, getHeight() - cropBox.y - cropBox.height);

		g.setColor(Color.WHITE);
		g.drawRect(cropBox.x, cropBox.y, cropBox.width, cropBox.height);
	}
	
	
	
	

	
	

	public static void buttonDesign(JButton button) {
		button.setFont(new Font("Arial", Font.BOLD, 15));
		button.setOpaque(true);
		button.setBackground(Color.WHITE);
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);
	}

	
	
	
	
	
	
	
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		this.startPoint = e.getPoint();

		if (cropBox == null) {
			cropBox = new Rectangle(x, y, 0, 0);
		} else {
			cropBox.setRect(x, y, 0, 0);
		}

		repaint();
	}

	
	public void mouseDragged(MouseEvent e) {
		
		if (!mouseInWindow) {
			return;
		}
		int x = Math.min(startPoint.x, e.getX());
		int y = Math.min(startPoint.y, e.getY());
		int width = Math.abs(startPoint.x - e.getX());
		int height = Math.abs(startPoint.y - e.getY());

		cropBox.setBounds(x, y, width, height);
		repaint();
	}
	
	public void mouseEntered(MouseEvent e) {
		mouseInWindow = true;
	}
	public void mouseExited(MouseEvent e) {
		mouseInWindow = false;
	}

	
	
	// boilerplate methods
	public void mouseReleased(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
}