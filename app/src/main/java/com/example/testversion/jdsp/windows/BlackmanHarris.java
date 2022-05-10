package com.example.testversion.jdsp.windows;

/**
 * <h1>Blackman-Harris Window</h1>
 * The Blackman-Harris window is a taper formed by using the first three terms of a summation of cosines. It was designed
 * to have close to the minimal leakage possible.  It is close to optimal, only slightly worse than a Kaiser window.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */
public class BlackmanHarris extends _Window {
    private double[] window;
    private final boolean sym;
    private final int len;

    /**
     * This constructor initialises the BlackmanHarris class.
     * @throws IllegalArgumentException if window length is less than 1
     * @param len Length of the window
     * @param sym Whether the window is symmetric
     */
    public BlackmanHarris(int len, boolean sym) throws IllegalArgumentException {
        super(len);
        this.len = len;
        this.sym = sym;
        generateWindow();
    }

    /**
     * This constructor initialises the BlackmanHarris class. Symmetricity is set to True.
     * @throws IllegalArgumentException if window length is less than 1.
     * @param len Length of the window
     */
    public BlackmanHarris(int len) throws IllegalArgumentException {
        this(len, true);
    }

    private void generateWindow() {
        double[] w = {0.35875, 0.48829, 0.14128, 0.01168};
        com.example.testversion.jdsp.windows.GeneralCosine gc = new GeneralCosine(this.len, w, this.sym);
        this.window = gc.getWindow();
    }

    /**
     * Generates and returns the BlackmanHarris Window
     * @return double[] the generated window
     */
    @Override
    public double[] getWindow() {
        return this.window;
    }
}
