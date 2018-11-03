package com.crawling.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.crawling.domain.DeleteFlag;
import com.crawling.domain.InterparkType;
import com.crawling.domain.QInterParkData;
import com.crawling.domain.SearchVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

public class InterparkPredicateProvider {
	public static Predicate getSearchPredicate(SearchVO search) {
		Optional<String> cityOpt = Optional.ofNullable(search.getRegion());
		Optional<InterparkType> dtypeOpt = Optional.ofNullable(search.getKindOf());
		Optional<LocalDateTime> startOpt = Optional.ofNullable(search.getStartDate());
		Optional<LocalDateTime> endOpt = Optional.ofNullable(search.getEndDate());
		
		BooleanBuilder build = new BooleanBuilder();
		QInterParkData data = QInterParkData.interParkData;
		cityOpt.ifPresent(city -> build.and(data.address.city.contains(city)));
		dtypeOpt.ifPresent(kind -> build.and(data.dtype.eq(kind)));

		if (startOpt.isPresent() && endOpt.isPresent()) {
			build.and(data.startDate.before(endOpt.get())
					.and(data.endDate.after(startOpt.get())));
		}

		build.and(data.deleteflag.eq(DeleteFlag.N));

		return build;
	}
}
