package com.covid.tracker.services;



import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.covid.tracker.model.LocationStates;

@Service
public class CoronaVirusDataService {

	private static String virus_data_url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStates> allStates = new ArrayList<>();
		
	public List<LocationStates> getAllStates() {
		return allStates;
	}

	@PostConstruct
	@Scheduled(cron="* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		
		List<LocationStates> newStates = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = (HttpRequest) HttpRequest.newBuilder()
				.uri(URI.create(virus_data_url))
				.build();
		
		HttpResponse<String> httpResponse =  client.send(request, HttpResponse.BodyHandlers.ofString());
		
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		
		
		for (CSVRecord record : records) {
			
			LocationStates locationState = new LocationStates();
			locationState.setState(record.get("Province/State"));
			locationState.setCountry(record.get("Country/Region"));
			int latestCases = Integer.parseInt(record.get(record.size()-1));
			int prevDayCases = Integer.parseInt(record.get(record.size()-2));
			locationState.setDifFromPrevDay(latestCases-prevDayCases);
			locationState.setLatestTotalCases(latestCases);
						
			newStates.add(locationState);
			
		}
		this.allStates = newStates;
	}
}
