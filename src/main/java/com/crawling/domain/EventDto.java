package com.crawling.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
	private Long eventId;
	private String name;
	private String location;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private InterparkType kindOf;
	@Builder.Default
	private List<Price> price = new ArrayList<>();

	public static EventDto fromEntity(InterParkData entity) {
		EventDto dto = EventDto.builder()
				.eventId(entity.getId())
				.name(entity.getName())
				.location(entity.getLocation())
				.startDate(entity.getStartDate())
				.endDate(entity.getEndDate())
				.price(entity.getPrice())
				.kindOf(entity.getDtype())
				.build();
		return dto;
	}
}
