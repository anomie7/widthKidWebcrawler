package com.crawling;

import java.io.IOException;
import java.time.LocalDateTime;
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

@Service
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

	public List<InterParkDTO> findNewCrawlingData(InterparkType dtype) throws Exception {
		List<InterParkDTO> ls = crawling(dtype);
		final List<String> tmp = interparkRepository.findInterparkcodeByDtype(dtype);
		List<InterParkDTO> result = ls.stream()
				.filter(f -> tmp.stream().noneMatch(m -> m.equals(f.getInterparkCode())))
				.collect(Collectors.toList());
		return result;
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
