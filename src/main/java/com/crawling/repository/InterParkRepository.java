package com.crawling.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.crawling.domain.InterParkData;
import com.crawling.domain.InterparkType;
import com.crawling.domain.QInterParkData;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

public interface InterParkRepository extends JpaRepository<InterParkData, Long>, QuerydslPredicateExecutor<InterParkData> {
	public List<InterParkData> findAllByDtype(InterparkType dtype);

	public List<InterParkData> findByEndDateBefore(LocalDateTime now);

	public List<InterParkData> findByEndDateAfter(LocalDateTime now);

	@Query("select i.interparkCode from InterParkData i where i.dtype = :dtype")
	public List<String> findInterparkcodeByDtype(@Param("dtype") InterparkType dtype);
	
	public default Predicate getPredicate(Optional<String> nameOpt, Optional<InterparkType> dtypeOpt) {
		BooleanBuilder build = new BooleanBuilder();
		QInterParkData data = QInterParkData.interParkData;
		nameOpt.ifPresent(s -> build.and(data.name.eq(s)));
		dtypeOpt.ifPresent(t -> build.and(data.dtype.eq(t)));
		return build;
	}
}
