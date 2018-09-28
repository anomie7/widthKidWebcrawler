package com.crawling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity @Table(name="INTERPART_CRAWLING_DATA")
@NoArgsConstructor @ToString
@Getter @EqualsAndHashCode
public class InterParkDTO {
	@Id  
	@GeneratedValue
	@Column(name = "INTERPART_ID")
	private Long id;
	private String name;
	private String location;
	
	@Column(unique=true)
	private String interparkCode;
	
	@Enumerated(EnumType.STRING)
	private DeleteFlag deleteflag = DeleteFlag.N;
	
	@Enumerated(EnumType.STRING)
	private InterparkType dtype;
	
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	
	public InterParkDTO(Long id, String name, String location, InterparkType dtype) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.dtype = dtype;
	}

	public void addStartDateAndEndDate(String date) {
		String[] tm = date.split("~");
		String start = tm[0].replace(".", "-");
		String end = tm[1].replace(".", "-");
		this.startDate = LocalDate.parse(start.trim()).atStartOfDay();
		this.endDate = LocalDate.parse(end.trim()).atStartOfDay();
	}

	public void addInterparkCode(String groupCode) {
		String pattern = "^*.*=";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(groupCode);
		this.interparkCode = m.replaceAll("");
	}
}
