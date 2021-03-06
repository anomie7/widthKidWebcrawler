package com.crawling.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class PriceDto {
	private Long id;
	private String name;
	private int price;
	
	public static PriceDto fromEntity(Price price) {
		return PriceDto.builder().id(price.getId()).name(price.getName()).price(price.getPrice()).build();
	}
}
