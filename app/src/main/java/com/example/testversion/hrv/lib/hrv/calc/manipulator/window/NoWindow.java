package com.example.testversion.hrv.lib.hrv.calc.manipulator.window;

import com.example.testversion.hrv.lib.hrv.RRData;
import com.example.testversion.hrv.lib.hrv.calc.manipulator.HRVDataManipulator;

public class NoWindow implements HRVDataManipulator {

	@Override
	public RRData manipulate(RRData data) {
		return data;
	}

}
