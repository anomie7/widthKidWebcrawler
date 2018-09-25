package com.crawling;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name="INTERPART_CRAWLING_DATA")
@NoArgsConstructor
@Getter @EqualsAndHashCode
public class InterParkDTO {
	@Id  
	@GeneratedValue
	@Column(name = "INTERPART_ID")
	private Long id;
	private String name;
	private String location;
	private String interparkCode;
	private char delete = 'N';
	
	@Enumerated(EnumType.STRING)
	private InterparkType dtype;
	
	private LocalDate startDate;
	private LocalDate endDate;
	
	public InterParkDTO(Long id, String name, String location, String interparkCode, InterparkType dtype,
			LocalDate startDate, LocalDate endDate) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.interparkCode = interparkCode;
		this.dtype = dtype;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
