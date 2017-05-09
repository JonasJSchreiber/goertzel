package com.jonas.goertzel;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.sound.sampled.TargetDataLine;

public class Processor {

	public static final int THRESHOLD = 250;
	public static final int TARGET_FREQUENCY = 2711;
	
	Logger logger = null;
	float sampleRate = (float) 44100.00;
	double ampl = -1.0;
	TargetDataLine line = null;
	int thresholdHit = 0;
	boolean exceededThreshold = false;
	double[] audioData;
	
	String filename = "data/output.wav";

	public static void main(String[] args) {
		 new Processor().start();
	}

	public void start() {
		
		try {
			logger = getLogger();
			line = AudioUtils.getLine();
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
			System.exit(1);
		}

		JavaSoundRecorder recorder = new JavaSoundRecorder();

		while (true) {
			Long start = System.currentTimeMillis();
			recorder.record(filename, 200, line);
			try {
				audioData = AudioUtils.getAudioData(filename);
				Goertzel goertzel = new Goertzel(sampleRate, (float) (TARGET_FREQUENCY * 2),
						audioData);
				ampl = goertzel.evaluate();
				exceededThreshold = (ampl > THRESHOLD);
				thresholdHit = exceededThreshold ? thresholdHit++ : 0;
				logger.info("Amplitude at: " + TARGET_FREQUENCY + "Hz: " + ampl);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (thresholdHit == 6) {
					AudioUtils.playAlarm();
					thresholdHit = 0;
					exceededThreshold = false;
				}
				try {
					Thread.sleep(exceededThreshold ? 
							(10000 - (System.currentTimeMillis() - start)) 
							: 500);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	Logger getLogger() throws Exception {
		Logger logger = Logger.getLogger(this.getClass().getName());
		FileHandler fh = new FileHandler("./log/" + this.getClass().getSimpleName() + "%u.log",
				10000000, 10, true);
		fh.setLevel(Level.INFO);
		fh.setFormatter(new SimpleFormatter());
		logger.addHandler(fh);
		logger.setLevel(Level.INFO);
		return logger;
	}
}
