package com.example.testversion.jdsp.windows;

import com.example.testversion.jdsp.misc.UtilMethods;

import java.util.Arrays;

/**
 * <h1>Tukey Window</h1>
 * Generates a Tukey window, also known as a tapered cosine window.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */
public class Tukey extends _Window {
    private double[] window;
    private final double alpha;
    private final boolean sym;
    private final int len;

    /**
     * This constructor initialises the Tukey class.
     *
     * @param len Length of the window
     * @param alpha Shape parameter of the Tukey window, representing the fraction of the window inside the cosine tapered region.
     * @param sym Whether the window is symmetric
     * @throws IllegalArgumentException if window length is less than 1
     */
    public Tukey(int len, double alpha, boolean sym) throws IllegalArgumentException {
        super(len);
        this.len = len;
        this.alpha = alpha;
        this.sym = sym;
        if (alpha > 1 || alpha < 0) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1");
        }
        generateWindow();
    }

    /**
     * This constructor initialises the Tukey class. Symmetricity is set to True.
     *
     * @param len Length of the window
     * @param alpha Shape parameter of the Tukey window, representing the fraction of the window inside the cosine tapered region.
     * @throws IllegalArgumentException if window length is less than 1
     */
    public Tukey(int len, double alpha) throws IllegalArgumentException {
        this(len, alpha, true);
    }

    private void generateWindow() {
        int tempLen = super.extend(this.len, this.sym);
        this.window = new double[tempLen];

        double[] n = UtilMethods.arange(0.0, tempLen, 1.0);
        int width = (int)Math.floor(this.alpha*(tempLen-1)/2.0);

        double[] n1 = UtilMethods.splitByIndex(n, 0, width+1);
        double[] n2 = UtilMethods.splitByIndex(n, width+1, tempLen-width-1);
        double[] n3 = UtilMethods.splitByIndex(n, tempLen-width-1, tempLen);

        for (int i=0; i<n1.length; i++) {
            n1[i] = 0.5 * (1 + Math.cos(Math.PI * (-1 + 2.0*n1[i]/this.alpha/(tempLen-1))));
        }
        Arrays.fill(n2, 1.0);
        for (int i=0; i<n3.length; i++) {
            n3[i] = 0.5 * (1 + Math.cos(Math.PI * (-2.0/this.alpha + 1 + 2.0*n3[i]/this.alpha/(tempLen-1))));
        }

        this.window = UtilMethods.concatenateArray(n1, n2);
        this.window = UtilMethods.concatenateArray(this.window, n3);
        this.window = super.truncate(this.window);
    }

    /**
     * Generates and returns the Tukey Window
     *
     * @return double[] the generated window
     */
    @Override
    public double[] getWindow() {
        return this.window;
    }
}
