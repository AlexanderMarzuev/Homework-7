package org.example;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.dto.UserEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    public UserService(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    @CircuitBreaker(name = "user-operation", fallbackMethod = "fallbackCreate")
    public User create(User user) {
        User saved = userRepository.save(user);
        kafkaProducer.sendUserEvent(new UserEvent("CREATED", saved.getId(),
                saved.getName(), saved.getEmail()));
        return saved;
    }

    private User fallbackCreate(User user, Throwable t) {
        throw new ServiceUnavailableException(
                "User service is temporarily unavailable: " + t.getMessage());
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "user-operation", fallbackMethod = "fallbackFindById")
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + id));
    }

    private User fallbackFindById(Long id, Throwable t) {
        throw new ServiceUnavailableException(
                "User service is temporarily unavailable: " + t.getMessage());
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "user-operation", fallbackMethod = "fallbackFindAll")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @SuppressWarnings("unused")
    private List<User> fallbackFindAll(Throwable t) {
        throw new ServiceUnavailableException(
                "User service is temporarily unavailable: " + t.getMessage());
    }

    @Transactional
    @CircuitBreaker(name = "user-operation", fallbackMethod = "fallbackUpdate")
    public User update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Cannot update user without ID");
        }
        User saved = userRepository.save(user);
        kafkaProducer.sendUserEvent(new UserEvent("UPDATED", saved.getId(),
                saved.getName(), saved.getEmail()));
        return saved;
    }

    private User fallbackUpdate(User user, Throwable t) {
        throw new ServiceUnavailableException(
                "User service is temporarily unavailable: " + t.getMessage());
    }

    @Transactional
    @CircuitBreaker(name = "user-operation", fallbackMethod = "fallbackDelete")
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        kafkaProducer.sendUserEvent(new UserEvent("DELETED", id, null, null));
    }

    private void fallbackDelete(Long id, Throwable t) {
        throw new ServiceUnavailableException(
                "User service is temporarily unavailable: " + t.getMessage());
    }
}
