package com.crawling.repository;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.crawling.WebCrawlingPracticeApplication;
import com.crawling.domain.InterParkData;
import com.crawling.domain.InterparkType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { WebCrawlingPracticeApplication.class })
@ActiveProfiles("test")
@Transactional
public class TestInterParkRepository {

	@Autowired
	private InterParkRepository interparkRepository;
	
	private List<InterParkData> testLs = new ArrayList<>();
	
	@Before
	public void generateTestObj() {
		InterParkData obj1 = InterParkData.builder().name("뽀로로1").dtype(InterparkType.Mu).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(Period.ofDays(1))).build();
		InterParkData obj2 = InterParkData.builder().name("뽀로로2").dtype(InterparkType.Cl).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(Period.ofDays(2))).build();
		InterParkData obj3 = InterParkData.builder().name("뽀로로3").dtype(InterparkType.Pl).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().minusDays(2)).build();
		InterParkData obj4 = InterParkData.builder().name("뽀로로4").dtype(InterparkType.Ex).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().minusDays(1)).build();
		
		testLs.add(obj1);
		testLs.add(obj2);
		testLs.add(obj3);
		testLs.add(obj4);
		interparkRepository.saveAll(testLs);
	}

	@Test
	public void testFindInterparkCode() {
		List<String> tmp = interparkRepository.findInterparkcodeByDtype(InterparkType.values()[0]);
		assertEquals(1, tmp.stream().count());
	}

	@Test
	public void testFindEndDateAfter() {
		List<InterParkData> tmp = interparkRepository.findByEndDateAfter(LocalDateTime.now());
		assertEquals(2, tmp.size());
	}

	@Test
	public void findByEndDateBefore() {
		List<InterParkData> tmp = interparkRepository.findByEndDateBefore(LocalDateTime.now());
		assertEquals(2, tmp.size());
	}
}
