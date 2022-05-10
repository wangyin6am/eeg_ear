package com.example.testversion.jdsp.windows;

import com.example.testversion.jdsp.misc.UtilMethods;

/**
 * <h1>Triangular Window</h1>
 * Generates a triangular window.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */
public class Triangular extends _Window {
    private double[] window;
    private final boolean sym;
    private final int len;

    /**
     * This constructor initialises the Triangular class.
     * @throws IllegalArgumentException if window length is less than 1
     * @param len Length of the window
     * @param sym Whether the window is symmetric
     */
    public Triangular(int len, boolean sym) throws IllegalArgumentException {
        super(len);
        this.len = len;
        this.sym = sym;
        generateWindow();
    }

    /**
     * This constructor initialises the Triangular class. Symmetricity is set to True.
     * @throws IllegalArgumentException if window length is less than 1.
     * @param len Length of the window
     */
    public Triangular(int len) throws IllegalArgumentException {
        this(len, true);
    }

    private void generateWindow() {
        int tempLen = super.extend(this.len, this.sym);

        int halfPoint = (tempLen+1)/2 + 1;
        double[] n = UtilMethods.arange(1.0, halfPoint, 1.0);

        if (tempLen%2 == 0) {
            for (int i=0; i<n.length; i++) {
                n[i]  = (2 * n[i] - 1.0)/tempLen;
            }
            double[] nRev = UtilMethods.reverse(n);
            this.window = UtilMethods.concatenateArray(n, nRev);
        }
        else {
            for (int i=0; i<n.length; i++) {
                n[i]  = (2 * n[i])/(tempLen+1);
            }
            double[] nRev = UtilMethods.splitByIndex(n,0, n.length-1);
            nRev = UtilMethods.reverse(nRev);
            this.window = UtilMethods.concatenateArray(n, nRev);
        }
        this.window = super.truncate(this.window);
    }

    /**
     * Generates and returns the Triangular Window
     * @return double[] the generated window
     */
    @Override
    public double[] getWindow() {
        return this.window;
    }
}
