package com.example.testversion.hrv.lib.hrv.calc.parameter;

import com.example.testversion.hrv.lib.hrv.RRData;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

public class MeanCalculator implements HRVDataProcessor {

	@Override
	public HRVParameter process(RRData data) {
		Mean m = new Mean();
		return new HRVParameter(HRVParameterEnum.MEAN, m.evaluate(data.getValueAxis()), data.getValueAxisUnit().toString());
	}

	@Override
	public boolean validData(RRData data) {
		return data.getValueAxis().length > 0;
	}
}
