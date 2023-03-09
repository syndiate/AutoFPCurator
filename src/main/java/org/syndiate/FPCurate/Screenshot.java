package org.syndiate.FPCurate;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Screenshot {
	
	
  public Window findWindow(String windowName) {
	  
	  Window window = null;
	  Window[] windows = Window.getWindows();
	  
	  for (Window w : windows) {
		  
		  if (!w.getName().equals(windowName)) continue;
	      window = w;
	      break;
	    
	  }
	  
	  if (window != null) {
	      return window;
	  } else {
	      return null;
	  }
	
  }
	
  public Screenshot() {

	  
	  Window flashWindow = findWindow("Adobe Flash Player 32");
	  Rectangle windowRect = flashWindow.getBounds();
	      
	  Robot robot;
	  try {
		  robot = new Robot();
		  BufferedImage image = robot.createScreenCapture(windowRect);
		  ImageIO.write(image, "png", new File(System.getenv("TEMP") + "/ss.png"));
		
	  } catch (AWTException | IOException e) {
		  e.printStackTrace();
	  }
	  
	  
  }
  
  
  
}