package com.gridiron.ecommerce.user;

import com.gridiron.ecommerce.user.request.CreateUserRequest;
import com.gridiron.ecommerce.user.response.UserProfileResponse;
import com.gridiron.ecommerce.utility.exception.ResourceExistsException;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import com.gridiron.ecommerce.utility.exception.UnauthorizedException;
import com.gridiron.ecommerce.utility.security.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Log
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * This method creates a new user account
     * @param createUserRequest The request object containing the user's input details, such as first name, last name, email, and password.
     * @param role The Role of the user which can either be ADMIN or CUSTOMER
     */
    public void createUser(CreateUserRequest createUserRequest, Role role) {

        if(userRepository.existsByEmailIgnoreCase(createUserRequest.email())){
            throw new ResourceExistsException("Account Already Exists");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        userRepository.save(new User(
                createUserRequest.firstName(),
                createUserRequest.lastName(),
                createUserRequest.email(),
                passwordEncoder.encode(createUserRequest.password()),
                roles
        ));

    }

    /**
     * This method authenticates a user account using the email and password
     * @param email email address of the user to be authenticated
     * @param password corresponding password of the user to be authenticated
     * @return UserProfileResponse contains the token and the information of the user
     */
    public UserProfileResponse authenticateUser(String email, String password) {
        User user =  userRepository.findByEmailIgnoreCase(email).orElseThrow(()->new ResourceNotFoundException("Invalid Account"));

        //check if password matches user account password
        if(!passwordEncoder.matches(password, user.getPassword())){
           throw new UnauthorizedException("Invalid Email or Password");
        }

        return UserProfileResponse.builder()
                .token(generateUserToken(user))
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailAddress(user.getEmail())
                .roles(user.getRole())
                .build();


    }

    /**
     * This method generates a new JWT token for a user account. It includes the userId and role as claims in the token
     * @param user User instance of authenticated user
     * @return the generated JWT token for the user
     */
    private String generateUserToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role",user.getRole());

        return jwtService.generateToken(user.getEmail(), claims);
    }


    /**
     * create a default admin account with credentials
     * email: johndoe@gmail.com
     * password: qwertyuiop
     */
    @PostConstruct
    private void createDefaultAdminAccount() {

        try {
            createUser(CreateUserRequest.builder()
                            .firstName("test")
                            .lastName("test")
                            .email("johndoe@gmail.com")
                            .password("qwertyuio")
                            .build(),
                    Role.ADMIN);

        }catch (Exception e){
            log.info(e.getMessage());
        }
    }
}
