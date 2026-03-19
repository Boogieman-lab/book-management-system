CREATE TABLE notification (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '알림 ID',
    user_id BIGINT NOT NULL COMMENT '수신자 ID',
    type VARCHAR(50) NOT NULL COMMENT '알림 유형',
    message VARCHAR(512) NOT NULL COMMENT '알림 내용',
    is_read BOOLEAN NOT NULL DEFAULT FALSE COMMENT '읽음 여부',
    related_id BIGINT NULL COMMENT '관련 엔티티 ID',
    created_at DATETIME(6) COMMENT '생성일시',
    updated_at DATETIME(6) COMMENT '수정일시',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부'
) COMMENT='알림';
