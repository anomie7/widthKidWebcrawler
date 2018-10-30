package com.crawling.repository;

import com.crawling.domain.DeleteFlag;
import com.crawling.domain.QInterParkData;
import com.crawling.domain.SearchVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

public class InterparkPredicateProvider {
	public static Predicate getSearchPredicate(SearchVO search) {
		BooleanBuilder build = new BooleanBuilder();
		QInterParkData data = QInterParkData.interParkData;
		search.getCity().ifPresent(city -> build.and(data.address.city.contains(city)));
		search.getKindOf().ifPresent(kind -> build.and(data.dtype.eq(kind)));

		if (search.getStartDateTime().isPresent() && search.getEndDateTime().isPresent()) {
			build.and(data.startDate.before(search.getEndDateTime().get())
					.and(data.endDate.after(search.getStartDateTime().get())));
		}

		build.and(data.deleteflag.eq(DeleteFlag.N));

		return build;
	}
}
