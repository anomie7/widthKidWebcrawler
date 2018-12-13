package com.crawling.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.crawling.domain.InterParkContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crawling.domain.InterparkType;

public interface InterParkRepository extends JpaRepository<InterParkContent, Long> {
	public List<InterParkContent> findAllByDtype(InterparkType dtype);

	public List<InterParkContent> findByEndDateBefore(LocalDateTime now);

	public List<InterParkContent> findByEndDateAfter(LocalDateTime now);

	@Query("select i.interparkCode from InterParkContent i where i.dtype = :dtype")
	public List<String> findInterparkcodeByDtype(@Param("dtype") InterparkType dtype);
}
