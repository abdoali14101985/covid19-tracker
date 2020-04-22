package com.abdoa.covid19Tracker.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.abdoa.covid19Tracker.entities.Covid19Cases;
import com.abdoa.covid19Tracker.entities.Covid19CasesTimeSeries;

@Service
public class Covid19DataService {
	
	private static String reportDate= getYesterdayDate();
	private static String COVID19_CASES_TIMESERIES_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private static String COVID19_RECOVERED_TIMESERIES_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
	private static String COVID19_DEATH_TIMESERIES_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
	List<Covid19Cases> covid19CasesListByCountries =new ArrayList<>();
	public List<Covid19Cases> getCovid19CasesListByCountries() {
		return covid19CasesListByCountries;
	}

	List<Covid19CasesTimeSeries> covid19TimeSeriesListByCountries=new ArrayList<>();
	
	public List<Covid19CasesTimeSeries> getCovid19TimeSeriesListByCountries() {
		return covid19TimeSeriesListByCountries;
	}
	public static String getReportDate() {
		return reportDate;
	}

	
	@PostConstruct
	@Scheduled(cron = "0 0 7 * * ?")
	public void fetchCovid19TimeSeriesData() throws IOException {
		System.out.println("New Run");
		String timeSeriesCasesDataResponse=getURLResponse(COVID19_CASES_TIMESERIES_DATA_URL);
		String timeSeriesRecoveredDataResponse=getURLResponse(COVID19_RECOVERED_TIMESERIES_DATA_URL);
		String timeSeriesDeathDataResponse=getURLResponse(COVID19_DEATH_TIMESERIES_DATA_URL);
		Iterable<CSVRecord> recordsForCountry = getCSVRecords(timeSeriesCasesDataResponse);
		List<String> uniqueCountryList=getUniqueCountriesFromRecords(recordsForCountry,"Country/Region");
		covid19CasesListByCountries=uniqueCountryList.stream()
				.map(country -> new Covid19Cases(country,0,0,0,0,0,0,0,0))
				.collect(Collectors.toList());
		List<String> dateHeaderList=getHeadersFromResponse(timeSeriesCasesDataResponse);
		covid19TimeSeriesListByCountries=uniqueCountryList.stream()
				.map(country -> new Covid19CasesTimeSeries(country, new LinkedHashMap<String,Long>())).
				collect(Collectors.toList());
		Iterable<CSVRecord> recordsForCaseData = getCSVRecords(timeSeriesCasesDataResponse);
		Iterable<CSVRecord> recordsForRecoveredData = getCSVRecords(timeSeriesRecoveredDataResponse);
		Iterable<CSVRecord> recordsForDeathData = getCSVRecords(timeSeriesDeathDataResponse);
		for(CSVRecord record : recordsForCaseData){
			addTimeSeriesCasesToCountries(covid19TimeSeriesListByCountries,record,dateHeaderList);
			addConfirmedCasesToCountries(covid19CasesListByCountries,record);
		}
		addGlobalTimeSeries(covid19TimeSeriesListByCountries,dateHeaderList);
		for(CSVRecord record : recordsForRecoveredData){
			addRecoveredCasesToCountries(covid19CasesListByCountries,record);
		}
		for(CSVRecord record : recordsForDeathData){
			addDeathCasesToCountries(covid19CasesListByCountries,record);
		}
		addActiveCasesToCountries(covid19CasesListByCountries);
		Collections.sort(covid19CasesListByCountries, Comparator.comparingLong(Covid19Cases ::getConfirmedCases).reversed());
	}

