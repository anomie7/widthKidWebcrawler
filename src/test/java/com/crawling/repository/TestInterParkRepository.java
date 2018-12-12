package com.crawling.repository;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import com.crawling.domain.InterParkContent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.crawling.WebCrawlingPracticeApplication;
import com.crawling.domain.InterparkType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebCrawlingPracticeApplication.class})
@ActiveProfiles("test")
@Transactional
public class TestInterParkRepository {

    @Autowired
    private InterParkRepository interparkRepository;

    private List<InterParkContent> testLs = new ArrayList<>();

    @Before
    public void generateTestObj() {
        InterParkContent obj1 = InterParkContent.builder().name("뽀로로1").dtype(InterparkType.Mu).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(Period.ofDays(1))).build();
        InterParkContent obj2 = InterParkContent.builder().name("뽀로로2").dtype(InterparkType.Cl).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plus(Period.ofDays(2))).build();
        InterParkContent obj3 = InterParkContent.builder().name("뽀로로3").dtype(InterparkType.Pl).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().minusDays(2)).build();
        InterParkContent obj4 = InterParkContent.builder().name("뽀로로4").dtype(InterparkType.Ex).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().minusDays(1)).build();

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
        List<InterParkContent> tmp = interparkRepository.findByEndDateAfter(LocalDateTime.now());
        assertEquals(2, tmp.size());
    }

    @Test
    public void findByEndDateBefore() {
        List<InterParkContent> tmp = interparkRepository.findByEndDateBefore(LocalDateTime.now());
        assertEquals(2, tmp.size());
    }
}
