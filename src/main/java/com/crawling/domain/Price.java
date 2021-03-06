package com.crawling.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@Entity @ToString
@EqualsAndHashCode
public class Price extends BaseEntity{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "PRICE_ID")
	private Long id;
	private String name;
	private int price;
	private boolean defaultPrice;
	private String ticketInfo;
	private String extraInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INTERPARK_ID")
	private InterParkContent interpark;

	public Price(Long id, String name, int price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}
}
