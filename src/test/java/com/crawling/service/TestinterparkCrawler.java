package com.crawling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import com.crawling.WebCrawlingPracticeApplication;
import com.crawling.domain.Address;
import com.crawling.domain.InterParkContent;
import com.crawling.domain.InterparkType;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebCrawlingPracticeApplication.class})
@ActiveProfiles("test")
@Slf4j
@Transactional
public class TestinterparkCrawler {

    @Autowired
    private InterParkCrawler interparkCrawling;

    @Test
    public void testCrawling() throws Exception {
        List<InterParkContent> ls = null;
        for (InterparkType dtype : InterparkType.values()) {
            ls = interparkCrawling.crawling(dtype);
            log.debug("result size  : {}", ls.size());
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

        String[] urlArr = {url1, url2, url3, url4, url5, url6, url7};

        List<Address> ls = new ArrayList<>();
        for (String addressUrl : urlArr) {
            Address address = InterParkCrawler.findAddressByUrl(addressUrl);
            ls.add(address);
        }

        assertThat(ls.get(0).getCity()).isEqualTo("서울특별시");
        assertThat(ls.get(1).getProvince()).isEqualTo("경상남도");
        assertThat(ls.get(2).getProvince()).isEqualTo("충남");
        assertThat(ls.get(3).getCity()).isEqualTo("서울");
        assertThat(ls.get(4).getCity()).isEqualTo("서울시");
        assertThat(ls.get(5).getCity()).isEqualTo(null);
        assertThat(ls.get(6)).isNull();
        ;
    }

    @Test
    public void testsaveImgFile() {
        String url = "http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=18013439";
        String fullFilePath = "D:\\imgFolder/test.gif";
        try {
            fullFilePath = InterParkCrawler.saveImgFile(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals("저장한 이미지가 존재하지 않습니다.", true, new File(fullFilePath).exists());
    }

    @Test
    public void testFIndPrice() throws Exception {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");

        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver(chromeOptions);

        for (InterparkType dtype : InterparkType.values()) {
            List<InterParkContent> ls;
            ls = interparkCrawling.crawling(dtype).stream().limit(10).collect(Collectors.toList());
            for (InterParkContent interParkDTO : ls) {
                try {
                    interparkCrawling.findPrice(driver, interParkDTO);
                } catch (Exception e) {
                    log.info(interParkDTO.toString());
                    log.error(e.getMessage());
                }
            }
            interparkCrawling.save(ls);
        }
    }

    @Test
    public void testFindEndDateBefore() {
        List<InterParkContent> tmp = interparkCrawling.invalidDataDelete();
        log.debug("{}", tmp.size());
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
