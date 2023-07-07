package org.syndiate.FPCurate.gui.cropper;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.syndiate.FPCurate.CommonMethods;
import org.syndiate.FPCurate.I18N;
import org.syndiate.FPCurate.gui.common.dialog.GenericDialog;

//https://github.com/lewiswhitaker1/ImageCropToSquare/blob/main/src/me/lewis/cropper/ImageCropper.java

// i replaced the mouse listeners from lewiswhitaker's imgae cropper with the listeners from this code: https://github.com/Wandonium/ImageCropper/blob/master/src/main/java/com/wandonium/imagecropper/DrawOnImage.java
// i was originally going to use https://github.com/imgeself/JavaFX-ImageCropper/ but trying to get javafx working in my project was too much of a pain

public class ImageCropper extends JPanel implements MouseListener, MouseMotionListener {

	
	
	
	private static final long serialVersionUID = 4951011590900776611L;
	private BufferedImage image;
//	private JButton saveButton, rotateButton;
	
	private Rectangle cropBox;
	private Point startPoint, draggingPoint;
	
	private boolean mouseInWindow = true;
	private boolean isDragging = false;
	private boolean isResizing = false;
	private String currentResizerHandle = "";
	
	private File saveLocation;
	
	
	private final Map<String, Rectangle> resizerHandles = new HashMap<>();
	private final Map<String, MouseMotionListener> resizerHandleListeners = new HashMap<>();
	
	@SuppressWarnings("unused")
	private final Map<String, String> cropperStrs = I18N.getStrings("cropper");
	private final Map<String, String> cropperExStrs = I18N.getStrings("exceptions/cropper");
	
	
	private final Color cropBoxColor = Color.WHITE;
	private final Color resizerSquareColor = Color.BLACK;
	private final int resizerHandleSize = 10;
	
	
	
	
	
	

