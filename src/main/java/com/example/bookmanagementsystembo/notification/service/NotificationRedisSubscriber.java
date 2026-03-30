package com.example.bookmanagementsystembo.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Redis Pub/Sub 구독자.
 * notification:user:{userId} 채널에서 메시지를 수신하여 SSE로 전달합니다.
 * 사용자가 이 인스턴스에 연결되지 않은 경우 SseEmitterManager가 조용히 무시합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRedisSubscriber implements MessageListener {

    private final SseEmitterManager sseEmitterManager;

    /**
     * Redis 채널에서 메시지를 수신하면 채널명에서 userId를 추출하여 SSE로 전달합니다.
     *
     * @param message Redis 메시지 (body = JSON payload)
     * @param pattern 매칭된 채널 패턴 (notification:user:*)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String payload = new String(message.getBody());

        try {
            // 채널명: notification:user:{userId} → userId 추출
            String userId = channel.substring(channel.lastIndexOf(':') + 1);
            sseEmitterManager.sendToUser(Long.parseLong(userId), payload);
        } catch (Exception e) {
            log.warn("Redis 알림 메시지 처리 실패 — channel={}", channel, e);
        }
    }
}
