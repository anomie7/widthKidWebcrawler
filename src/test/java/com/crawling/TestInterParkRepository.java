package com.crawling;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
public class TestInterParkRepository {

	@Autowired
	private InterParkRepository interparkRepository;
	
	@Autowired
	private InterParkCrawling interparkCrawling;
	
//	@Test
//	public void testSave() throws Exception {
//		ChromeOptions chromeOptions = new ChromeOptions();
//		chromeOptions.addArguments("--headless");
//
//		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
//		WebDriver driver = new ChromeDriver(chromeOptions);
//		
//		for (InterparkType dtype : InterparkType.values()) {
//			List<InterParkDTO> ls = interparkCrawling.crawling(dtype);
//			for (InterParkDTO interParkDTO : ls) {
//				try {
//					List<Price> price = interparkCrawling.findPrice(driver, interParkDTO);
//					for (Price o : price) {
//						interParkDTO.addPrice(o);
//					}
//				} catch (Exception e) {
//					log.error("가격형식이 다르거나 해당 이벤트가 마감되었습니다.");
//				}
//			}
//			interparkRepository.save(ls);
//		}
//	}

	@Test
	public void testFindInterparkCode() {
		List<String> tmp = interparkRepository.findInterparkcodeByDtype(InterparkType.values()[0]);
		log.debug("Dtype : {} count:  {}", InterparkType.values()[0], tmp.stream().count());
	}

	@Test
	public void testFindEndDateAfter() {
		List<InterPark> tmp = interparkRepository.findByEndDateAfter(LocalDateTime.now());
		tmp.forEach(m -> {
			log.debug(m.toString());
		});
		log.debug("{}", tmp.size());
	}

	@Test
	public void testFindStartDateBeforeAndEndDate() {
		List<InterPark> tmp = interparkRepository.findByEndDateAfter(LocalDateTime.now());
		tmp.forEach(m -> {
			log.debug(m.toString());
		});
		log.debug("{}", tmp.size());
	}
}
