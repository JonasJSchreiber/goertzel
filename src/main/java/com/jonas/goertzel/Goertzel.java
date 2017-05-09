package com.jonas.goertzel;

public class Goertzel {
	
	private float sampling_rate;
	private float target_frequency;
	private int n;
	private double[] testData;
	private double coeff, Q1, Q2;
	private double sine, cosine;

	public Goertzel(float sampleRate, float targetFreq, double[] data) {
		sampling_rate = sampleRate;
		target_frequency = targetFreq;
		n = data.length;
		testData = data;
		sine = 0.14904226617617444692935471527722; // = sin(2*pi*200/420)
		cosine = -0.98883082622512854506974288293401; // = cos(2*pi*200/420)
		coeff = 2 * cosine;
		init();
	}

	/**
	 * Call this once, to precompute the constants.
	 *
	 * @return void
	 */
	public void init() {
		int k;
		float floatN;
		double omega;

		floatN = (float) n;
		k = (int) (0.5 + ((floatN * target_frequency) / sampling_rate));
		omega = (2.0 * Math.PI * k) / floatN;
		sine = Math.sin(omega);
		cosine = Math.cos(omega);
		coeff = 2.0 * cosine;
		resetGoertzel();
	}


	/**
	 * Call this method after every block of N samples has been processed.
	 *
	 * @return void
	 */
	public void resetGoertzel() {
		Q2 = 0;
		Q1 = 0;
	}
	/**
	 * Call this routine for every sample.
	 *
	 * @param sample
	 *            is a double
	 * @return void
	 */
	public void processSample(double sample) {
		double Q0;

		Q0 = coeff * Q1 - Q2 + sample;
		Q2 = Q1;
		Q1 = Q0;
	}

	/**
	 * Basic Goertzel. Call this routine after every block to get the complex result.
	 *
	 * @param parts
	 *            has length two where the first item is the real part and the second item is the
	 *            complex part.
	 * @return double[] stores the values in the param
	 */
	public double[] getRealImag(double[] parts) {
		parts[0] = (Q1 - Q2 * cosine);
		parts[1] = (Q2 * sine);
		return parts;
	}

	/**
	 * Optimized Goertzel. Call this after every block to get the RELATIVE magnitude squared.
	 *
	 * @return double is the value of the relative mag squared.
	 */
	public double getMagnitudeSquared() {
		return (Q1 * Q1 + Q2 * Q2 - Q1 * Q2 * coeff);
	}

	public double evaluate() {
		int index;

		double magnitudeSquared;
		double magnitude;
		double real;
		double imag;
		double[] parts = new double[2];

		/* Process the samples. */
		for (index = 0; index < n; index++) {
			processSample(testData[index]);
		}

		parts = getRealImag(parts);
		real = parts[0];
		imag = parts[1];

		magnitudeSquared = real * real + imag * imag;
		magnitude = Math.sqrt(magnitudeSquared);

		resetGoertzel();
		return magnitude;
	}

}
