package com.example.bookmanagementsystembo.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄러 활성화 설정.
 * test 프로파일에서는 비활성화되어 통합 테스트 시 스케줄러가 의도치 않게 실행되지 않습니다.
 */
@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulingConfig {
}
