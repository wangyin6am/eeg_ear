package com.example.testversion.hrv.lib.hrv.calc.parameter;

import com.example.testversion.hrv.lib.hrv.RRData;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class SDHRCalculator implements HRVDataProcessor {

    @Override
    public HRVParameter process(RRData data) {
        StandardDeviation d = new StandardDeviation();

        double[] values = data.getValueAxis();
        double[] HR = new double[values.length];
        for (int i=0; i<values.length; i++) {
            HR[i] = 60.0 / values[i];
        }

        return new HRVParameter(HRVParameterEnum.SDHR, d.evaluate(HR), data.getValueAxisUnit().toString());
    }

    @Override
    public boolean validData(RRData data) {
        return data.getValueAxis().length > 0;
    }
}
