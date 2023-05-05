package org.syndiate.FPCurate.gui.cropper;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


// https://github.com/lewiswhitaker1/ImageCropToSquare/blob/main/src/me/lewis/cropper/Cropper.java
public class Cropper {
	
	
	private static JFrame cropperFrame = null;
	
    public static void main(String[] args) {
    	try {
			new Cropper(new File("C:/Test/image.jpeg"), new File("C:/Test/image-cropped.jpeg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    
    
    
    public Cropper(File file, File saveLocation) throws IOException {
		createGUI(openImage(file), saveLocation);
    }
    public Cropper(BufferedImage image, File saveLocation) {
    	createGUI(image, saveLocation);
    }
    
    
    
    public static void createGUI(BufferedImage image, File saveLocation) {
    	ImageCropper cropper = new ImageCropper(image, saveLocation);
        cropperFrame = new JFrame("Cropper");
        cropperFrame.setName("Cropper");
        cropperFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cropperFrame.setContentPane(cropper);
        cropperFrame.pack();
        cropperFrame.setVisible(true);
        cropperFrame.toFront();
        cropperFrame.requestFocus();
        cropperFrame.setResizable(false);
    }
    
    public static void closeGUI() {
    	if (cropperFrame == null) {
    		return;
    	}
    	cropperFrame.setVisible(false);
    	cropperFrame.dispose();
    	cropperFrame = null;
    }
    
    


    public static BufferedImage openImage(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
//        String fileExtension = CommonMethods.getFileExtension(file);
        int width = img.getWidth();
        int height = img.getHeight();
        
        double aspectRatio = (double) width / height;
        if (width > height && width > 1000) {
            width = 1000;
            height = (int) (width / aspectRatio);
        } else if (height > 1000) {
            height = 1000;
            width = (int) (height * aspectRatio);
        }
        
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage scaledBI = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g = scaledBI.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();
        return scaledBI;
    }
}