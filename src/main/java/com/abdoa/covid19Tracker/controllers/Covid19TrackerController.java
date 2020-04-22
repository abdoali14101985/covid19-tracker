package com.abdoa.covid19Tracker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.abdoa.covid19Tracker.entities.Covid19Cases;
import com.abdoa.covid19Tracker.entities.Covid19CasesTimeSeries;
import com.abdoa.covid19Tracker.services.Covid19DataService;
import com.google.gson.Gson;

@Controller
public class Covid19TrackerController {
	
	@Autowired
	Covid19DataService covid19DataService;
	
	@GetMapping("/")
	public String covid19tracker(Model model) {
		Gson gson =new Gson();
		List<Covid19Cases> covid19CasesList=covid19DataService.getCovid19CasesListByCountries();
		List<Covid19CasesTimeSeries> covid19TimeSeriesList=covid19DataService.getCovid19TimeSeriesListByCountries();
		final String reportDate=Covid19DataService.getReportDate();
		Covid19CasesTimeSeries globalTimeSeries=covid19TimeSeriesList.stream().
				filter(c -> c.getCountry().equalsIgnoreCase("Global"))
				.reduce((a, b) -> {
		            throw new IllegalStateException("Multiple elements: " + a + ", " + b);
		        }).get();
		long totalConfirmedCases=covid19CasesList.stream().mapToLong(covid19Cases -> covid19Cases.getConfirmedCases()).sum();
		long totalRecoveredCases=covid19CasesList.stream().mapToLong(covid19Cases -> covid19Cases.getRecoveredCases()).sum();
		long totalDeaths=covid19CasesList.stream().mapToLong(covid19Cases -> covid19Cases.getConfirmedDeaths()).sum();
		long totalActiveCases=covid19CasesList.stream().mapToLong(covid19Cases -> covid19Cases.getActiveCases()).sum();
		long totalConfirmedChange=covid19CasesList.stream().mapToLong(covid19Cases -> covid19Cases.getConfirmedCasesChange()).sum();
		long totalRecoveredChange=covid19CasesList.stream().mapToLong(covid19Cases -> covid19Cases.getRecoveredCasesChange()).sum();
		long totalDeathChange=covid19CasesList.stream().mapToLong(covid19Cases -> covid19Cases.getConfirmedDeathsChange()).sum();
		long totalActiveChange=covid19CasesList.stream().mapToLong(covid19Cases -> covid19Cases.getActiveCasesChange()).sum();
		String jsonStr = gson.toJson(globalTimeSeries.getTimeSeriesCasesMap());
		String newJsonStr=jsonStr.replaceAll("/", "-");
		model.addAttribute("covid19CasesList", covid19CasesList);
		model.addAttribute("totalConfirmedCases", totalConfirmedCases);
		model.addAttribute("totalRecoveredCases", totalRecoveredCases);
		model.addAttribute("totalDeaths", totalDeaths);
		model.addAttribute("totalActiveCases", totalActiveCases);
		model.addAttribute("totalConfirmedChange", totalConfirmedChange);
		model.addAttribute("totalRecoveredChange", totalRecoveredChange);
		model.addAttribute("totalDeathChange", totalDeathChange);
		model.addAttribute("totalActiveChange", totalActiveChange);
		model.addAttribute("reportDate", reportDate);
		model.addAttribute("jsonStr",newJsonStr);
		return "covid19tracker";
	}

}
