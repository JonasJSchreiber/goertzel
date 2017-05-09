package com.jonas.goertzel;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * A sample program is to demonstrate how to record sound in Java author: www.codejava.net
 */
public class JavaSoundRecorder {
	File wavFile = null;

	// format of audio file
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
	long RECORD_TIME;

	public void record(String outputFile, long recordTime, TargetDataLine line) {
		final JavaSoundRecorder recorder = new JavaSoundRecorder();
		recorder.RECORD_TIME = recordTime;
		recorder.wavFile = new File(outputFile);
		recorder.start(line);
	}
	
	void start(final TargetDataLine line) {
		try {
			line.open(AudioUtils.getAudioFormat());
			AudioInputStream ais = new AudioInputStream(line);
			Thread stopper = new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(RECORD_TIME);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					line.stop();
				}
			});
			stopper.start();
			line.start();
			AudioSystem.write(ais, fileType, wavFile);

		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
