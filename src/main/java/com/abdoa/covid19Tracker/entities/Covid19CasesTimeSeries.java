package com.abdoa.covid19Tracker.entities;

import java.util.Map;

public class Covid19CasesTimeSeries {
	private String country;
	private Map<String,Long> timeSeriesCasesMap;
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Map<String, Long> getTimeSeriesCasesMap() {
		return timeSeriesCasesMap;
	}
	public void setTimeSeriesCasesMap(Map<String, Long> timeSeriesCasesMap) {
		this.timeSeriesCasesMap = timeSeriesCasesMap;
	}
	public Covid19CasesTimeSeries(String country, Map<String, Long> timeSeriesCasesMap) {
		super();
		this.country = country;
		this.timeSeriesCasesMap = timeSeriesCasesMap;
	}
	@Override
	public String toString() {
		return "Covid19CasesTimeSeries [country=" + country + ", timeSeriesCasesMap=" + timeSeriesCasesMap + "]";
	}
	

}
