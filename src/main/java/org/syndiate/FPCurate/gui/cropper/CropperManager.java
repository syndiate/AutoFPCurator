package org.syndiate.FPCurate.gui.cropper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.gui.common.CommonGUI;


// https://github.com/lewiswhitaker1/ImageCropToSquare/blob/main/src/me/lewis/cropper/Cropper.java
public class CropperManager {
	
	
	private static JFrame cropperFrame = null;
	
	
	private static final Map<String, String> commonStrs = I18N.getStrings("common");
	private static final Map<String, String> cropperStrs = I18N.getStrings("cropper");
	
	
	
	
    public static void main(String[] args) {
    	try {
			new CropperManager(new File("C:/Test/image.jpeg"), new File("C:/Test/image-cropped.jpeg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    
    
    
    public CropperManager(File file, File saveLocation) throws IOException {
		createGUI(openImage(file), saveLocation);
    }
    public CropperManager(BufferedImage image, File saveLocation) {
    	createGUI(image, saveLocation);
    }
    
    
    
    
    public static void createGUI(BufferedImage image, File saveLocation) {
    	
    	
    	AtomicReference<ImageCropper> cropper = new AtomicReference<>(new ImageCropper(image, saveLocation));
    	
        cropperFrame = new JFrame("Cropper");
        cropperFrame.setName("Cropper");
        cropperFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cropperFrame.setContentPane(cropper.get());
        cropperFrame.pack();
        cropperFrame.toFront();
        cropperFrame.requestFocus();
        cropperFrame.setResizable(false);
        
        
        
        {
        	
			JMenuBar bar = new JMenuBar();
			bar.setOpaque(true);
			bar.setBackground(Color.WHITE);
			JMenu utils = new JMenu(commonStrs.get("file"));

			
			
			JMenuItem rotateBtn = new JMenuItem(cropperStrs.get("rotateBtn"));
			rotateBtn.addActionListener((ActionEvent e) -> {
				
				BufferedImage rotatedImage = cropper.get().rotateImage(90);
				cropper.set(new ImageCropper(rotatedImage, saveLocation));
				cropperFrame.setContentPane(cropper.get());
				cropperFrame.pack();
				
			});

			
			JMenuItem saveBtn = new JMenuItem(cropperStrs.get("saveBtn"));
			saveBtn.addActionListener((ActionEvent e) -> cropper.get().saveImage());
			
			
			utils.add(rotateBtn);
			utils.add(saveBtn);
			bar.add(utils);
			cropperFrame.setJMenuBar(bar);
			
		}
        
        
        cropperFrame.setVisible(true);
        CommonGUI.setIconImage(cropperFrame, "logo.png");
		
        
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