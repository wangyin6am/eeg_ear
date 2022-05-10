package com.example.testversion.jdsp.windows;

import com.example.testversion.jdsp.misc.UtilMethods;

/**
 * <h1>Bohman Window</h1>
 * Generates a Bohman window.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */
public class Bohman extends _Window {
    private double[] window;
    private final boolean sym;
    private final int len;

    /**
     * This constructor initialises the Bohman class.
     * @throws IllegalArgumentException if window length is less than 1
     * @param len Length of the window
     * @param sym Whether the window is symmetric
     */
    public Bohman(int len, boolean sym) throws IllegalArgumentException {
        super(len);
        this.len = len;
        this.sym = sym;
        generateWindow();
    }

    /**
     * This constructor initialises the Bohman class. Symmetricity is set to True.
     * @throws IllegalArgumentException if window length is less than 1.
     * @param len Length of the window
     */
    public Bohman(int len) throws IllegalArgumentException {
        this(len, true);
    }

    private void generateWindow() {
        int tempLen = super.extend(this.len, this.sym);
        this.window = UtilMethods.linspace(-1, 1, tempLen, true);
        for (int i=0; i<this.window.length; i++) {
            double fac = Math.abs(this.window[i]);
            this.window[i] = (1 - fac) * Math.cos(Math.PI * fac) + 1.0/Math.PI * Math.sin(Math.PI * fac);
        }
        this.window[0] = 0;
        this.window[tempLen-1] = 0;
        this.window = super.truncate(this.window);
    }

    /**
     * Generates and returns the Bohman Window
     * @return double[] the generated window
     */
    @Override
    public double[] getWindow() {
        return this.window;
    }
}
