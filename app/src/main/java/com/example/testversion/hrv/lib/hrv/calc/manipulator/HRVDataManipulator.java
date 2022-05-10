package com.example.testversion.hrv.lib.hrv.calc.manipulator;

import com.example.testversion.hrv.lib.hrv.RRData;

@FunctionalInterface
public interface HRVDataManipulator {

	/**
	 * Manipulates the given data and returns the manipulated data. The given data must at least contain three data-points.
	 * @param data data to manipulate.
	 * @return Manipulated data.
	 */
	RRData manipulate(RRData data);
}
