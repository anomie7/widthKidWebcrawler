package com.crawling;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestJsoup {
	
	@Autowired
	private InterParkRepository interparkRepository;
	
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
		Elements el = null;
		try {
			for (int j = 0; j < CategoryArray.length; j++) {
				List<InterParkDTO> ls = new ArrayList<>();
				doc = Jsoup.connect(URL + CategoryArray[j]).get();
				el = doc.select(cssQuery);
				for (Element element : el) {
					String name = element.getElementsByClass("RKtxt").select("a").text();
					String location = element.getElementsByClass("Rkdate").select("a").text();
					String date = element.child(3).text();
					String groupCode = element.select(".fw_bold a").attr("href");
					
					//groupCode 추출해서 interParkCode로 변환
					String pattern = "^*.*=";
					Pattern r = Pattern.compile(pattern);
					Matcher m = r.matcher(groupCode);
					String interparkCode = m.replaceAll("");
					
					String[] tm = date.split("~");
					String start = tm[0].replace(".", "-");
					String end = tm[1].replace(".", "-");
					LocalDate startDate = LocalDate.parse(start.trim());
					LocalDate endDate = LocalDate.parse(end.trim());
					InterParkDTO tmp = new InterParkDTO(null, name, location, interparkCode, InterparkType.values()[j], startDate, endDate);
					ls.add(tmp);
				}
				
				interparkRepository.save(ls);
				List<InterParkDTO> tm = interparkRepository.findAllByDtype(InterparkType.values()[j]);
				
				assertEquals("크롤링한 페이지의 타이틀이 동일하지 않습니다.", doc.title(), "싸니까 믿으니까 - 인터파크 티켓");
				assertEquals("처리 전 데이터와 처리 후 데이터의 List size가 불일치합니다.",el.size(), ls.size());
				for(int i = 0; i < tm.size(); i++) {
					assertEquals("카테고리별로 데이터베이스에 값이 들어가지 않았습니다.",tm.get(i), ls.get(i));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			
		}
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
