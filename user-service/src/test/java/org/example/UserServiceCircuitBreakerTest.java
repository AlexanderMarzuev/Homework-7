package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class UserServiceCircuitBreakerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private KafkaProducer kafkaProducer;

    @Test
    void create_shouldTriggerCircuitBreakerFallback_whenRepositoryThrows() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("DB connection lost"));

        User user = new User(null, "Test", "test@test.com", 25);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessageContaining("temporarily unavailable");
    }

    @Test
    void findById_shouldTriggerCircuitBreakerFallback_whenRepositoryThrows() {
        when(userRepository.findById(1L))
                .thenThrow(new RuntimeException("DB connection lost"));

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessageContaining("temporarily unavailable");
    }

    @Test
    void findAll_shouldTriggerCircuitBreakerFallback_whenRepositoryThrows() {
        when(userRepository.findAll())
                .thenThrow(new RuntimeException("DB connection lost"));

        assertThatThrownBy(() -> userService.findAll())
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessageContaining("temporarily unavailable");
    }

    @Test
    void delete_shouldTriggerCircuitBreakerFallback_whenRepositoryThrows() {
        when(userRepository.existsById(1L))
                .thenThrow(new RuntimeException("DB connection lost"));

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessageContaining("temporarily unavailable");
    }
}
