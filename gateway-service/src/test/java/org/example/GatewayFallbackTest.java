package org.example.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false",
        "spring.cloud.gateway.discovery.locator.enabled=false"
})
class GatewayFallbackTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void fallbackUsers_shouldReturn503WithErrorMessage() {
        webTestClient.get()
                .uri("/fallback/users")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.status").isEqualTo("error")
                .jsonPath("$.message").isEqualTo(
                        "User service is temporarily unavailable. Please try again later."
                );
    }

    @Test
    void fallbackNotifications_shouldReturn503WithErrorMessage() {
        webTestClient.get()
                .uri("/fallback/notifications")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.status").isEqualTo("error")
                .jsonPath("$.message").isEqualTo(
                        "Notification service is temporarily unavailable. Please try again later."
                );
    }
}
