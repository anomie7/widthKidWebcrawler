package com.crawling.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createDateTime;

    @LastModifiedDate
    @Column(name="last_modified_at", updatable = true)
    private LocalDateTime lastModifiedDateTime;
}
