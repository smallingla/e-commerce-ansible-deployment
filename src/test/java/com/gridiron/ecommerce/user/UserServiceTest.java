package com.gridiron.ecommerce.user;

import com.gridiron.ecommerce.user.request.CreateUserRequest;
import com.gridiron.ecommerce.user.response.UserProfileResponse;
import com.gridiron.ecommerce.utility.exception.ResourceExistsException;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import com.gridiron.ecommerce.utility.exception.UnauthorizedException;
import com.gridiron.ecommerce.utility.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Set up any common objects needed for testing
    }

    @Test
    void createUser_ShouldCreateNewUser_WhenEmailDoesNotExist() {
        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userService.createUser(request, Role.CUSTOMER);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceExistsException.class, () -> userService.createUser(request, Role.CUSTOMER));
    }

    @Test
    void authenticateUser_ShouldReturnUserProfileResponse_WhenCredentialsAreValid() {
        // Arrange
        String email = "john.doe@example.com";
        String password = "password123";
        User user = new User("John", "Doe", email, passwordEncoder.encode(password), Set.of(Role.CUSTOMER));

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString(), anyMap())).thenReturn("mocked-jwt-token");

        // Act
        UserProfileResponse response = userService.authenticateUser(email, password);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.token());
        assertEquals(email, response.emailAddress());
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.authenticateUser(email, password));
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenPasswordIsIncorrect() {
        // Arrange
        String email = "john.doe@example.com";
        String password = "wrongpassword";
        User user = new User("John", "Doe", email, passwordEncoder.encode("password123"), Set.of(Role.CUSTOMER));

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> userService.authenticateUser(email, password));
    }
}
