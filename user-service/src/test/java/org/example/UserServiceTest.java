package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Test
    void create_shouldSaveUser() {
        User input = new User(null, "Alex", "alex@gmail.com", 30);
        User expected = new User(1L, "Alex", "alex@gmail.com", 30);
        when(userRepository.save(any())).thenReturn(expected);

        User result = userService.create(input);
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).save(any(User.class));
        verify(kafkaProducer).sendUserEvent(any());
    }

    @Test
    void findById_shouldReturnUser() {
        User user = new User(1L, "Kate", "kate@ya.ru", 25);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);
        assertThat(result.getName()).isEqualTo("Kate");
        verify(userRepository).findById(1L);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User u1 = new User(1L, "Alice", "alice@ya.ru", 28);
        User u2 = new User(2L, "Bob", "bob@ya.ru", 35);
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<User> result = userService.findAll();
        assertThat(result).hasSize(2);
        verify(userRepository).findAll();
    }

    @Test
    void delete_shouldRemoveUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);
        verify(userRepository).deleteById(1L);
        verify(kafkaProducer).sendUserEvent(any());
    }
}
