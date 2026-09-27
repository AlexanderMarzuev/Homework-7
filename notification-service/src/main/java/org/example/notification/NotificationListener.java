package org.example.notification;

import org.example.notification.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        log.info("Получено событие из Kafka: type={}, userId={}, name={}, email={}",
                event.getEventType(), event.getUserId(), event.getName(), event.getEmail());

        switch (event.getEventType()) {
            case "CREATED" -> log.info("Отправляем welcome-уведомление пользователю {} ({})",
                    event.getName(), event.getEmail());
            case "UPDATED" -> log.info("Отправляем уведомление об обновлении профиля: {} ({})",
                    event.getName(), event.getEmail());
            case "DELETED" -> log.info("Отправляем уведомление об удалении аккаунта: userId={}",
                    event.getUserId());
            default -> log.warn("Неизвестный тип события: {}", event.getEventType());
        }
    }
}
