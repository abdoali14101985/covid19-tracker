package com.abdoa.covid19Tracker.entities;

public class Covid19Cases {
	private String country;
	private long confirmedCases;
	private long confirmedCasesChange;
	private long confirmedDeaths;
	private long confirmedDeathsChange;
	private long recoveredCases;
	private long recoveredCasesChange;
	private long activeCases;
	private long activeCasesChange;
	public Covid19Cases(String country, long confirmedCases, long confirmedCasesChange, long confirmedDeaths,
			long confirmedDeathsChange, long recoveredCases, long recoveredCasesChange, long activeCases,
			long activeCasesChange) {
		super();
		this.country = country;
		this.confirmedCases = confirmedCases;
		this.confirmedCasesChange = confirmedCasesChange;
		this.confirmedDeaths = confirmedDeaths;
		this.confirmedDeathsChange = confirmedDeathsChange;
		this.recoveredCases = recoveredCases;
		this.recoveredCasesChange = recoveredCasesChange;
		this.activeCases = activeCases;
		this.activeCasesChange = activeCasesChange;
	}
	public long getConfirmedCasesChange() {
		return confirmedCasesChange;
	}
	public void setConfirmedCasesChange(long confirmedCasesChange) {
		this.confirmedCasesChange = confirmedCasesChange;
	}
	public long getConfirmedDeathsChange() {
		return confirmedDeathsChange;
	}
	public void setConfirmedDeathsChange(long confirmedDeathsChange) {
		this.confirmedDeathsChange = confirmedDeathsChange;
	}
	public long getRecoveredCasesChange() {
		return recoveredCasesChange;
	}
	public void setRecoveredCasesChange(long recoveredCasesChange) {
		this.recoveredCasesChange = recoveredCasesChange;
	}
	public long getActiveCasesChange() {
		return activeCasesChange;
	}
	public void setActiveCasesChange(long activeCasesChange) {
		this.activeCasesChange = activeCasesChange;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public long getConfirmedCases() {
		return confirmedCases;
	}
	public void setConfirmedCases(long confirmedCases) {
		this.confirmedCases = confirmedCases;
	}
	public long getConfirmedDeaths() {
		return confirmedDeaths;
	}
	public void setConfirmedDeaths(long confirmedDeaths) {
		this.confirmedDeaths = confirmedDeaths;
	}
	public long getRecoveredCases() {
		return recoveredCases;
	}
	public void setRecoveredCases(long recoveredCases) {
		this.recoveredCases = recoveredCases;
	}
	public long getActiveCases() {
		return activeCases;
	}
	public void setActiveCases(long activeCases) {
		this.activeCases = activeCases;
	}
	@Override
	public String toString() {
		return "Covid19Cases [country=" + country + ", confirmedCases=" + confirmedCases + ", confirmedCasesChange="
				+ confirmedCasesChange + ", confirmedDeaths=" + confirmedDeaths + ", confirmedDeathsChange="
				+ confirmedDeathsChange + ", recoveredCases=" + recoveredCases + ", recoveredCasesChange="
				+ recoveredCasesChange + ", activeCases=" + activeCases + ", activeCasesChange=" + activeCasesChange
				+ "]";
	}

}
