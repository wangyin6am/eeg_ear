package com.example.testversion.hrv.lib.hrv.calc.parameter;

import com.example.testversion.hrv.lib.hrv.RRData;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class SD1Calculator implements HRVDataProcessor {

	@Override
	public HRVParameter process(RRData data) {
		StandardDeviation sdnnCalc = new StandardDeviation();
		double sdnn = sdnnCalc.evaluate(data.getValueAxis());
		return new HRVParameter(HRVParameterEnum.SD1, Math.sqrt(0.5 * sdnn * sdnn), data.getValueAxisUnit().toString());
	}

	@Override
	public boolean validData(RRData data) {
		return data.getValueAxis().length > 1;
	}
}