	public ImageCropper(BufferedImage image, File saveLocation) {
		this.image = image;
		this.saveLocation = saveLocation;

		
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		setOpaque(true);
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		
/*
		rotateButton = new JButton(cropperStrs.get("rotateBtn"));
		rotateButton.addActionListener((ActionEvent e) -> {

			
		});
		
		
		saveButton = new JButton(cropperStrs.get("saveBtn"));
		saveButton.addActionListener((ActionEvent e) -> saveImage());

		buttonDesign(saveButton);
		buttonDesign(rotateButton);*/
//		add(saveButton);
//		add(rotateButton);
		
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
	
	
	public BufferedImage rotateImage(double angle) {
		return rotateImage(image, angle);
	}
	
	
	
	
	
	public void rotateImageGUI() {
		BufferedImage rotatedImage = rotateImage(image, 90);
		ImageCropper cropper = new ImageCropper(rotatedImage, saveLocation);

		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if (!(window instanceof JFrame)) {
				continue;
			}
			JFrame frame = (JFrame) window;
			if (frame.getName().equalsIgnoreCase("cropper")) {
				frame.setContentPane(cropper);
				frame.pack();
				repaint();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void saveImage() {

		if (cropBox == null) {
			new GenericDialog(cropperExStrs.get("noCropBox"));
			return;
		}
		

		try {
			
			BufferedImage croppedImage = image.getSubimage(cropBox.x, cropBox.y, cropBox.width, cropBox.height);
			ImageIO.write(croppedImage, CommonMethods.getFileExtension(saveLocation), saveLocation);
//			System.out.println("Image saved: " + saveLocation.getAbsolutePath());
			CropperManager.closeGUI();
			
		} catch (RasterFormatException e) {
			new GenericDialog(cropperExStrs.get("invalidCropBox"));
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

		g.setColor(cropBoxColor);
		g.drawRect(cropBox.x, cropBox.y, cropBox.width, cropBox.height);
		
		
	    g.setColor(resizerSquareColor);
	    
	    makeNWResizerSquare();
	    makeNCResizerSquare();
	    makeNEResizerSquare();
	    makeCWResizerSquare();
	    makeCEResizerSquare();
	    makeSWResizerSquare();
	    makeSCResizerSquare();
	    makeSEResizerSquare();
	    
	    for (Rectangle handle : resizerHandles.values()) {
	    	g.fillRect(handle.x, handle.y, resizerHandleSize, resizerHandleSize);
	    }
	}
	

	public static void buttonDesign(JButton button) {
		button.setFont(new Font("Arial", Font.BOLD, 15));
		button.setOpaque(true);
		button.setBackground(Color.WHITE);
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);
	}

	
	
	
	
	
	
	
	public void mousePressed(MouseEvent e) {
		
		if (cropBox != null) {
			
			if (cropBox.contains(e.getPoint())) {
				isDragging = true;
				draggingPoint = e.getPoint();
				return;
			}
			for (String handleLoc : resizerHandles.keySet()) {
				Rectangle handle = resizerHandles.get(handleLoc);
				if (!handle.contains(e.getPoint())) {
					continue;
				}
				currentResizerHandle = handleLoc;
				isResizing = true;
				return;
			}
			
		}
		
		isResizing = false;
		isDragging = false;
		
		int x = e.getX();
		int y = e.getY();
		this.startPoint = e.getPoint();
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		if (cropBox == null) {
			cropBox = new Rectangle(x, y, 0, 0);
		} else {
			cropBox.setRect(x, y, 0, 0);
		}

		revalidate();
		repaint();
	}
	

	
	
	public void mouseDragged(MouseEvent e) {
		
		if (!mouseInWindow) {
			return;
		}
		
		
		if (isResizing) {
			resizerHandleListeners.get(currentResizerHandle).mouseDragged(e);
			revalidate();
			repaint();
			return;
		}

		
		if (isDragging) {
			
			double offsetX = e.getX() - draggingPoint.getX();
			double offsetY = e.getY() - draggingPoint.getY();
			int newX = (int) (cropBox.getX() + offsetX);
	        int newY = (int) (cropBox.getY() + offsetY);
			
            if (newX >= 0 && newX + cropBox.getWidth() <= getBounds().getWidth()) {
                cropBox.x = newX;
            }
            if (newY >= 0 && newY + cropBox.getHeight() <= getBounds().getHeight()) {
                cropBox.y = newY;
            }
            draggingPoint = e.getPoint();
            
			cropBox.setLocation(newX, newY);
			revalidate();
			repaint();
			return;
			
		}
		
		
		int x = Math.min(startPoint.x, e.getX());
		int y = Math.min(startPoint.y, e.getY());
		int width = Math.abs(startPoint.x - e.getX());
		int height = Math.abs(startPoint.y - e.getY());

		cropBox.setBounds(x, y, width, height);
		revalidate();
		repaint();
	}
	

	
	
	public void mouseMoved(MouseEvent e) {
		if (cropBox == null) {
			return;
		}
		
		for (String handleLoc : resizerHandles.keySet()) {
			
			Rectangle handle = resizerHandles.get(handleLoc);
			if (!handle.contains(e.getPoint())) {
				continue;
			}
			
			switch(handleLoc) {
				case "nw":
					setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					break;
				case "nc":
					setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					break;
				case "ne":
					setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					break;
				case "cw":
					setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					break;
				case "ce":
					setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					break;
				case "sw":
					setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					break;
				case "sc":
					setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
					break;
				case "se":
					setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					break;
			}
			currentResizerHandle = handleLoc;
			isResizing = true;
			return;
		}
		isResizing = false;
		
		
		
		if (cropBox.contains(e.getPoint())) {
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	
	
	
	public void mouseEntered(MouseEvent e) {
		mouseInWindow = true;
	}
	public void mouseExited(MouseEvent e) {
		mouseInWindow = false;
	}
	public void mouseReleased(MouseEvent e) {
		isDragging = false;
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	private void makeNWResizerSquare() {
		
        Rectangle squareNW = new Rectangle(resizerHandleSize, resizerHandleSize);
        squareNW.x = (int) (cropBox.getX() - (squareNW.getWidth() / 2));
        squareNW.y = (int) (cropBox.getY() - (squareNW.getHeight() / 2));
        
        
        resizerHandles.put("nw", squareNW);
        resizerHandleListeners.put("nw", new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				
	            double offsetX = e.getX() - cropBox.getX();
	            double offsetY = e.getY() - cropBox.getY();
	            int newX = (int) (cropBox.getX() + offsetX) ;
	            int newY = (int) (cropBox.getY() + offsetY) ;

	            if (newX >= 0 && newX <= cropBox.getX() + cropBox.getWidth() ) {
	                cropBox.x = newX;
	                cropBox.width = (int) (cropBox.getWidth() - offsetX);
	            }

	            if (newY >= 0 && newY <= cropBox.getY() + cropBox.getHeight() ) {
	                cropBox.y = newY;
	                cropBox.height = (int) (cropBox.getHeight() - offsetY);
	            }
	            
			}
			@Override
			public void mouseMoved(MouseEvent e) {}
        });
            
    }
	
	
	
	private void makeNCResizerSquare() {
		
		Rectangle squareNC = new Rectangle(resizerHandleSize, resizerHandleSize);
		squareNC.x = (int) (cropBox.getX() + ((cropBox.getWidth() / 2) - (squareNC.getWidth() / 2)));
	    squareNC.y = (int) (cropBox.getY() - (squareNC.getHeight() / 2));
	    
	    
	    resizerHandles.put("nc", squareNC);
        resizerHandleListeners.put("nc", new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent ev) {
				
				double offsetY = ev.getY() - cropBox.getY();
	            double newY = cropBox.getY() + offsetY;

	            if (newY >= 0 && newY <= cropBox.getY() + cropBox.getHeight()) {
	                cropBox.y = (int) newY;
	                cropBox.height = (int) (cropBox.getHeight() - offsetY);
	            }
	            
			}
			@Override
			public void mouseMoved(MouseEvent e) {}
        });
	}


	
	
	private void makeNEResizerSquare() {
		
		Rectangle squareNE = new Rectangle(resizerHandleSize, resizerHandleSize);
		squareNE.x = (int) (cropBox.getX() + (cropBox.getWidth() - (squareNE.getWidth() / 2)));
	    squareNE.y = (int) (cropBox.getY() - (squareNE.getHeight() / 2));
	    
	    
	    resizerHandles.put("ne", squareNE);
        resizerHandleListeners.put("ne", new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				
	            double offsetX = e.getX() - cropBox.getX();
	            double offsetY = e.getY() - cropBox.getY();
	            int newY = (int) (cropBox.getY() + offsetY) ;

	            if (offsetX >= 0 && offsetX <= cropBox.getX() + cropBox.getWidth() - 5) {
	                cropBox.width = (int) offsetX;
	            }

	            if (newY >= 0 && newY <= cropBox.getY() + cropBox.getHeight() - 5) {
	                cropBox.y = newY;
	                cropBox.height = (int) (cropBox.getHeight() - offsetY);
	            }
	            
			}
			@Override
			public void mouseMoved(MouseEvent e) {}
        });
	}
	
	
	
	
	private void makeCWResizerSquare() {
		
		Rectangle squareCW = new Rectangle(resizerHandleSize, resizerHandleSize);
		squareCW.x = (int) (cropBox.getX() - (squareCW.getWidth() / 2));
		squareCW.y = (int) (cropBox.getY() + ((cropBox.getHeight() / 2) - (squareCW.getHeight() / 2)));
		
		
		resizerHandles.put("cw", squareCW);
		resizerHandleListeners.put("cw", new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent ev) {
				
				double offsetX = ev.getX() - cropBox.getX();
	            double newX = cropBox.getX() + offsetX;

	            if (newX >= 0 && newX <= cropBox.getX() + cropBox.getWidth() - 5) {
	                cropBox.x = (int) newX;
	                cropBox.width = (int) (cropBox.getWidth() - offsetX);
	            }
	            
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
		});
	}
	
	
	
	
	private void makeCEResizerSquare() {
		
		Rectangle squareCE = new Rectangle(resizerHandleSize, resizerHandleSize);
		squareCE.x = (int) (cropBox.getX() + cropBox.getWidth() - (squareCE.getWidth() / 2));
		squareCE.y = (int) (cropBox.getY() + ((cropBox.getHeight() / 2) - (squareCE.getHeight() / 2)));
		
		resizerHandles.put("ce", squareCE);
		resizerHandleListeners.put("ce", new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent ev) {
				
				int offsetX = (int) (ev.getX() - cropBox.getX());
	            if (offsetX >= 0 && offsetX <= cropBox.getX() + cropBox.getWidth() - 5) {
	                cropBox.width = offsetX;
	            }
	            
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
		});
	}
	
	
	
	
	private void makeSWResizerSquare() {
		
		Rectangle squareSW = new Rectangle(resizerHandleSize, resizerHandleSize);
		squareSW.x = (int) (cropBox.getX() - (squareSW.getWidth() / 2));
		squareSW.y = (int) (cropBox.getY() + (cropBox.getHeight() - (squareSW.getHeight() / 2)));
		
		resizerHandles.put("sw", squareSW);
		resizerHandleListeners.put("sw", new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent ev) {
				
				 double offsetX = ev.getX() - cropBox.getX();
		            double offsetY = ev.getY() - cropBox.getY();
		            double newX = cropBox.getX() + offsetX;

		            if (newX >= 0 && newX <= cropBox.getX() + cropBox.getWidth() - 5) {
		                cropBox.x = (int) newX;
		                cropBox.width = (int) (cropBox.getWidth() - offsetX);
		            }

		            if (offsetY >= 0 && offsetY <= cropBox.getY() + cropBox.getHeight() - 5) {
		                cropBox.height = (int) offsetY;
		            }
		            
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
			
		});
	}
	
	
	
	
	private void makeSCResizerSquare() {
		
		Rectangle squareSC = new Rectangle(resizerHandleSize, resizerHandleSize);
		squareSC.x = (int) (cropBox.getX() + ((cropBox.getWidth() / 2)  - (squareSC.getWidth() / 2)));
		squareSC.y = (int) (cropBox.getY() + (cropBox.getHeight() - (squareSC.getHeight() / 2)));
		
		resizerHandles.put("sc", squareSC);
		resizerHandleListeners.put("sc", new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent ev) {
				double offsetY = ev.getY() - cropBox.getY();

	            if (offsetY >= 0 && offsetY <= cropBox.getY() + cropBox.getHeight() - 5) {
	                cropBox.height = (int) offsetY;
	            }
			}
			@Override
			public void mouseMoved(MouseEvent e) {}
		});
	}
	
	
	
	private void makeSEResizerSquare() {
		
		Rectangle squareSE = new Rectangle(resizerHandleSize, resizerHandleSize);
		squareSE.x = (int) (cropBox.getX() + cropBox.getWidth() - (squareSE.getWidth() / 2));
		squareSE.y = (int) (cropBox.getY() + (cropBox.getHeight() - (squareSE.getHeight() / 2)));
		
		resizerHandles.put("se", squareSE);
		resizerHandleListeners.put("se", new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent ev) {
				double offsetX = ev.getX() - cropBox.getX();
	            double offsetY = ev.getY() - cropBox.getY();

	            if (offsetX >= 0 && offsetX <= cropBox.getX() + cropBox.getWidth() - 5) {
	                cropBox.width = (int) offsetX;
	            }

	            if (offsetY >= 0 && offsetY <= cropBox.getY() + cropBox.getHeight() - 5) {
	                cropBox.height = (int) offsetY;
	            }
			}
			@Override
			public void mouseMoved(MouseEvent e) {}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// boilerplate methods
	public void mouseClicked(MouseEvent e) {}
}