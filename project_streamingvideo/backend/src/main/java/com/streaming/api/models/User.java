package com.streaming.api.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection ="users")
public class User {
    
    @Id
    private String id;
    
    private String user_id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String role;
    
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    
    private String token;
    private String refresh_token;
    private List<String> favourite_genres;
    private List<String> favourite_movies;
    
    
    
}
