package com.pluse.service;

import com.pluse.PluseCartApplication;
import com.pluse.model.UserDetail;
import com.pluse.repository.UserRepository;
import com.pluse.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PluseCartApplication.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDetail user;

    @BeforeEach
    void setUp() {
        // Initialize the user object before each test
        user = new UserDetail();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("ROLE_USER");
    }

    @Test
    void saveUser_ShouldReturnSavedUser() {
        // Mock the password encoding behavior
        Mockito.when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        // Mock the repository save behavior to return the same user object
        Mockito.when(userRepository.save(Mockito.any(UserDetail.class))).thenReturn(user);

        // Act: Save the user
        UserDetail savedUser = userService.saveUser(user);

        // Assert: Check if the password was encoded and saved correctly
        assertNotNull(savedUser); // Ensure the saved user is not null
        assertEquals("encodedPassword123", savedUser.getPassword()); // Assert encoded password
        assertEquals("ROLE_USER", savedUser.getRole()); // Assert role is set

        // Verify interactions with passwordEncoder and userRepository
        Mockito.verify(passwordEncoder).encode("password123");
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        // Mock the behavior of the user repository to return the test user
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        // Act: Call the method to test
        UserDetail foundUser = userService.getUserByEmail("test@example.com");

        // Assert: Ensure the correct user is returned
        assertEquals(user, foundUser);

        // Verify that the repository was called with the correct email
        verify(userRepository).findByEmail("test@example.com");
    }
}
