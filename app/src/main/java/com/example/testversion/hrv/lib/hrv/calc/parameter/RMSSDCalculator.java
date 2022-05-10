package com.example.testversion.hrv.lib.hrv.calc.parameter;

import com.example.testversion.hrv.lib.hrv.RRData;

public class RMSSDCalculator implements HRVDataProcessor {

	@Override
	public HRVParameter process(RRData data) {
		double[] rr = data.getValueAxis();
		double sum = 0;

		for (int i = 1; i < rr.length; i++) {
			sum += (rr[i - 1] - rr[i]) * (rr[i - 1] - rr[i]);
		}

		return new HRVParameter(HRVParameterEnum.RMSSD, Math.sqrt(sum / (rr.length - 1)), data.getTimeAxisUnit().toString());
	}

	@Override
	public boolean validData(RRData data) {
		return data.getValueAxis().length > 1;
	}
}
