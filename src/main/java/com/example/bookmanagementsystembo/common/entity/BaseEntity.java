package com.example.bookmanagementsystembo.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 JPA 엔티티의 공통 베이스 클래스.
 * - Spring Data Auditing으로 createdAt/updatedAt 자동 설정
 * - isDeleted 플래그를 이용한 Soft Delete 지원 (Hard Delete 금지)
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    /** 레코드 최초 생성 일시 (INSERT 시 자동 설정, 이후 변경 불가) */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성일시")
    private LocalDateTime createdAt;

    /** 레코드 최종 수정 일시 (UPDATE 시 자동 갱신) */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정일시")
    private LocalDateTime updatedAt;

    /**
     * Soft Delete 플래그.
     * true = 논리 삭제 상태. @Where(clause = "is_deleted = false") 로 자동 필터링됨.
     */
    @Column(name = "is_deleted", nullable = false)
    @Comment("소프트 삭제 여부 (true: 삭제됨)")
    private boolean isDeleted = false;
}