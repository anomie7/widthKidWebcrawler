package com.crawling.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.crawling.domain.InterParkData;
import com.crawling.domain.InterparkType;
import com.crawling.repository.InterParkRepository;
import com.crawling.service.InterParkCrawler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InterParkCrawlingScheduler {
	@Autowired
	private InterParkRepository interparkRepository;
	@Autowired
	private InterParkCrawler interparkCrawling;
	
	@Scheduled(cron="0 45 * * * *")
	public void crawlingInterparkData() throws Exception {
		log.info("crawling start!");
		for (InterparkType dtype : InterparkType.values()) {
			List<InterParkData> tmp = interparkCrawling.findNewCrawlingData(dtype);
			interparkRepository.saveAll(tmp);
		}
		log.info("crawling finish!");
	}
	
	@Scheduled(cron="0 35 * * * *")
	public void invalidDataDelete() {
		interparkCrawling.invalidDataDelete();
	}
}
