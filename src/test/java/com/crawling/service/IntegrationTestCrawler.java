package com.crawling.service;

import java.util.List;

import org.junit.Ignore;
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

@Ignore("통합테스트라 시간이 너무 오래 걸림(11m 18 s) 테스트 필요할 시에만 annotation 제거하고 실행할 것")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebCrawlingPracticeApplication.class})
@ActiveProfiles("test")
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
