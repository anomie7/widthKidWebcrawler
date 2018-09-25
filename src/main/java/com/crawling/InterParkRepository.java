package com.crawling;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterParkRepository extends JpaRepository<InterParkDTO, Long> {
	public List<InterParkDTO> findAllByDtype(InterparkType dtype);

}
