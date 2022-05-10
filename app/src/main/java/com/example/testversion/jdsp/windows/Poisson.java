package com.example.testversion.jdsp.windows;

import com.example.testversion.jdsp.misc.UtilMethods;

/**
 * <h1>Poisson Window</h1>
 * Generates an Poisson (or exponential) window.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */
public class Poisson extends _Window {
    private double[] window;
    private final boolean sym;
    private final int len;

    /**
     * This constructor initialises the Poisson class.
     * @throws IllegalArgumentException if window length is less than 1
     * @param len Length of the window
     * @param sym Whether the window is symmetric
     */
    public Poisson(int len, boolean sym) throws IllegalArgumentException {
        super(len);
        this.len = len;
        this.sym = sym;
        generateWindow();
    }

    /**
     * This constructor initialises the Poisson class. Symmetricity is set to True.
     * @throws IllegalArgumentException if window length is less than 1.
     * @param len Length of the window
     */
    public Poisson(int len) throws IllegalArgumentException {
        this(len, true);
    }

    private void generateWindow() {
        int tempLen = super.extend(this.len, this.sym);
        double centre = ((double)tempLen-1.0)/2.0;
        this.window = UtilMethods.arange(0.0, tempLen, 1.0);
        for (int i=0; i<this.window.length; i++) {
            this.window[i] = Math.exp(-Math.abs(this.window[i] - centre));
        }
        this.window = super.truncate(this.window);
    }

    /**
     * Generates and returns the Poisson Window
     * @return double[] the generated window
     */
    @Override
    public double[] getWindow() {
        return this.window;
    }
}
