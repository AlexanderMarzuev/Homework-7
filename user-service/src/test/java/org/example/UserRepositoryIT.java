package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class UserRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearDb() {
        userRepository.deleteAll();
    }

    @Test
    void saveAndFindAll_shouldWork() {
        User u1 = new User(null, "Masha", "masha@ya.ru", 28);
        User u2 = new User(null, "Dima", "dima@ya.ru", 35);
        userRepository.saveAll(List.of(u1, u2));

        List<User> all = userRepository.findAll();
        assertThat(all).hasSize(2);
        assertThat(all.stream().map(User::getName).toList())
                .containsExactlyInAnyOrder("Masha", "Dima");
    }
}
