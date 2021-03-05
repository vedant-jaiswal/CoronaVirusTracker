package com.covid.tracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.covid.tracker.model.LocationStates;
import com.covid.tracker.services.CoronaVirusDataService;

@Controller
public class HomeController {
	
	@Autowired
	CoronaVirusDataService coronaVirusDataService;
	
	@GetMapping("/")
	public String home(Model model) {
		List<LocationStates> allStates = coronaVirusDataService.getAllStates();
		int totalReportedCases = allStates.stream().mapToInt(state -> state.getLatestTotalCases()).sum();
		int totalNewCases = allStates.stream().mapToInt(state -> state.getDifFromPrevDay()).sum();
		model.addAttribute("locationStates",allStates);
		model.addAttribute("totalReportedCases",totalReportedCases);
		model.addAttribute("totalNewCases",totalNewCases);
		
		return "home";
	}
}
