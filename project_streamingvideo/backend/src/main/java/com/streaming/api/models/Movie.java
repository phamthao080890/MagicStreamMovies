package com.streaming.api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "movies")
public class Movie {
    @Id
    private String id;
    private String imdb_id;
    private String title;
    private String poster_path;
    private String youtube_id;
    private List<Map<String, Object>> genre;
    private String admin_review;
    private Map<String, Object> ranking;
}
