package com.crawling;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crawling.exception.SizeNotMatchedException;

import lombok.extern.slf4j.Slf4j;

@Service @Slf4j
public class InterParkCrawling {
	@Autowired
	private InterParkRepository interparkRepository;
	private final String URL = "http://ticket.interpark.com/TPGoodsList.asp?Ca=Fam&SubCa=";
	private final String cssQuery = ".Rk_gen2 .stit tbody tr";

	public List<InterParkDTO> crawling(InterparkType dtype) throws Exception {
		try {
			Document doc = Jsoup.connect(URL + dtype.getSubCa()).get();
			Elements el = doc.select(cssQuery);
			List<InterParkDTO> result = new ArrayList<>();
			
			for (Element element : el) {
				String name = element.getElementsByClass("RKtxt").select("a").text().trim();
				Elements addressUrl = element.getElementsByClass("Rkdate").select("a");
				String location = addressUrl.text().trim();
				String date = element.child(3).text();
				String groupCode = element.select(".fw_bold a").attr("href");
				InterParkDTO tmp = new InterParkDTO(null, name, location, dtype);
				
				Address address = findAddressByUrl(addressUrl.attr("href"));
				
				String groupCodeUrl = "http://ticket.interpark.com/" + groupCode;
				tmp.addImageFilePath(saveImgFile(groupCodeUrl));
				
				tmp.addAddress(address);
				tmp.addStartDateAndEndDate(date);
				tmp.addInterparkCode(groupCode);
				
				result.add(tmp);
			}
			
			if(el.size() != result.size()) {
				throw new SizeNotMatchedException("인터파크에서 가져온 데이터와 가공한 데이터의 사이즈가 불일치합니다.");
			}
			
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void save(List<InterParkDTO> dto) {
		interparkRepository.save(dto);
	}
	
	//크롤링 과정 중에 모든 이미지를 받아버림..이미 db에 존재하거나 무효한 데이터는 이미지를 저장하지 않도록 해야함
	public List<InterParkDTO> findNewCrawlingData(InterparkType dtype) throws Exception {
		List<InterParkDTO> ls = crawling(dtype);
		final List<String> tmp = interparkRepository.findInterparkcodeByDtype(dtype);
		List<InterParkDTO> result = ls.stream()
				.filter(f -> tmp.stream().noneMatch(m -> m.equals(f.getInterparkCode())))
				.collect(Collectors.toList());
		return result;
	}
	
	public String saveImgFile(String url) throws IOException {
		DateTimeFormatter dirFormattor = DateTimeFormatter.ofPattern("yyyyMMdd");
		DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		
		Document doc = Jsoup.connect(url).get();
		String imgTagSrc = doc.getElementsByClass("poster").select("img").attr("src");
		
		log.debug(imgTagSrc);
		
		String filePath = "D:/imgFolder/" + LocalDate.now().format(dirFormattor) + "/";
		String fileName = LocalDateTime.now().format(fileNameFormatter);
		String saveFilePath = filePath + fileName;
		File dir = new File(filePath);

		InputStream is = null;
		BufferedOutputStream os = null;

		if (!dir.exists()) {
			dir.mkdirs();
		}

		URL fileUrl;
		try {
			fileUrl = new URL(imgTagSrc);
			URLConnection urlConn = fileUrl.openConnection();
			urlConn.connect();
			is = urlConn.getInputStream();
			os = new BufferedOutputStream(new FileOutputStream(saveFilePath));

			int readBytes = 0;

			byte[] buf = new byte[4096];

			while ((readBytes = is.read(buf)) != -1) {
				os.write(buf, 0, readBytes);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return saveFilePath;
	}

	public List<InterParkDTO> invalidDataDelete() {
		List<InterParkDTO> result = interparkRepository.findByEndDateBefore(LocalDateTime.now());
		result.forEach(m -> {m.setDeleteflag(DeleteFlag.Y);});
		interparkRepository.save(result);
		return result;
	}

	public Address findAddressByUrl(String addressUrl) throws IOException {
		Document doc = Jsoup.connect(addressUrl).get();
		Elements el = doc.select("body table > tbody > tr:nth-child(2) > td:nth-child(3) > table > tbody > tr:nth-child(2)")
						 .select("table > tbody > tr > td:nth-child(2)")
						 .select("table > tbody > tr:nth-child(3) > td");
		String address =  el.text().replace("주 소 :", "").trim();
		return Address.convertStringToAddressObj(address);
	}
}
