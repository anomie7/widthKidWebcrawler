package com.crawling;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	// log.debug(doc.select(cssQuery).toString());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	@Test
	public void testCrawling() throws Exception {
		List<InterParkDTO> ls = null;
		for (InterparkType dtype : InterparkType.values()) {
			ls = interparkCrawling.crawling(dtype);
			log.debug("result size  : {}", ls.size());
		}
	}

	@Test
	public void testFindNewCrawlingData() throws Exception {
		for (InterparkType dtype : InterparkType.values()) {
			List<InterParkDTO> tmp = interparkCrawling.findNewCrawlingData(dtype);
			interparkRepository.save(tmp);
			log.debug("{}", tmp.size());
		}
	}

	@Test
	public void testFindAddress() throws IOException {
		// 서울특별시
		String url1 = "http://ticket.interpark.com/TPPlace/Main/TPPlace_Detail.asp?PlaceCode=12010451&PlaceOfFlag=";
		// 경상남도
		String url2 = "http://ticket.interpark.com/TPPlace/Main/TPPlace_Detail.asp?PlaceCode=12011427&PlaceOfFlag=";
		// 충남 천안
		String url3 = "http://ticket.interpark.com/TPPlace/Main/TPPlace_Detail.asp?PlaceCode=17000392&PlaceOfFlag=";
		// 서울 강남구
		String url4 = "http://ticket.interpark.com/TPPlace/Main/TPPlace_Detail.asp?PlaceCode=11010461&PlaceOfFlag=";
		// 서울시 광진구 능동 25번지
		String url5 = "http://ticket.interpark.com/TPPlace/Main/TPPlace_Detail.asp?PlaceCode=18000545&PlaceOfFlag=";
		// 덕충동 덕충안길
		String url6 = "http://ticket.interpark.com/TPPlace/Main/TPPlace_Detail.asp?PlaceCode=17000697&PlaceOfFlag=";
		// empty
		String url7 = "http://ticket.interpark.com/TPPlace/Main/TPPlace_Detail.asp?PlaceCode=17000853&PlaceOfFlag=";

		String[] urlArr = { url1, url2, url3, url4, url5, url6, url7 };

		for (String addressUrl : urlArr) {
			Address address = interparkCrawling.findAddressByUrl(addressUrl);
		}
	}

	@Test // 보류 이미지 파일 받은 이후에
	public void testsaveImgFile() {
		String url = "http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GroupCode=18009908";
		String fullFilePath = null;
		try {
			fullFilePath = interparkCrawling.saveImgFile(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("저장한 이미지가 존재하지 않습니다.", true, new File(fullFilePath).exists());
		// String tmp = doc.select(".info_Div").text();
	}

	@Test
	public void testAllSavePriceUsingSelenium() throws Exception {

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");

		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
		WebDriver driver = new ChromeDriver(chromeOptions);

		for (InterparkType dtype : InterparkType.values()) {
			List<InterParkDTO> ls;
			ls = interparkCrawling.crawling(dtype);

			for (InterParkDTO interParkDTO : ls) {
				try {
					interparkCrawling.findPrice(driver, interParkDTO);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}

	}

	@Test
	public void testFindSomePrice() {
//		final String[] priceUrl = { "/Ticket/Goods/GoodsInfo.asp?GroupCode=18013627", // 시크릿
//				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18009908", // 먹구렁이와 생일파티
//				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18013051", // 황글별을 찾아라
//				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18011242", // 미니특공대x
//				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18012269", // 버블매직쇼
//				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18012422", // 베이블레이드 버스트 갓
//				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18011430", // 포켓다이노
//				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18012942" // 무지개 물고기
//		};
		
		final String[] priceUrl = { 
				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18013243",
				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18012678",
				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18008744",
				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18010761",
				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18010717",
				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18010706",
				"/Ticket/Goods/GoodsInfo.asp?GroupCode=18012270"
		};

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");

		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
		WebDriver driver = new ChromeDriver(chromeOptions);

		for (int i = 0; i < priceUrl.length; i++) {
			InterParkDTO dto = new InterParkDTO(null, null, null, null, null, null, priceUrl[i]);
			try {
				interparkCrawling.findPrice(driver, dto);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	@Test
	public void testFindInterparkCode() {
		List<String> tmp = null;
		tmp = interparkRepository.findInterparkcodeByDtype(InterparkType.values()[0]);
		log.debug("interparkcode {} count:  {}", InterparkType.values()[0], tmp.stream().count());
	}

	@Test
	public void testFindEndDateBefore() {
		List<InterParkDTO> tmp = interparkCrawling.invalidDataDelete();
		log.debug("{}", tmp.size());
	}

	@Test @Transactional
	public void testFindEndDateAfter() {
		List<InterParkDTO> tmp = interparkRepository.findByEndDateAfter(LocalDateTime.now());
		tmp.forEach(m -> {
			log.debug(m.toString());
		});
		log.debug("{}", tmp.size());
	}

	// @Test
	// public void testFindStartDateBeforeAndEndDate) {
	// List<InterParkDTO> tmp =
	// interparkRepository.findByEndDateAfter(LocalDateTime.now());
	// tmp.forEach(m -> {log.debug(m.toString() );});
	// log.debug("{}", tmp.size());
	// }

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

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		log.debug(LocalDate.now().format(formatter));
		log.debug(LocalDateTime.now().format(formatter2));

		assertEquals("2018-09-03T00:00", start.toString());
		assertEquals(endDate + "T00:00", end.toString());
		assertEquals(true, start.isBefore(LocalDateTime.now()));
	}

}
