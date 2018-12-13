package com.crawling.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.crawling.domain.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crawling.domain.InterParkContent;
import com.crawling.exception.SizeNotMatchedException;
import com.crawling.repository.InterParkRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class InterParkCrawler {
    @Autowired
    private InterParkRepository interparkRepository;

    public static Address findAddressByUrl(String addressUrl) throws IOException {
        Document doc = Jsoup.connect(addressUrl).get();
        Elements el = doc
                .select("body table > tbody > tr:nth-child(2) > td:nth-child(3) > table > tbody > tr:nth-child(2)")
                .select("table > tbody > tr > td:nth-child(2)").select("table > tbody > tr:nth-child(3) > td");
        String address = el.text().replace("주 소 :", "").trim();
        return Address.convertStringToAddressObj(address);
    }

    public static String saveImgFile(String url) throws IOException {
        DateTimeFormatter dirFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

        Document doc = Jsoup.connect(url).get();
        String imgUrl = doc.getElementsByClass("poster").select("img").attr("src");

        log.debug(imgUrl);

        String folderPath = "D:/imgFolder/" + LocalDate.now().format(dirFormatter) + "/";
        String ext = imgUrl.substring(imgUrl.lastIndexOf('.') + 1, imgUrl.length()); // 이미지 확장자 추출
        String fileName = LocalDateTime.now().format(fileNameFormatter) + "." + ext;
        String saveFilePath = folderPath + fileName;
        File dir = new File(folderPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        BufferedImage in = ImageIO.read(new URL(imgUrl).openStream());
        ImageIO.write(in, ext, new File(saveFilePath));
        return "/imgFolder/" + LocalDate.now().format(dirFormatter) + "/" + fileName;
    }

    public List<InterParkContent> findNewCrawlingData(InterparkType type) throws Exception {
        List<InterParkContent> contents = crawling(type);
        final List<String> existInterparkcodes = interparkRepository.findInterparkcodeByDtype(type);

        List<InterParkContent> newContents;
        newContents = contents.parallelStream()
                .filter(content -> existInterparkcodes.stream().noneMatch(existCode -> existCode.equals(content.getInterparkCode())))
                .collect(Collectors.toList());
        newContents.parallelStream().forEach(InterParkContent::addMembers);

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");

        final WebDriver driver = new ChromeDriver(chromeOptions);
        for (InterParkContent newContent : newContents) {
            try {
                this.findPrice(driver, newContent);
            } catch (Exception e) {
                log.info(newContent.toString());
                log.error(e.getMessage());
            }
        }
        return newContents;
    }

    public List<InterParkContent> crawling(InterparkType type) throws Exception {
        String URL = "http://ticket.interpark.com/TPGoodsList.asp?Ca=Fam&SubCa=";
        Document dom = Jsoup.connect(URL + type.getSubCa()).get();

        String cssQuery = ".Rk_gen2 .stit tbody tr";
        Elements el = dom.select(cssQuery);

        List<InterParkContent> result = new ArrayList<>();
        for (Element element : el) {
            String name = element.getElementsByClass("RKtxt").select("a").text().trim();
            Elements addressUrl = element.getElementsByClass("Rkdate").select("a");
            String location = addressUrl.text().trim();

            String date = element.child(3).text();
            String groupCode = element.select(".fw_bold a").attr("href");

            InterParkContent content = new InterParkContent(null, name, location, type, addressUrl.attr("href"), date, groupCode);
            content.addInterparkCode(groupCode);
            result.add(content);
        }

        if (el.size() != result.size()) {
            throw new SizeNotMatchedException("인터파크에서 가져온 데이터와 가공한 데이터의 사이즈가 불일치합니다.");
        }

        return result;
    }


    public void findPrice(WebDriver driver, InterParkContent content) {
        String url = "http://ticket.interpark.com/" + content.getGroupCode();
        log.debug(url);
        driver.get(url);
        content.addInterparkCode(url);
        driver.findElement(By.cssSelector("img[alt=\"가격상세보기\"]")).click();
        List<Price> result = new ArrayList<>();
        List<WebElement> tb = driver.switchTo().frame("ifrTabB").findElements(By.className("tb_lv2"));
        for (WebElement webElement : tb) {
            List<WebElement> tr = webElement.findElements(By.tagName("tr"));
            tr.forEach(el -> {
                String name = el.findElement(By.cssSelector("td:nth-child(1)")).getText();
                String won = el.findElement(By.cssSelector("td:nth-child(2)")).getText().replaceAll(",|원", "");
                Price price = new Price();
                price.setName(name);
                price.setPrice(Integer.parseInt(won));
                log.debug(price.toString());
                result.add(price);
            });
        }
        for (Price price : result) {
            content.addPrice(price);
        }
    }

    public List<InterParkContent> invalidDataDelete() {
        List<InterParkContent> result = interparkRepository.findByEndDateBefore(LocalDateTime.now());
        result.forEach(content -> {
            content.setDeleteflag(DeleteFlag.Y);
        });
        interparkRepository.saveAll(result);
        return result;
    }

    public void save(List<InterParkContent> dto) {
        interparkRepository.saveAll(dto);
    }
}
