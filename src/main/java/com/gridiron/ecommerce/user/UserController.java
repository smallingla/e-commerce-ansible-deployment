package com.gridiron.ecommerce.user;

import com.gridiron.ecommerce.user.request.AuthenticateUserRequest;
import com.gridiron.ecommerce.user.request.CreateUserRequest;
import com.gridiron.ecommerce.utility.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * This endpoint is to create a new customer user account.
     * It is not secured, and accessible to non-authenticated users
     * @param createUserRequest The request object containing the user's input details, such as first name, last name, email, and password.
     */
    @PostMapping("/public/customer")
    private ResponseEntity<ApiResponse> createUserAccount(@RequestBody @Valid CreateUserRequest createUserRequest) {

        userService.createUser(createUserRequest,Role.CUSTOMER);
        return new ResponseEntity<>(new ApiResponse(true,
                "Customer Created Successfully", null), HttpStatus.CREATED);
    }

    /**
     * This endpoint is to create a new admin user account.
     * It is secured, and accessible to only authenticated users with ADMIN role
     * @param createUserRequest The request object containing the user's input details, such as first name, last name, email, and password.
     */
    @PostMapping("/private/admin")
    private ResponseEntity<ApiResponse> createAdminAccount(@RequestBody @Valid CreateUserRequest createUserRequest) {

        userService.createUser(createUserRequest,Role.ADMIN);
        return new ResponseEntity<>(new ApiResponse(true,
                "Customer Created Successfully", null), HttpStatus.CREATED);
    }

    /**
     * This endpoint is to authenticate a user account
     * It is not secured, and accessible to any user.
     * @param authenticateUserRequest  The request object containing the user's input details, such as  email, and password.
     */
    @PostMapping("/public/authenticate")
    private ResponseEntity<ApiResponse> authenticateUserAccount(@RequestBody @Valid AuthenticateUserRequest authenticateUserRequest) {

        return new ResponseEntity<>(new ApiResponse(true, "Account Authenticated Successfully",
                userService.authenticateUser(authenticateUserRequest.email(), authenticateUserRequest.password())),
                HttpStatus.CREATED);
    }


}
