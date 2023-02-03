package me.pro2027.main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class SecurityCamMgr {

	public static SecurityCam securityCam = null;
	public static Webcam webcam = null;
	//public static JLabel infoLabel = new JLabel("Info Placeholder");
	public static JFrame window = new JFrame("Security Camera Monitor");
	
	public static void takeScreenshot() {
		new File("screenshots").mkdir();
		String day = "screenshots/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date());
		String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		new File(day).mkdir();
		File saveFile = new File(day+"/"+time+".png");
		BufferedImage image = webcam.getImage();

		// save image to PNG file
		try {
			ImageIO.write(image, "PNG", saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		Dimension Dimension4K = new Dimension(3264,2448);//new Dimension(3840, 2448);
		Dimension size = WebcamResolution.FHD.getSize();//Dimension4K;//
		webcam = Webcam.getDefault();
		Dimension[] nonStandardResolutions = new Dimension[] {
				//WebcamResolution.FHD.getSize(),
				//Dimension4K//almost UDH4K
				size
			};
		webcam.setCustomViewSizes(nonStandardResolutions);
		webcam.setViewSize(size);
		
		
		new Thread() {
			public void run() {
				securityCam = new SecurityCam(webcam, size);
			}
		}.start();
		
		//Thread.sleep(3000);//wait so securityCam is not null, hackey though
		
		//System.out.println("Displaying cam: "+webcam.open());

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		//panel.setMirrored(true);
		
		JButton button = new JButton("Take Screenshot");
		button.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  takeScreenshot();
			  } 
			} );
		
		panel.add(button);
		window.add(panel);
		//window.add(infoLabel);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
		
		/*new Thread() {
			public void run() {
				while (true) {
					if (securityCam.isRecording()) {
						window.setTitle("Recording to `"+securityCam.getFileName()+"`");
					} else {
						window.setTitle("Waiting for movement...");
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();*/
	}

}
