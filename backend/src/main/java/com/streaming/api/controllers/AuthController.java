package com.streaming.api.controllers;

import com.streaming.api.models.User;
import com.streaming.api.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.ArrayList;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")

public class AuthController {

    private final UserRepository userRepository;

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private static final String MESSAGE = "message";

    private static final String LOGIN_SUCCESS = "Login successfully";
    private static final String REGISTER_SUCCESS = "User registered successfully";
    private static final String ERR_USER_NOT_FOUND = "User not found";
    private static final String ERR_INCORRECT_PWD = "Incorrect password";
    private static final String ERR_EXISTED_EMAIL = "Email already registered";

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email = body.get(EMAIL);
        String password = body.get(PASSWORD);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                    MESSAGE, ERR_USER_NOT_FOUND
                )
            );
        }

        // Validate password using BCrypt
        boolean passwordMatch = BCrypt.checkpw(password, user.getPassword());
        if (!passwordMatch) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                    MESSAGE, ERR_INCORRECT_PWD
                )
            );
        }
        // Login success
        return ResponseEntity.status(HttpStatus.OK).body(
            Map.of(
                MESSAGE, LOGIN_SUCCESS,
                "userId", user.getId(),
                "role", user.getRole(),
                EMAIL, user.getEmail(),
                "name", user.getFirst_name()
            )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String firstName = body.get("name");

        String email = body.get(EMAIL);
        String password = body.get(PASSWORD);

        if (userRepository.findByEmail(email) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                    MESSAGE, ERR_EXISTED_EMAIL
                )
            );
        }

        //hash password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        //create new user
        User newUser = new User();
        newUser.setFirst_name(firstName);
        newUser.setLast_name("");
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);
        newUser.setRole("USER");

        newUser.setFavourite_movies(new ArrayList<>());
        newUser.setFavourite_genres(new ArrayList<>());

        newUser.setCreated_at(LocalDateTime.now());
        newUser.setUpdated_at(LocalDateTime.now());

        newUser.setToken("");
        newUser.setRefresh_token("");

        //save to database
        userRepository.save(newUser);

        //return response
        return ResponseEntity.status(HttpStatus.OK).body(
            Map.of(
                MESSAGE, REGISTER_SUCCESS,
                "userId", newUser.getId(),
                "role", newUser.getRole(),
                EMAIL, newUser.getEmail()
            )
        );
    }
}
