package com.crawling.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.crawling.domain.DeleteFlag;
import com.crawling.domain.InterParkData;
import com.crawling.domain.InterparkType;
import com.crawling.domain.Price;
import com.crawling.domain.QInterParkData;
import com.jayway.jsonpath.Option;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TestQueryDsl {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private InterParkRepository interparkRepository;
	private List<InterParkData> testLs = new ArrayList<>();
	private List<Price> prices = new ArrayList<>();
	private List<Price> prices2 = new ArrayList<>();
	private List<Price> prices3 = new ArrayList<>();
	private List<Price> prices4 = new ArrayList<>();

	@Before
	public void generateTestObj() {
		prices.add(new Price(null, "일반가", 10000));
		prices.add(new Price(null, "특별가", 20000));
		prices.add(new Price(null, "상상가", 30000));
		prices.add(new Price(null, "부모동반", 6000));

		prices2.add(new Price(null, "일반가2", 20000));
		prices2.add(new Price(null, "특별가2", 40000));
		prices2.add(new Price(null, "상상가2", 60000));
		prices2.add(new Price(null, "부모동반2", 12000));
		
		prices3.add(new Price(null, "일반가2", 20000));
		prices3.add(new Price(null, "특별가2", 40000));
		prices3.add(new Price(null, "상상가2", 60000));
		prices3.add(new Price(null, "부모동반2", 12000));
		
		prices4.add(new Price(null, "일반가2", 20000));
		prices4.add(new Price(null, "특별가2", 40000));
		prices4.add(new Price(null, "상상가2", 60000));
		prices4.add(new Price(null, "부모동반2", 12000));

		InterParkData obj1 = new InterParkData(null, "뽀로로1", null, null, null, null, InterparkType.Mu, new ArrayList<>(),
				LocalDateTime.now(), LocalDateTime.now().plus(Period.ofDays(1)), null, null, null);
		InterParkData obj2 = new InterParkData(null, "뽀로로2", null, null, null, null, InterparkType.Cl, new ArrayList<>(),
				LocalDateTime.now(), LocalDateTime.now().plus(Period.ofDays(2)), null, null, null);
		InterParkData obj3 = new InterParkData(null, "뽀로로3", null, null, null, null, InterparkType.Pl, new ArrayList<>(),
				LocalDateTime.now(), LocalDateTime.now().minusDays(2), null, null, null);
		InterParkData obj4 = new InterParkData(null, "뽀로로4", null, null, null, null, InterparkType.Ex, new ArrayList<>(),
				LocalDateTime.now(), LocalDateTime.now().minusDays(1), null, null, null);
		
		obj1.addPrice(prices);
		obj2.addPrice(prices2);
		obj3.addPrice(prices3);
		obj4.addPrice(prices4);
		testLs.add(obj1);
		testLs.add(obj2);
		testLs.add(obj3);
		testLs.add(obj4);

		interparkRepository.saveAll(testLs);
	}

	@Test
	public void testNomalQuery() {
		QInterParkData data = QInterParkData.interParkData;
		JPAQuery query = new JPAQuery(em);
		query.from(data).where(data.deleteflag.eq(DeleteFlag.N)).orderBy(data.startDate.asc());
		List<InterParkData> ls = query.fetch();
		
		assertEquals(prices, ls.get(0).getPrice());
		assertEquals(prices2, ls.get(1).getPrice());
		assertEquals(prices3, ls.get(2).getPrice());
		assertEquals(prices4, ls.get(3).getPrice());
	}
	
	@Test
	public void testUpdateQuery() {
		QInterParkData data = QInterParkData.interParkData;
		JPAQuery query = new JPAQuery(em);
		
		new JPAUpdateClause(em, data).where(data.id.eq(1L)).set(data.name, "뽀통령").execute();
		query.select(data.name).from(data).where(data.id.eq(1L));
		Object obj = query.fetchOne();
		assertEquals("뽀통령", obj.toString());
	}
	
	@Test
	public void testDynamicQuery() {
		QInterParkData data = QInterParkData.interParkData;
		JPAQuery query = new JPAQuery(em);
		BooleanBuilder build = new BooleanBuilder();
		String name = "뽀로로4";
		InterparkType dtype = InterparkType.Ex;
		
		Optional<String> nameOpt = Optional.ofNullable(name);
		Optional<InterparkType> dtypeOpt = Optional.ofNullable(dtype);
		
		nameOpt.ifPresent(s -> build.and(data.name.eq(s)));
		dtypeOpt.ifPresent(t -> build.and(data.dtype.eq(t)));
		
		query.from(data).where(build);
		InterParkData obj = (InterParkData) query.fetchOne();
		
		assertEquals(nameOpt.get(), obj.getName());
		assertEquals(dtypeOpt.get(), obj.getDtype());
	}

}
