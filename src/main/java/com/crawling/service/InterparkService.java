package com.crawling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crawling.domain.InterParkData;
import com.crawling.domain.SearchVO;
import com.crawling.repository.InterParkRepository;
import com.crawling.repository.InterparkPredicateProvider;
import com.crawling.web.EventResponse;

@Service
public class InterparkService {
	
	@Autowired
	private InterParkRepository interparkRepository;
	
	@Transactional(readOnly=true)
	public EventResponse searchEvent(SearchVO search, Pageable pageable) {
		Page<InterParkData> event = interparkRepository.findAll(InterparkPredicateProvider.getSearchPredicate(search)
																,pageable);
		EventResponse res = EventResponse.fromEntity(event.getContent());
		res.addPageInfo(event);
		return res;
	}

}
