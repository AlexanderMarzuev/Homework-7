package org.example;

import org.example.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.user-events:user-events}")
    private String topic;

    public KafkaProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserEvent(UserEvent event) {
        log.info("Отправка события в Kafka topic {}: type={}, userId={}",
                topic, event.getEventType(), event.getUserId());
        kafkaTemplate.send(topic,
                event.getUserId() != null ? event.getUserId().toString() : null,
                event);
    }
}
