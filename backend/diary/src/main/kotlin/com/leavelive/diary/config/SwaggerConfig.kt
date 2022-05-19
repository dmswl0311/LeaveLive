package com.leavelive.diary.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun publicApi(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("V1")
            .pathsToMatch("/api/**")
            .build()

    @Bean
    fun diaryOpenApi(): OpenAPI =
        OpenAPI()
            .info(Info()
                .title("Leave, Live Diary API")
                .description("리브리브 다이어리 API")
                .version("V1"))
}