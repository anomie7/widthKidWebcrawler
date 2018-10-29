package com.crawling.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.crawling.scheduler.InterParkCrawlingScheduler;

@Controller
public class InterparkController {
	@Autowired
	private InterParkCrawlingScheduler scheduler;
	
	@GetMapping(value="/")
	public void findNewData() throws Exception {
		scheduler.crawlingInterparkData();
	}
	
	@GetMapping(value="/delete")
	public void invalidDateUpdate() throws Exception {
		scheduler.invalidDataDelete();
	}

}
