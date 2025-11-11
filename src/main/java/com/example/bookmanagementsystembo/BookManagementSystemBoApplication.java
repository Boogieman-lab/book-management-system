package com.example.bookmanagementsystembo;


import com.example.bookmanagementsystembo.auth.config.KakaoOAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({KakaoOAuthProperties.class})
public class BookManagementSystemBoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookManagementSystemBoApplication.class, args);
    }

}
