package com.crawling.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	private List<PriceDto> price = new ArrayList<>();

	public static EventDto fromEntity(InterParkData entity) {
		EventDto dto = EventDto.builder()
				.eventId(entity.getId())
				.name(entity.getName())
				.location(entity.getLocation())
				.startDate(entity.getStartDate())
				.endDate(entity.getEndDate())
				.kindOf(entity.getDtype())
				.build();
		dto.addPrice(entity.getPrice());
		return dto;
	}

	private void addPrice(List<Price> price) {
		this.price = price.stream().map(PriceDto::fromEntity).collect(Collectors.toList());
	}
}
