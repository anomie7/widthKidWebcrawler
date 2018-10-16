package com.crawling;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { WebCrawlingPracticeApplication.class })
@ActiveProfiles("test")
@Slf4j @Transactional
public class IntegrationTestCrawling {

	@Autowired
	private InterParkRepository interparkRepository;

	@Autowired
	private InterParkCrawling interparkCrawling;
	
	@Test
	public void testFindNewCrawlingData() throws Exception {
		for (InterparkType dtype : InterparkType.values()) {
			List<InterParkDTO> tmp = interparkCrawling.findNewCrawlingData(dtype);
			interparkRepository.save(tmp);
			log.debug("{}", tmp.size());
		}
	}

}
