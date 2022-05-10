package com.example.testversion.jdsp.windows;

/**
 * <h1>Hanning Window</h1>
 * The Hanning window is a taper formed by using a raised cosine or sine-squared with ends that touch zero.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */
public class Hanning extends _Window {
    private double[] window;
    private final boolean sym;
    private final int len;

    /**
     * This constructor initialises the Hanning class.
     * @throws IllegalArgumentException if window length is less than 1
     * @param len Length of the window
     * @param sym Whether the window is symmetric
     */
    public Hanning(int len, boolean sym) throws IllegalArgumentException {
        super(len);
        this.len = len;
        this.sym = sym;
        generateWindow();
    }

    /**
     * This constructor initialises the Hanning class. Symmetricity is set to True.
     * @throws IllegalArgumentException if window length is less than 1.
     * @param len Length of the window
     */
    public Hanning(int len) throws IllegalArgumentException {
        this(len, true);
    }

    private void generateWindow() {
        double[] w = {0.5, 0.5};
        GeneralCosine gc = new GeneralCosine(this.len, w, this.sym);
        this.window = gc.getWindow();
    }

    /**
     * Generates and returns the Hanning Window
     * @return double[] the generated window
     */
    @Override
    public double[] getWindow() {
        return this.window;
    }
}
