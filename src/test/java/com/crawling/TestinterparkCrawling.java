package com.crawling;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
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
		String[] Category = InterParkCrawling.CategoryArray;
		List<InterParkDTO> ls = null;
		for (int i = 0; i < Category.length; i++) {
			ls = interparkCrawling.crawling(Category[i], i);
			log.info("result size  : {}", ls.size());
		}
	}

	@Test
	public void testFindInterparkCode() {
		List<String> tmp = null;
		tmp = interparkRepository.findInterparkcodeByDtype(InterparkType.values()[0]);
		log.info("interparkcode {} count:  {}", InterparkType.values()[0], tmp.stream().count());
	}

	@Test
	public void testLocalDate() {
		String startDate = "2018-10-03";
		LocalDate start = LocalDate.parse(startDate);
		String endDate = "2019-11-02";
		LocalDate end = LocalDate.parse(endDate);

		assertEquals(startDate, start.toString());
		assertEquals(endDate, end.toString());
	}

}
