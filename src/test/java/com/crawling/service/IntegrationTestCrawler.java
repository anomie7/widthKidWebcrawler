package com.crawling.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.crawling.WebCrawlingPracticeApplication;
import com.crawling.domain.InterParkContent;
import com.crawling.domain.InterparkType;
import com.crawling.repository.InterParkRepository;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebCrawlingPracticeApplication.class})
@ActiveProfiles("test")//통합테스트므로 인메모리보다는 프로덕트와 동일한 환경에서 해야할듯..도커도 있으니깐 쉽게 가능할것임
@Slf4j
@Transactional
public class IntegrationTestCrawler {

    @Autowired
    private InterParkRepository interparkRepository;

    @Autowired
    private InterParkCrawler interparkCrawling;

    @Test
    public void testFindNewCrawlingData() throws Exception {
        for (InterparkType dtype : InterparkType.values()) {
            List<InterParkContent> tmp = interparkCrawling.findNewCrawlingData(dtype);
            interparkRepository.saveAll(tmp);
            log.debug("{}", tmp.size());
        }
    }

}
