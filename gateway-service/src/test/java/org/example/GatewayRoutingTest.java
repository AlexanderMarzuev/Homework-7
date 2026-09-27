package org.example.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false",
        "spring.cloud.gateway.discovery.locator.enabled=false"
})
class GatewayRoutingTest {

    @Autowired
    private WebTestClient webTestClient;

    @Configuration
    static class TestRoutesConfig {
        @Bean
        RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes()
                    .route("test-fallback", r -> r
                            .path("/api/test-fallback/**")
                            .filters(f -> f.filter((exchange, chain) -> {
                                // Симулируем недоступность downstream — CB должен сработать
                                exchange.getResponse().setRawStatusCode(503);
                                return exchange.getResponse().setComplete();
                            }))
                            .uri("lb://nonexistent-service"))
                    .build();
        }
    }

    @Test
    void gatewayContext_shouldStartSuccessfully() {
        // Проверяем, что Gateway стартует с корректным контекстом
        webTestClient.get()
                .uri("/fallback/users")
                .exchange()
                .expectStatus().isEqualTo(503);
    }
}
