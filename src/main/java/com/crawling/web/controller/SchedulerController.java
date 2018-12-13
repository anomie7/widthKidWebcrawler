package com.crawling.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.crawling.scheduler.InterParkCrawlingScheduler;

@Controller
public class SchedulerController {
	@Autowired
	private InterParkCrawlingScheduler scheduler;
	
	@GetMapping(value="/scheduler")
	public String findNewData() throws Exception {
		scheduler.crawlingInterparkData();
		return "";
	}
	
	@GetMapping(value="/scheduler/event/delete")
	public void invalidDateUpdate() throws Exception {
		scheduler.invalidDataDelete();
	}

}
