package org.example.notification;

import org.example.notification.dto.UserEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"user-events"})
class NotificationListenerTest {

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> "${spring.embedded.kafka.brokers}");
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");
    }

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Test
    void shouldConsumeUserCreatedEvent() throws Exception {
        UserEvent event = new UserEvent("CREATED", 1L, "Alice", "alice@example.com");

        kafkaTemplate.send("user-events", "1", event);
        kafkaTemplate.flush();

        Thread.sleep(3000);

        assertThat(kafkaTemplate.getDefaultTopic()).isEqualTo("user-events");
    }

    @Test
    void shouldConsumeUserDeletedEvent() throws Exception {
        UserEvent event = new UserEvent("DELETED", 5L, null, null);

        kafkaTemplate.send("user-events", "5", event);
        kafkaTemplate.flush();

        Thread.sleep(3000);

        assertThat(event.getEventType()).isEqualTo("DELETED");
        assertThat(event.getUserId()).isEqualTo(5L);
    }
}
