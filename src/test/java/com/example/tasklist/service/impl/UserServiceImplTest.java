package com.example.tasklist.service.impl;

import com.example.tasklist.domain.exception.ResourceNotFoundException;
import com.example.tasklist.domain.user.Role;
import com.example.tasklist.domain.user.User;
import com.example.tasklist.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void getUser_WithValidUserId_ShouldReturnUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        User testUser = userService.getById(userId);
        verify(userRepository).findById(userId);
        assertEquals(user, testUser);
    }

    @Test
    public void getUser_WithNonValidUserId_ShouldThrowResourceNotFoundException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    public void getUser_WithValidEmail_ShouldReturnUser() {
        String userEmail = "mila.samilyak@gmail.com";
        User user = new User();
        user.setEmail(userEmail);

        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        User testUser = userService.getUserByEmail(userEmail);
        verify(userRepository).findUserByEmail(userEmail);
        assertEquals(user, testUser);
    }

    @Test
    public void getUser_WithNonValidEmail_ShouldThrowResourceNotFoundException() {
        String userEmail = "ilya.samilyak@gmail.com";
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(userEmail));
        verify(userRepository).findUserByEmail(userEmail);
    }

    @Test
    public void updateUser() {
        String password = "password";
        User user = new User();
        user.setPassword(password);
        userService.update(user);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(user);
    }

    @Test
    public void isTaskOwner_WithValidUserIdAndTaskId_ShouldReturnTrue() {
        Long userId = 1L;
        Long taskId = 1L;

        when(userRepository.isTaskOwner(userId, taskId)).thenReturn(true);

        boolean isOwner = userService.isTaskOwner(userId, taskId);
        verify(userRepository).isTaskOwner(userId, taskId);
        assertTrue(isOwner);
    }

    @Test
    public void createUser_WithValidData_OK() {
        String userEmail = "username";
        String password = "password";
        User user = new User();
        user.setEmail(userEmail);
        user.setPassword(password);
        user.setPasswordConfirmation(password);

        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.empty());
        User testUser = userService.create(user);
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(password);
        assertEquals(Set.of(Role.ROLE_USER), testUser.getRoles());
    }

    @Test
    public void createUser_WithExistingEmail_ShouldThrowIllegalArgumentException() {
        String userEmail = "username";
        String password = "password";
        User user = new User();
        user.setEmail(userEmail);
        user.setPassword(password);
        user.setPasswordConfirmation(password);

        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.create(user));
        verify(userRepository, never()).save(user);
    }


    @Test
    public void createUser_WithDifferentPassword_ShouldThrowIllegalArgumentException() {
        String userEmail = "username";
        String password = "password";
        String passwordConfirmation = "passwordConfirmation";
        User user = new User();
        user.setEmail(userEmail);
        user.setPassword(password);
        user.setPasswordConfirmation(passwordConfirmation);

        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.create(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    public void deleteUser_WithValidUserId() {
        Long userId = 1L;
        userService.delete(userId);
        verify(userRepository).deleteById(userId);
    }




}