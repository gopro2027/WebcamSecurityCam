package me.pro2027.main;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;


/**
 * The goal of this example is to demonstrate the idea behind detecting that
 * motion has stopped.
 * 
 * @author Bartosz Firyn (sarxos)
 */
public class SecurityCam {
	
	boolean recording = false;
	File saveFile = new File("temporary.mp4");
	
	public boolean isRecording() {
		return recording;
	}
	public String getFileName() {
		return saveFile.getName();
	}
	
	SecurityCam(Webcam webcam, Dimension size) {
		
		
		//webcam.setViewSize(size);
		//webcam.open();
		
		
		MyMotion motion = new MyMotion(webcam);
		
		
		new File("recordings").mkdir();
		
		IMediaWriter writer = ToolFactory.makeWriter(saveFile.getName());
		long start = System.currentTimeMillis();
		boolean keyFrame = false;
		
		while (true) {
			boolean inmotion = motion.isInMotion();
			//System.out.println("In motion: "+inmotion);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (inmotion) {
				if (recording == false) {
					System.out.println("Starting recording...");
					recording = true;
					//start recording
					String day = "recordings/"+new SimpleDateFormat("yyyy.MM.dd").format(new Date());
					String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
					new File(day).mkdir();
					saveFile = new File(day+"/"+time+".mp4");
					writer = ToolFactory.makeWriter(saveFile.getAbsolutePath());
					writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4/*ICodec.ID.CODEC_ID_H264*/, size.width, size.height);
					start = System.currentTimeMillis();
					keyFrame = true;
				}
			} else {
				//inmotion == false
				if (recording == true) {
					recording = false;
					//stop recording
					writer.close();
			        System.out.println("Video recorded to the file: " + saveFile.getAbsolutePath());
				}
			}
			if (recording == true) {
				BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
	            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
	            
	            IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
	            frame.setKeyFrame(keyFrame);
	            keyFrame = false;
	            frame.setQuality(100);
	            
	            writer.encodeVideo(0, frame);
	            
	            if (System.currentTimeMillis() - start > 60 * 1000) {
	            	//save file, start continuation file
	            	recording = false;
	            	writer.close();
			        System.out.println("Video cut 60 seconds, recorded to the file: " + saveFile.getAbsolutePath());
	            }
			}
		}
	}

	/*public static void main(String[] args) throws IOException {
		Dimension size = WebcamResolution.VGA.getSize();
		Webcam webcam = Webcam.getDefault();
		new SecurityCam(webcam, size);
	}*/
}
