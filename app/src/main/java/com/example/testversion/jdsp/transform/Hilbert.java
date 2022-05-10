/*
 * Copyright (c) 2020 Sambit Paul
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.testversion.jdsp.transform;


import android.util.Log;

import com.example.testversion.jdsp.misc.UtilMethods;

import org.apache.commons.math3.analysis.function.Atan2;
import org.apache.commons.math3.complex.Complex;

import java.util.Arrays;

/**
 * <h1>Hilbert Transform</h1>
 * The Hilbert class applies the Hilbert transform on the input signal and produces an analytical signal.
 * The analytical signal can be used for finding the amplitude envelope, instantaneous phase and instantaneous frequency of the original signal.
 * Reference <a href="https://en.wikipedia.org/wiki/Hilbert_transform">article</a> for more information on Hilbert transform.
 * Reference <a href="https://tomroelandts.com/articles/what-is-an-analytic-signal">article</a> for more information on analytical signals.
 * <p>
 *
 * @author  Sambit Paul
 * @version 1.0
 */
public class Hilbert {
    String TAG = "Hilbert";
    private double[] signal;
    private double[] h;
    private Complex[] output = null;

    /**
     * This constructor initialises the prerequisites required to use Hilbert.
     * @param s Signal to be transformed
     */
    public Hilbert(double[] s) {
        this.signal = s;
    }

    private void fillH() {
        this.h[0] = 1;
        if (this.h.length%2 == 0) {
            for (int i=1; i<this.h.length/2; i++) {
                this.h[i] = 2;
            }
            this.h[this.h.length/2] = 1;
        }
        else {
            for (int i=0; i<(this.h.length+1)/2; i++) {
                this.h[i] = 2;
            }
        }
    }

    /**
     * This function performs the hilbert transform on the input signal
     */
    public void transform() {
        Log.i("Detection", "transform!-1");
        _Fourier dft;
        if (this.signal.length > 200) {
            dft = new FastFourier(this.signal);
        }
        else {
            dft = new DiscreteFourier(this.signal);
        }
        Log.i("Detection", "transform!-2");
        this.h = new double[dft.getSignalLength()];
        Arrays.fill(this.h, 0);
        this.fillH();
        dft.transform();
        double[][] dftOut = dft.getComplex2D(false);

        double[][] modOut = new double[dftOut.length][dftOut[0].length];
        Log.i("Detection", "transform!-3");
        for (int i=0; i<modOut.length; i++) {
            modOut[i][0] = dftOut[i][0] * this.h[i];
            modOut[i][1] = dftOut[i][1] * this.h[i];
        }
        Log.i("Detection", "transform!-4,modOut.length="+modOut.length);
        _InverseFourier idft;
        if (Math.log(modOut.length)%Math.log(2) == 0) {
            idft = new InverseFastFourier(UtilMethods.matToComplex(modOut), false);
            Log.i("Detection", "transform!-5");
        }
        else {
            idft = new InverseDiscreteFourier(modOut, false);
            Log.i("Detection", "transform!-6");
        }
        Log.i("Detection", "transform!-7");
        idft.transform();
        Log.i("Detection", "transform!-8");
        this.output = idft.getComplex();
        Log.i(TAG, "Detection:transform: output.length"+output.length);
    }

    /**
     * Returns the complex value of the generated analytical signal as a 2D matrix.
     * @throws ExceptionInInitializerError if called before executing hilbert_transform() method
     * @return double[][] The decimated signal
     */
    public double[][] getOutput() throws ExceptionInInitializerError {
        if (this.output == null) {
            throw new ExceptionInInitializerError("Execute hilbert_transform() function before returning result");
        }
        double[][] out = new double[this.output.length][2];
        for (int i=0; i<out.length; i++) {
            out[i][0] = this.output[i].getReal();
            out[i][1] = this.output[i].getImaginary();
        }
        return out;
    }

    /**
     * Returns the amplitude envelope generated analytical signal.
     * @throws ExceptionInInitializerError if called before executing hilbert_transform() method
     * @return double[] The decimated signal
     */
    public double[] getAmplitudeEnvelope() throws ExceptionInInitializerError {
        Log.i("Detection", "Detection:getAmplitudeEnvelope!");
        if (this.output == null) {
            throw new ExceptionInInitializerError("Execute hilbert_transform() function before returning result");
        }
        double[] sig = new double[this.output.length];
        for (int i=0; i<sig.length; i++) {
//            sig[i] = this. output[i].abs();
            sig[i] = Math.hypot(this.output[i].getReal(),this.output[i].getImaginary());
        }
        return sig;
    }

    /**
     * Returns the instantaneous phase generated analytical signal.
     * @throws ExceptionInInitializerError if called before executing hilbert_transform() method
     * @return double[] The decimated signal
     */
    public double[] getInstantaneousPhase() throws ExceptionInInitializerError {
        if (this.output == null) {
            throw new ExceptionInInitializerError("Execute hilbert_transform() function before returning result");
        }
        double[] sig = new double[this.output.length];
        Atan2 ang = new Atan2();
        for (int i=0; i<sig.length; i++) {
            sig[i] = ang.value(this.output[i].getImaginary(), this.output[i].getReal());
        }
        return UtilMethods.unwrap(sig);
    }

    /**
     * Returns the instantaneous frequency generated analytical signal.
     * @param Fs Sampling Frequency to be used
     * @throws ExceptionInInitializerError if called before executing hilbert_transform() method
     * @return double[] The decimated signal
     */
    public double[] getInstantaneousFrequency(double Fs) throws ExceptionInInitializerError {
        if (this.output == null) {
            throw new ExceptionInInitializerError("Execute hilbert_transform() function before returning result");
        }
        double[] temp = this.getInstantaneousPhase();
        double cons = 2 * Math.PI;
        double[] sig = UtilMethods.diff(temp);
        for (int i=0; i<sig.length; i++) {
            sig[i] = (sig[i]/cons)*Fs;
        }
        return sig;
    }
}
