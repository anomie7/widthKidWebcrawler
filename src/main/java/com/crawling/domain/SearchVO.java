package com.crawling.domain;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SearchVO {
	private Optional<String> city;
	private Optional<InterparkType> kindOf;
	private Optional<LocalDateTime> startDateTime;
	private Optional<LocalDateTime> endDateTime;
}
