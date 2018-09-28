package com.crawling;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterParkRepository extends JpaRepository<InterParkDTO, Long> {
	public List<InterParkDTO> findAllByDtype(InterparkType dtype);

	public List<InterParkDTO> findByEndDateBefore(LocalDateTime now);

	public List<InterParkDTO> findByEndDateAfter(LocalDateTime now);

	@Query("select i.interparkCode from InterParkDTO i where i.dtype = :dtype")
	public List<String> findInterparkcodeByDtype(@Param("dtype") InterparkType dtype);
}