	private void addGlobalTimeSeries(List<Covid19CasesTimeSeries> covid19TimeSeriesListByCountries2, List<String> headerList) {
		Map<String,Long> globalTimeSeriesMap = new LinkedHashMap<String,Long>();
		for (Covid19CasesTimeSeries timeSeries : covid19TimeSeriesListByCountries) {
			for(int j=0;j<headerList.size();j++) {
				if(globalTimeSeriesMap.containsKey(headerList.get(j))) {
					globalTimeSeriesMap.put(headerList.get(j), 
							globalTimeSeriesMap.get(headerList.get(j))+timeSeries.getTimeSeriesCasesMap().get(headerList.get(j)));
				}else {
					globalTimeSeriesMap.put(headerList.get(j),timeSeries.getTimeSeriesCasesMap().get(headerList.get(j)));
				}
			}
		}
		covid19TimeSeriesListByCountries.add(new Covid19CasesTimeSeries("Global", globalTimeSeriesMap));
		
		
	}
	private void addTimeSeriesCasesToCountries(List<Covid19CasesTimeSeries> covid19TimeSeriesListByCountries,
			CSVRecord record, List<String> headerList) {
		for (Covid19CasesTimeSeries timeSeries : covid19TimeSeriesListByCountries) {
			if(timeSeries.getCountry().equalsIgnoreCase(record.get("Country/Region"))) {
				for(int i=0;i<headerList.size();i++) {
					if(timeSeries.getTimeSeriesCasesMap().containsKey(headerList.get(i))) {
						timeSeries.getTimeSeriesCasesMap().put(headerList.get(i), 
						timeSeries.getTimeSeriesCasesMap().get(headerList.get(i))+ Long.parseLong(record.get(headerList.get(i))));
					} else {
						timeSeries.getTimeSeriesCasesMap().put(headerList.get(i),Long.parseLong(record.get(headerList.get(i))));
					}
				}
			}
		}
		
	}
	private List<String> getHeadersFromResponse(String strResponse) throws IOException {
		List<String> headerList = new ArrayList<>();
		StringReader csvReader = new StringReader(strResponse);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(csvReader);
		for (CSVRecord record : records) {
		    for(int i=4;i<=record.size()-1;i++) {
		    	headerList.add(record.get(i));
		    }
		    break;
		}
		return headerList;
	}
	private String getURLResponse(String urlStr) throws IOException {
		URL url=new URL(urlStr);
		HttpURLConnection con=(HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		StringBuffer response = new StringBuffer();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
				response.append("\n");
			}
			br.close();
		} else {
			System.out.println("Error getting data from url");
		}
		return response.toString();
	}

	private void addConfirmedCasesToCountries(List<Covid19Cases> newcovid19CasesListByCountries, CSVRecord record) {
		for (Covid19Cases cases : newcovid19CasesListByCountries) {
			if (cases.getCountry().equalsIgnoreCase(record.get("Country/Region"))) {
				cases.setConfirmedCases(cases.getConfirmedCases()+Long.parseLong(record.get(record.size()-1)));
				cases.setConfirmedCasesChange(cases.getConfirmedCasesChange()+(
						Long.parseLong(record.get(record.size()-1))
						- Long.parseLong(record.get(record.size()-2))));
			}
		}
	}
	
	private void addRecoveredCasesToCountries(List<Covid19Cases> newcovid19CasesListByCountries, CSVRecord record) {
		for (Covid19Cases cases : newcovid19CasesListByCountries) {
			if (cases.getCountry().equalsIgnoreCase(record.get("Country/Region"))) {
				cases.setRecoveredCases(cases.getRecoveredCases()+Long.parseLong(record.get(record.size()-1)));
				cases.setRecoveredCasesChange(cases.getRecoveredCasesChange()+(
						Long.parseLong(record.get(record.size()-1))
						- Long.parseLong(record.get(record.size()-2))));
			}
		}
		
	}
	
	private void addDeathCasesToCountries(List<Covid19Cases> newcovid19CasesListByCountries, CSVRecord record) {
		for (Covid19Cases cases : newcovid19CasesListByCountries) {
			if (cases.getCountry().equalsIgnoreCase(record.get("Country/Region"))) {
				cases.setConfirmedDeaths(cases.getConfirmedDeaths()+Long.parseLong(record.get(record.size()-1)));
				cases.setConfirmedDeathsChange(cases.getConfirmedDeathsChange()+(
						Long.parseLong(record.get(record.size()-1))
						- Long.parseLong(record.get(record.size()-2))));
			}
		}
		
	}
	
	private void addActiveCasesToCountries(List<Covid19Cases> newcovid19CasesListByCountries) {
		for (Covid19Cases cases : newcovid19CasesListByCountries) {
			cases.setActiveCases(cases.getConfirmedCases()-cases.getRecoveredCases()-cases.getConfirmedDeaths());
			cases.setActiveCasesChange(cases.getConfirmedCasesChange()-cases.getRecoveredCasesChange()-cases.getConfirmedDeathsChange());
		}
		
	}

	private List<String> getUniqueCountriesFromRecords(Iterable<CSVRecord> records, String countryHeader) {
		List<String> countryList=new ArrayList<>();
		for (CSVRecord record : records) { 
			  countryList.add(record.get(countryHeader));
			  }
		countryList=countryList.stream()
				.distinct()
				.collect(Collectors.toList());
		return countryList;
	}

	private Iterable<CSVRecord> getCSVRecords(String strResponse) throws IOException {
		StringReader csvReader = new StringReader(strResponse);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(csvReader);
		return records;
	}

	private static String getYesterdayDate() {
		final LocalDate date = LocalDate.now();
		LocalDate yesterdayDate=date.minusDays(1);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		return yesterdayDate.toString();

	}

}
