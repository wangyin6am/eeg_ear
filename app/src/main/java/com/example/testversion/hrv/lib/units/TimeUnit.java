package com.example.testversion.hrv.lib.units;

public enum TimeUnit {
	DAY("d"),
	HOUR("h"),
	MINUTE("m"),
	SECOND("s"),
	MILLISECOND("ms");
	
	private final String asText;
	
	TimeUnit(String asText) {
		this.asText = asText;
	}
	
	@Override
	public String toString() {
		return asText;
	}
}
