package com.example.testversion.hrv.lib.hrv.calc.continous;

import com.example.testversion.hrv.lib.hrv.calc.parameter.HRVParameter;

@FunctionalInterface
public interface HRVParameterChangedListener {

	void parameterChanged(HRVParameter param);
}
