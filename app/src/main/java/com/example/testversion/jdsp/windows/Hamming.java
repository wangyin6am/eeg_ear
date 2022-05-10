package com.example.testversion.jdsp.windows;

/**
 * <h1>Hamming Window</h1>
 * The Hamming window is a taper formed by using a raised cosine with non-zero endpoints,
 * optimized to minimize the nearest side lobe.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */
public class Hamming extends _Window {
    private double[] window;
    private final boolean sym;
    private final int len;

    /**
     * This constructor initialises the Hamming class.
     * @throws IllegalArgumentException if window length is less than 1
     * @param len Length of the window
     * @param sym Whether the window is symmetric
     */
    public Hamming(int len, boolean sym) throws IllegalArgumentException {
        super(len);
        this.len = len;
        this.sym = sym;
        generateWindow();
    }

    /**
     * This constructor initialises the Hamming class. Symmetricity is set to True.
     * @throws IllegalArgumentException if window length is less than 1.
     * @param len Length of the window
     */
    public Hamming(int len) throws IllegalArgumentException {
        this(len, true);
    }

    private void generateWindow() {
        double[] w = {0.54, 0.46};
        GeneralCosine gc = new GeneralCosine(this.len, w, this.sym);
        this.window = gc.getWindow();
    }

    /**
     * Generates and returns the Hamming Window
     * @return double[] the generated window
     */
    @Override
    public double[] getWindow() {
        return this.window;
    }
}
