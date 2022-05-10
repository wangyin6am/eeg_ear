package com.example.testversion.hrv.lib.hrv.calc.continous;

import com.example.testversion.hrv.lib.common.ArrayUtils;
import com.example.testversion.hrv.lib.common.RotatingMaxSizeList;
import com.example.testversion.hrv.lib.hrv.RRData;
import com.example.testversion.hrv.lib.hrv.calc.parameter.HRVDataProcessor;
import com.example.testversion.hrv.lib.hrv.calc.parameter.HRVParameter;
import com.example.testversion.hrv.lib.units.TimeUnit;

import java.util.ArrayList;
import java.util.List;

abstract class HRVContinuousParameterCalculator implements HRVRRIntervalListener, HRVDataProcessor {

	protected final List<HRVParameterChangedListener> listeners = new ArrayList<>();
	protected final RotatingMaxSizeList<Double> ibis;
	
	protected HRVContinuousParameterCalculator(int size) {
		ibis = new RotatingMaxSizeList<>(new Double[size]);
	}
	
	public void addHRVParameterChangedListener(HRVParameterChangedListener listener) {
		listeners.add(listener);
	}
	
	public void removeHRVParameterChangedListener(HRVParameterChangedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Takes a new Inter-Beat-Interval, adds it to existing data, calculates the new parameter and informs
	 * all listeners.
	 * The given {@code ibi} must be in seconds.
	 */
	@Override
	public void newRRInterval(HRVRRIntervalEvent eventArgs) {
		ibis.add(eventArgs.getRr());
		Double[] ibisArray = ibis.getArray();
		double[] ibisArrayPrimitive = ArrayUtils.toPrimitiveIgnoreNull(ibisArray);
		RRData rrData = RRData.createFromRRInterval(ibisArrayPrimitive, TimeUnit.SECOND);
		notifyAll(process(rrData));
	}
	
	private void notifyAll(HRVParameter param) {
		for(HRVParameterChangedListener listener : listeners) {
			listener.parameterChanged(param);
		}
	}
	
	@Override
	public boolean validData(RRData data) {
		return true;
	}
}
