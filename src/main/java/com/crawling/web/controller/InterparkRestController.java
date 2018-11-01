package com.crawling.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crawling.domain.SearchVO;
import com.crawling.service.InterparkService;
import com.crawling.web.response.EventResponse;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin(origins="*")
@RestController
public class InterparkRestController {
	@Autowired
	private InterparkService interpakrService;
	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)"),
		@ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page.") })
	@GetMapping("/event")
	public ResponseEntity<EventResponse> getEvent(SearchVO search, @ApiIgnore Pageable pageable){
		EventResponse searchEvent = interpakrService.searchEvent(search, pageable);
		return ResponseEntity.ok().body(searchEvent);
	}
}
