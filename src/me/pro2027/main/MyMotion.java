package me.pro2027.main;

import java.io.IOException;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionDetectorDefaultAlgorithm;
import com.github.sarxos.webcam.WebcamResolution;

import boofcv.abst.feature.detect.interest.DetectorInterestPointMulti;


/**
 * The goal of this example is to demonstrate the idea behind detecting that
 * motion has stopped.
 * 
 * @author Bartosz Firyn (sarxos)
 */
public class MyMotion {

	WebcamMotionDetector detector;
	
	private boolean motion = false;
	
	public boolean isInMotion() {
		return motion;
	}
	

	public MyMotion(Webcam webcam) {

		detector = new WebcamMotionDetector(webcam);
		detector.setInterval(WebcamMotionDetector.DEFAULT_INTERVAL); // one check per half second
		detector.setInertia(10000); // keep "motion" state for 10 seconds (so video clips are at least 10 seconds long)
		detector.setAreaThreshold(WebcamMotionDetectorDefaultAlgorithm.DEFAULT_AREA_THREASHOLD/2.5d);
		detector.setPixelThreshold((int)((float)WebcamMotionDetectorDefaultAlgorithm.DEFAULT_PIXEL_THREASHOLD/2.5f));
		detector.start();

		Thread t = new Thread("motion-printer") {

			@Override
			public void run() {

				long now = 0;

				while (true) {
					now = System.currentTimeMillis();
					if (detector.isMotion()) {
						if (!motion) {
							motion = true;
							System.out.println(now + " MOTION STARTED");
						}
					} else {
						if (motion) {
							motion = false;
							System.out.println(now + " MOTION STOPPED");
						}
					}
					try {
						Thread.sleep(50); // must be smaller than interval
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		t.setDaemon(true);
		t.start();
	}
}
