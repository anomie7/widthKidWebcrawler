package com.crawling;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Price {
	@Id
	@GeneratedValue
	@Column(name = "PRICE_ID")
	private Long id;
	private String name;
	private int price;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INTERPARK_CODE")
	private InterParkDTO interpark;
}
