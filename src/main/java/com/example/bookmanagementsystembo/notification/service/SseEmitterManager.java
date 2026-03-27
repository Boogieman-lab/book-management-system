package com.example.bookmanagementsystembo.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE Emitter 생명주기를 관리하는 컴포넌트.
 * 사용자별 SseEmitter를 보관하고 실시간 알림 전송을 담당합니다.
 * 단일 인스턴스 환경 기준 — 다중 인스턴스 환경에서는 Redis Pub/Sub 전환 필요(U-02).
 */
@Slf4j
@Component
public class SseEmitterManager {

    /** SSE 연결 타임아웃: 30분 */
    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 사용자의 SSE 구독을 등록하고 SseEmitter를 반환합니다.
     * 기존 연결이 있으면 교체합니다.
     */
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        emitters.put(userId, emitter);

        // 연결 확인용 초기 이벤트 (503 방지)
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            emitters.remove(userId);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 특정 사용자에게 알림 이벤트를 전송합니다.
     * 사용자가 연결되지 않은 경우 조용히 무시합니다.
     */
    public void sendToUser(Long userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event().name("notification").data(data));
        } catch (IOException e) {
            log.warn("SSE 전송 실패 — userId={}, 연결 제거", userId);
            emitters.remove(userId);
        }
    }
}
