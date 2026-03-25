package com.example.bookmanagementsystembo;


import com.example.bookmanagementsystembo.auth.config.KakaoOAuthProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@OpenAPIDefinition(
        info = @Info(
                title = "도서 관리 시스템 API",
                version = "1.0.0",
                description = "도서 대출, 예약, 신청 및 회원 관리를 위한 REST API",
                contact = @Contact(name = "Book Management System")
        ),
        servers = @Server(url = "/", description = "기본 서버"),
        security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "JWT Access Token을 입력하세요. 예: Bearer {token}"
)
@SpringBootApplication
@EnableConfigurationProperties({KakaoOAuthProperties.class})
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class BookManagementSystemBoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookManagementSystemBoApplication.class, args);
    }

}
