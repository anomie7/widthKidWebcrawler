package com.crawling;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter @EqualsAndHashCode
@AllArgsConstructor
public class InterParkDTO {
	@Id  
	@GeneratedValue
	@Column(name = "INTERPART_ID")
	private Long id;
	private String name;
	private String location;
	private String interparkCode;
	
	@Enumerated
	private InterparkType dtype;
	
	private LocalDate startDate;
	private LocalDate endDate;
	
}
