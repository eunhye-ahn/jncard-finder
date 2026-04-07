package com.esstudy.jncardsearch.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* [WHAT] Swagger 문서 UI의 헤더 설정
*/

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("가맹점 API")
                        .description("ElasticSearch 기반 가맹점 찾기 서비스")
                        .version("v1.0.0")
                );
    }
}
