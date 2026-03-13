package com.streaming.api.controllers;

import com.streaming.api.models.User;
import com.streaming.api.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")

public class UserController {

    private final UserRepository userRepository;

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    private static final String MSG_ADDED_TO_FAVORITES = "Movie added to favourites";
    private static final String MSG_EXISTED_IN_FAVOURITES = "Movie already exists in favourites";
    private static final String MSG_REMOVED_FROM_FAVORITES = "Movie removed from favourites";
    private static final String MSG_MOVIE_NOT_FOUND_IN_FAVORITES = "Movie not found in favourites";
    private static final String MSG_USER_NOT_FOUND = "User not found";

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    @GetMapping("/{userId}")
    public Optional<User> getUserById(@PathVariable String userId) {
        return userRepository.findById(userId);
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email);
    }

    // Get favourite movies
    @GetMapping("/{userId}/favourites")
    public List<String> getFavouriteMovies(@PathVariable String userId) {
        return userRepository.findById(userId).map(User::getFavourite_movies)
            .orElse(Collections.emptyList());
    }

    // Add movie to favourites (use POST instead PUT method)
    @PostMapping("/{userId}/favourites/{movieId}")
    public Map<String, String> addFavouriteMovie(
        @PathVariable String userId,
        @PathVariable String movieId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return Map.of(ERROR, MSG_USER_NOT_FOUND);
        }
        User user = userOpt.get();

        if (user.getFavourite_movies() == null) {
            user.setFavourite_movies(new ArrayList<>());
        }

        // check if the movie is already in the favourite list
        if (!user.getFavourite_movies().contains(movieId)) {
            user.getFavourite_movies().add(movieId);
            userRepository.save(user);
            return Map.of(MESSAGE, MSG_ADDED_TO_FAVORITES);
        }
        return Map.of(MESSAGE, MSG_EXISTED_IN_FAVOURITES);
    }

    // Remove Movie From Favourites
    @DeleteMapping("/{userId}/favourites/{movieId}")
    public Map<String, String> removeFavouriteMovie(
        @PathVariable String userId,
        @PathVariable String movieId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Map.of(ERROR, MSG_USER_NOT_FOUND);
        }

        User user = userOpt.get();
        // check if the movie exists in the users' favourite list and remove it
        if (user.getFavourite_movies() != null &&
            user.getFavourite_movies().remove(movieId)) {
            userRepository.save(user);

            return Map.of(MESSAGE, MSG_REMOVED_FROM_FAVORITES);
        }
        return Map.of(ERROR, MSG_MOVIE_NOT_FOUND_IN_FAVORITES);
    }
}
