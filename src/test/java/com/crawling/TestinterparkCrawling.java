package com.crawling;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { WebCrawlingPracticeApplication.class })
@ActiveProfiles("test")
@Slf4j
public class TestinterparkCrawling {

	@Autowired
	private InterParkRepository interparkRepository;

	@Autowired
	private InterParkCrawling interparkCrawling;

	// @Test
	// public void testWemapeCrawling() {
	// Document doc;
	// final String URL = "http://www.wemakeprice.com/main/980200";
	// final String cssQuery = ".container-inner .content-main";
	//
	// try {
	// //페이지가 동적이기 때문에 이 방식으로는 모든 페이지의 컨텐츠를 크롤링할 수 없음
	// doc = Jsoup.connect(URL).get();
	// assertEquals("크롤링한 페이지의 타이틀이 동일하지 않습니다.", doc.title(), "특가대표! 위메프");
	// log.info(doc.select(cssQuery).toString());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	@Test
	public void testInterparkCrawling() throws Exception {
		List<InterParkDTO> ls = null;
		for (InterparkType dtype  :  InterparkType.values()) {
			ls = interparkCrawling.crawling(dtype);
			log.info("result size  : {}", ls.size());
		}
	}
	

	@Test
	public void testFindNewCrawlingData() throws Exception {
		for (InterparkType dtype  :  InterparkType.values()) {
			List<InterParkDTO> tmp = interparkCrawling.findNewCrawlingData(dtype);
			interparkRepository.save(tmp);
			log.info("{}", tmp.size());
		}
	}

	@Test
	public void testFindInterparkCode() {
		List<String> tmp = null;
		tmp = interparkRepository.findInterparkcodeByDtype(InterparkType.values()[0]);
		log.info("interparkcode {} count:  {}", InterparkType.values()[0], tmp.stream().count());
	}
	
	@Test
	public void testFindEndDateBefore() {
		List<InterParkDTO> tmp = interparkCrawling.invalidDataDelete();
		log.info("{}", tmp.size());
	}
	
	@Test
	public void testFindEndDateAfter() {
		List<InterParkDTO> tmp = interparkRepository.findByEndDateAfter(LocalDateTime.now());
		tmp.forEach(m -> {log.info(m.toString() );});
		log.info("{}", tmp.size());
	}
	
//	@Test
//	public void testFindStartDateBeforeAndEndDate) {
//		List<InterParkDTO> tmp = interparkRepository.findByEndDateAfter(LocalDateTime.now());
//		tmp.forEach(m -> {log.info(m.toString() );});
//		log.info("{}", tmp.size());
//	}
	
	@Test
	public void testEnumVal() {
		InterparkType[] tmp = InterparkType.values();
		String tmp2 = InterparkType.Mu.getSubCa();
	}


	@Test
	public void testLocalDate() {
		String startDate = "2018-09-03";
		LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
		String endDate = "2019-11-02";
		LocalDateTime end = LocalDate.parse(endDate).atStartOfDay();

		assertEquals("2018-09-03T00:00", start.toString());
		assertEquals(endDate+"T00:00", end.toString());
		assertEquals(true, start.isBefore(LocalDateTime.now()));
	}

}
