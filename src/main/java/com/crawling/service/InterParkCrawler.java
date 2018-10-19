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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

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

import com.crawling.domain.Address;
import com.crawling.domain.DeleteFlag;
import com.crawling.domain.InterParkData;
import com.crawling.domain.InterparkType;
import com.crawling.domain.Price;
import com.crawling.exception.SizeNotMatchedException;
import com.crawling.repository.InterParkRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class InterParkCrawler {
	@Autowired
	private InterParkRepository interparkRepository;
	private final String URL = "http://ticket.interpark.com/TPGoodsList.asp?Ca=Fam&SubCa=";
	private final String cssQuery = ".Rk_gen2 .stit tbody tr";

	public static Address findAddressByUrl(String addressUrl) throws IOException {
		Document doc = Jsoup.connect(addressUrl).get();
		Elements el = doc
				.select("body table > tbody > tr:nth-child(2) > td:nth-child(3) > table > tbody > tr:nth-child(2)")
				.select("table > tbody > tr > td:nth-child(2)").select("table > tbody > tr:nth-child(3) > td");
		String address = el.text().replace("주 소 :", "").trim();
		return Address.convertStringToAddressObj(address);
	}

	public static String saveImgFile(String url) throws IOException {
		DateTimeFormatter dirFormattor = DateTimeFormatter.ofPattern("yyyyMMdd");
		DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

		Document doc = Jsoup.connect(url).get();
		String imgUrl = doc.getElementsByClass("poster").select("img").attr("src");

		log.debug(imgUrl);

		String folderPath = "D:/imgFolder/" + LocalDate.now().format(dirFormattor) + "/";
		String ext = imgUrl.substring(imgUrl.lastIndexOf('.') + 1, imgUrl.length()); // 이미지 확장자 추출
		String fileName = LocalDateTime.now().format(fileNameFormatter) + "." + ext;
		String saveFilePath = folderPath + fileName;
		File dir = new File(folderPath);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		BufferedImage in = ImageIO.read(new URL(imgUrl).openStream());
		ImageIO.write(in, ext, new File(saveFilePath));
		return saveFilePath;
	}

	public List<InterParkData> crawling(InterparkType dtype) throws Exception {
		Document doc = Jsoup.connect(URL + dtype.getSubCa()).get();
		Elements el = doc.select(cssQuery);
		List<InterParkData> result = new ArrayList<>();

		for (Element element : el) {
			String name = element.getElementsByClass("RKtxt").select("a").text().trim();
			Elements addressUrl = element.getElementsByClass("Rkdate").select("a");
			String location = addressUrl.text().trim();

			String date = element.child(3).text();
			String groupCode = element.select(".fw_bold a").attr("href");

			InterParkData dto = new InterParkData(null, name, location, dtype, addressUrl.attr("href"), date, groupCode);
			dto.addInterparkCode(groupCode);
			result.add(dto);
		}

		if (el.size() != result.size()) {
			throw new SizeNotMatchedException("인터파크에서 가져온 데이터와 가공한 데이터의 사이즈가 불일치합니다.");
		}

		return result;
	}

	public void save(List<InterParkData> dto) {
		interparkRepository.save(dto);
	}

	public List<InterParkData> findNewCrawlingData(InterparkType dtype) throws Exception {
		List<InterParkData> ls = crawling(dtype);
		final List<String> tmp = interparkRepository.findInterparkcodeByDtype(dtype);
		List<InterParkData> result = ls.parallelStream()
				.filter(f -> tmp.stream().noneMatch(m -> m.equals(f.getInterparkCode()))).collect(Collectors.toList());
		result.parallelStream().forEach(InterParkData::interparkConsumer);

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");

		final WebDriver driver = new ChromeDriver(chromeOptions);
		for (InterParkData obj : result) {
			try {
				if (dtype.equals(InterparkType.Ex)) {
					this.findPriceDtypeEx(driver, obj);
				} else {
					this.findPrice(driver, obj);
				}
			} catch (Exception e) {
				log.info(obj.toString());
				log.error(e.getMessage());
			}
		}
		return result;
	}

	public void findPrice(WebDriver driver, InterParkData dto) {
		String url = "http://ticket.interpark.com/" + dto.getGroupCode();
		log.debug(url);
		driver.get(url);
		dto.addInterparkCode(url);
		List<WebElement> el = driver.findElement(By.id("divSalesPrice")).findElements(By.tagName("tr"));
		List<Price> result = new ArrayList<>();
		for (WebElement tr : el) {
			List<WebElement> td = tr.findElements(By.tagName("td"));
			if (td.size() == 3) {
				Price price = new Price();
				td.forEach(n -> {
					final String text = n.getText().replaceAll(",|원", "");
					if (text != null && !text.equals(" ")) {
						Matcher matcher = Pattern.compile("(\\d[^%|층]{2,})+$").matcher(text);
						if (matcher.find()) {
							price.setPrice(Integer.parseInt(text.trim()));
							log.debug("[{}] price : {}", dto.getInterparkCode(), price.getPrice());
						} else {
							price.setName(text.trim());
							log.debug("[{}] name : {}", dto.getInterparkCode(), price.getName());
						}
					}
				});
				result.add(price);
			} else if (td.size() == 1) {
				td.forEach(n -> {
					String[] text = n.getText().split("원");
					for (String t : text) {
						Price price = new Price();
						t = t.replaceAll(",|▶", "");
						Matcher matcher = Pattern.compile("(\\d[^%|층]{2,})+$").matcher(t);
						if (matcher.find()) {
							int startIdx = matcher.start();
							String name = t.substring(0, startIdx).trim();
							String priceValue = t.substring(startIdx, t.length()).trim();
							price.setPrice(Integer.parseInt(priceValue));
							price.setName(name);
							log.debug("[{}] ul에서 추출한  {}", dto.getInterparkCode(), price.toString());
						}
						result.add(price);
					}
				});
			}
		}
		for (Price price : result) {
			dto.addPrice(price);
		}
	}

	public void findPriceDtypeEx(WebDriver driver, InterParkData dto) {
		String url = "http://ticket.interpark.com/" + dto.getGroupCode();
		log.debug(url);
		driver.get(url);
		dto.addInterparkCode(url);
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
			dto.addPrice(price);
		}
	}

	public List<InterParkData> invalidDataDelete() {
		List<InterParkData> result = interparkRepository.findByEndDateBefore(LocalDateTime.now());
		result.forEach(m -> {
			m.setDeleteflag(DeleteFlag.Y);
		});
		interparkRepository.save(result);
		return result;
	}
}
