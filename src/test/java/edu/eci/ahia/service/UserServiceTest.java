package edu.eci.ahia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.eci.ahia.exception.UserNotFoundException;
import edu.eci.ahia.model.entity.User;
import edu.eci.ahia.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void createUser_ShouldBuildAndSaveAndReturn() {
        User input = User.builder()
            .name("Alice")
            .email("alice@example.com")
            .phoneNumber(1234567890L)
            .build();

        User savedUser = User.builder()
            .id(1L)
            .name("Alice")
            .email("alice@example.com")
            .phoneNumber(1234567890L)
            .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals(1234567890L, result.getPhoneNumber());

        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertEquals("Alice", captured.getName());
        assertEquals("alice@example.com", captured.getEmail());
        assertEquals(1234567890L, captured.getPhoneNumber());
    }

    @Test
    void findOrCreateByPhoneNumber_WhenUserExists_ShouldReturnExisting() {
        User existing = User.builder().id(1L).name("Alice").phoneNumber(1234567890L).build();

        when(userRepository.findByPhoneNumber(1234567890L)).thenReturn(Optional.of(existing));

        User result = userService.findOrCreateByPhoneNumber("Alice", 1234567890L);

        assertEquals(existing, result);
        verify(userRepository).findByPhoneNumber(1234567890L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void findOrCreateByPhoneNumber_WhenUserDoesNotExist_ShouldCreateNew() {
        User created = User.builder().id(2L).name("Bob").phoneNumber(9876543210L).build();

        when(userRepository.findByPhoneNumber(9876543210L)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(created);

        User result = userService.findOrCreateByPhoneNumber("Bob", 9876543210L);

        assertEquals(created, result);
        verify(userRepository).findByPhoneNumber(9876543210L);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("Bob", userCaptor.getValue().getName());
        assertEquals(9876543210L, userCaptor.getValue().getPhoneNumber());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        User existing = User.builder()
            .id(1L)
            .name("Alice")
            .email("alice@example.com")
            .phoneNumber(1234567890L)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> userService.getUserById(99L));
        verify(userRepository).findById(99L);
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        User user1 = User.builder().id(1L).name("Alice").build();
        User user2 = User.builder().id(2L).name("Bob").build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDelete() {
        User existing = User.builder()
            .id(1L)
            .name("Alice")
            .email("alice@example.com")
            .phoneNumber(1234567890L)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(existing);
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> userService.deleteUser(99L));
        verify(userRepository).findById(99L);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateFields() {
        User existing = User.builder()
            .id(1L)
            .name("Alice")
            .email("alice@example.com")
            .phoneNumber(1234567890L)
            .build();

        User input = User.builder()
            .name("Alice Updated")
            .email("alice.updated@example.com")
            .phoneNumber(9876543210L)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(existing);

        User result = userService.updateUser(1L, input);

        assertEquals("Alice Updated", result.getName());
        assertEquals("alice.updated@example.com", result.getEmail());
        assertEquals(9876543210L, result.getPhoneNumber());
        assertEquals("Alice Updated", existing.getName());
        assertEquals("alice.updated@example.com", existing.getEmail());
        assertEquals(9876543210L, existing.getPhoneNumber());
        verify(userRepository).findById(1L);
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrow() {
        User input = User.builder()
            .name("Alice Updated")
            .build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> userService.updateUser(99L, input));
        verify(userRepository).findById(99L);
        verify(userRepository, never()).save(any());
    }
}
