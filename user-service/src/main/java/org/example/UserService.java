package org.example;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @CircuitBreaker(name = "user-operation", fallbackMethod = "fallbackCreate")
    public User create(User user) {
        return userRepository.save(user);
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
        return userRepository.save(user);
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
    }

    private void fallbackDelete(Long id, Throwable t) {
        throw new ServiceUnavailableException(
                "User service is temporarily unavailable: " + t.getMessage());
    }
}
