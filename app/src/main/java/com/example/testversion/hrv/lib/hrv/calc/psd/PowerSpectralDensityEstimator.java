package com.example.testversion.hrv.lib.hrv.calc.psd;

import com.example.testversion.hrv.lib.hrv.RRData;

@FunctionalInterface
public interface PowerSpectralDensityEstimator {

	PowerSpectrum calculateEstimate(RRData rr);
}
