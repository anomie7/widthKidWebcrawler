package com.crawling;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterParkRepository extends JpaRepository<InterPark, Long> {
	public List<InterPark> findAllByDtype(InterparkType dtype);

	public List<InterPark> findByEndDateBefore(LocalDateTime now);

	public List<InterPark> findByEndDateAfter(LocalDateTime now);

	@Query("select i.interparkCode from InterPark i where i.dtype = :dtype")
	public List<String> findInterparkcodeByDtype(@Param("dtype") InterparkType dtype);
}
