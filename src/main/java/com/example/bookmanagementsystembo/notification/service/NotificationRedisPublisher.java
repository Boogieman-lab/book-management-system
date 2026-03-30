package com.example.bookmanagementsystembo.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 알림 이벤트를 Redis 채널로 발행합니다.
 * 채널: notification:user:{userId}
 * 동일 인스턴스 및 다른 인스턴스의 구독자 모두에게 전달됩니다.
 */
@Component
@RequiredArgsConstructor
public class NotificationRedisPublisher {

    private static final String CHANNEL_PREFIX = "notification:user:";

    private final StringRedisTemplate redisTemplate;

    /**
     * 특정 사용자 채널로 알림 JSON 메시지를 발행합니다.
     *
     * @param userId  수신 대상 사용자 ID
     * @param payload JSON 직렬화된 알림 데이터
     */
    public void publish(Long userId, String payload) {
        redisTemplate.convertAndSend(CHANNEL_PREFIX + userId, payload);
    }
}
