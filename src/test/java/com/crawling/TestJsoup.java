package com.crawling;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestJsoup {

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
	public void testInterparkCrawling() {
		Document doc;
		final String[] CategoryArray = { "Fam_M", "Fam_P", "Fam_C", "Fam_L" };
		final String URL = "http://ticket.interpark.com/TPGoodsList.asp?Ca=Fam&SubCa=";
		final String cssQuery = ".Rk_gen2 .stit tbody tr";
		List<Map<String, Object>> ls = new ArrayList<>();
		Elements el = null;
		try {
			for (String subCa : CategoryArray) {
				Map<String, Object> map = new HashMap<>();
				doc = Jsoup.connect(URL + subCa).get();
				assertEquals("크롤링한 페이지의 타이틀이 동일하지 않습니다.", doc.title(), "싸니까 믿으니까 - 인터파크 티켓");
				el = doc.select(cssQuery);
				for (Element element : el) {
					String tmp = element.getElementsByClass("RKtxt").select("a").text();
					map.put("name", tmp);
				}
				
				for (Element element : el) {
					String tmp = element.getElementsByClass("Rkdate").select("a").text();
					map.put("location", tmp);
				}

				for (Element element : el) {
					String tmp = element.child(3).text();
					String[] tm = tmp.split("~");
					String startDate = tm[0];
					String endDate = tm[1];
					map.put("startDate", startDate);
					map.put("endDate", endDate);
				}
				ls.add(map);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			assertEquals("처리 전 데이터와 처리 후 데이터의 List size가 불일치합니다.",el.size(), ls.size());
		}
	}

}
