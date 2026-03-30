package com.example.bookmanagementsystembo.notification.config;

import com.example.bookmanagementsystembo.notification.service.NotificationRedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 알림 시스템 Redis Pub/Sub 설정.
 * 채널 패턴: notification:user:{userId}
 * 다중 인스턴스 환경에서 모든 인스턴스가 채널을 구독하여 SSE로 전달합니다.
 */
@Configuration
public class NotificationRedisConfig {

    /** Redis 메시지 리스너 컨테이너 — notification:user:* 채널 구독 */
    @Bean
    public RedisMessageListenerContainer notificationListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter notificationListenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
                notificationListenerAdapter,
                new PatternTopic("notification:user:*")
        );
        return container;
    }

    /** NotificationRedisSubscriber.onMessage()를 리스너 메서드로 등록 */
    @Bean
    public MessageListenerAdapter notificationListenerAdapter(NotificationRedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }
}
