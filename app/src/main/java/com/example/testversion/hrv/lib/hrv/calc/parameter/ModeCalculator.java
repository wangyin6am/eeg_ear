package com.example.testversion.hrv.lib.hrv.calc.parameter;

import com.example.testversion.hrv.lib.hrv.RRData;

public class ModeCalculator implements HRVDataProcessor {

	@Override
	public HRVParameter process(RRData data) {
		
		double[] a = data.getValueAxis();
		double maxValue = 0;
		double maxCount = 0;

		// For each element in a calculate the occurrences of that element in a.
		for (double anA : a) {
			double count = 0;
			for (double anA1 : a) {

				// Because the elements are of floating point precision they are
				// almost never the same.
				// Therefore they have to be in a certain range.
				if (!((anA1 > anA * 1.05) || (anA1 < anA * 0.95)))
					++count;
			}
			if (count > maxCount) {
				maxCount = count;
				maxValue = anA;
			}
		}

		return new HRVParameter(HRVParameterEnum.MODE, maxValue, "non");
	}
	
	@Override
	public boolean validData(RRData data) {
		return true;
	}
}
