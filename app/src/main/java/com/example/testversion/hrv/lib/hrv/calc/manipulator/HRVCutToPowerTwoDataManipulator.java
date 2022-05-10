package com.example.testversion.hrv.lib.hrv.calc.manipulator;

import com.example.testversion.hrv.lib.common.MathUtils;
import com.example.testversion.hrv.lib.hrv.RRData;

/**
 * Cuts the given data down, so that the resulting length of the data is a power of two.
 * 
 * @author Julian
 *
 */
public class HRVCutToPowerTwoDataManipulator implements HRVDataManipulator {

	@Override
	public RRData manipulate(RRData data) {
		int cutAt = MathUtils.largestNumThatIsPowerOf2(data.getTimeAxis().length);

		double[] oldRRY = data.getValueAxis();
		double[] oldRRX = data.getTimeAxis();

		double[] newX = new double[cutAt];
		double[] newY = new double[cutAt];

		for (int i = data.getTimeAxis().length - cutAt; i < data.getTimeAxis().length; i++) {
			newX[i - (data.getTimeAxis().length - cutAt)] = oldRRX[i];
			newY[i - (data.getTimeAxis().length - cutAt)] = oldRRY[i];
		}
		
		return new RRData(newX, data.getTimeAxisUnit(), newY, data.getValueAxisUnit());
	}

}
