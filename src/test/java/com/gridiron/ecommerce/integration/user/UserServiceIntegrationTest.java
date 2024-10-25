package com.gridiron.ecommerce.integration.user;

import com.gridiron.ecommerce.integration.AbstractIntegrationTest;
import com.gridiron.ecommerce.user.Role;
import com.gridiron.ecommerce.user.User;
import com.gridiron.ecommerce.user.UserRepository;
import com.gridiron.ecommerce.user.UserService;
import com.gridiron.ecommerce.user.request.CreateUserRequest;
import com.gridiron.ecommerce.user.response.UserProfileResponse;
import com.gridiron.ecommerce.utility.exception.ResourceExistsException;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
//@ActiveProfiles("test")
public class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_ShouldSaveUser_WhenValidDataProvided() {
        CreateUserRequest request = new CreateUserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );

        userService.createUser(request, Role.CUSTOMER);

        User user = userRepository.findByEmailIgnoreCase("john.doe@example.com")
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void createUser_ShouldThrowException_WhenUserAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest(
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "password123"
        );

        userService.createUser(request, Role.CUSTOMER);

        assertThrows(ResourceExistsException.class, () -> {
            userService.createUser(request, Role.CUSTOMER);
        });
    }

    @Test
    void authenticateUser_ShouldReturnUserProfile_WhenValidCredentials() {
        CreateUserRequest request = new CreateUserRequest(
                "Alice",
                "Smith",
                "alice.smith@example.com",
                "password123"
        );
        userService.createUser(request, Role.CUSTOMER);

        UserProfileResponse response = userService.authenticateUser(
                "alice.smith@example.com",
                "password123"
        );

        assertNotNull(response.token());
        assertEquals("Alice", response.firstName());
        assertEquals("Smith", response.lastName());
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenInvalidCredentials() {
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.authenticateUser("nonexistent@example.com", "wrongpassword");
        });
    }

}
