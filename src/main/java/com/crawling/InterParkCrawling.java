package com.crawling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	final static String[] CategoryArray = { "Fam_M", "Fam_P", "Fam_C", "Fam_L" };
	private final String URL = "http://ticket.interpark.com/TPGoodsList.asp?Ca=Fam&SubCa=";
	private final String cssQuery = ".Rk_gen2 .stit tbody tr";

	public List<InterParkDTO> crawling(String category, int i) throws Exception {
		try {
			Document doc = Jsoup.connect(URL + category).get();
			Elements el = doc.select(cssQuery);
			List<InterParkDTO> result = new ArrayList<>();
			
			for (Element element : el) {
				String name = element.getElementsByClass("RKtxt").select("a").text().trim();
				String location = element.getElementsByClass("Rkdate").select("a").text().trim();
				String date = element.child(3).text();
				String groupCode = element.select(".fw_bold a").attr("href");
				
				InterParkDTO tmp = new InterParkDTO(null, name, location, InterparkType.values()[i]);
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
		}
		return null;
	}
	
	public void save(List<InterParkDTO> dto) {
		interparkRepository.save(dto);
	}
}
