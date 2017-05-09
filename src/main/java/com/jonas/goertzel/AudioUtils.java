package com.jonas.goertzel;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioUtils {

	static TargetDataLine getLine() throws LineUnavailableException {
		AudioFormat format = getAudioFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		// checks if system supports the data line
		if (!AudioSystem.isLineSupported(info)) {
			throw new LineUnavailableException("Line not supported!");
		}

		final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
		return line;
	}

	public static double[] getAudioData(String filename) throws Exception {
		final File audioFile = new File(filename);
		final AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buff = new byte[1024];
		for (int read = 0; (read = inputStream.read(buff)) != -1;) {
			out.write(buff, 0, read);
		}
		out.close();
		final byte[] audioBytes = out.toByteArray();

		double[] audioData = new double[audioBytes.length / 2];
		for (int i = 0, j = 0; j < audioData.length;) {
			audioData[j++] = ((audioBytes[i++] & 0xff) | (audioBytes[i++] << 8)) / 32768.0;
		}
		return audioData;
	}

	static AudioFormat getAudioFormat() {
		float sampleRate = 44100;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
		return format;
	}
	
	public static void playAlarm() {
		AudioInputStream ais = null;
		Clip clip = null;
		try {
			ais = AudioSystem.getAudioInputStream(new File("data/CarAlarm.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();

			while (!clip.isRunning())
				Thread.sleep(10);
			while (clip.isRunning())
				Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ais.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
