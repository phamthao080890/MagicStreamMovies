package com.streaming.api.models;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class Movie {
    private String id;
    private String imdb_id;
    private String title;
    private String description;
    private String source_url;
    private String poster_path;
    private String youtube_id;
    private String imdb_video_id;
    private List<Map<String, Object>> genre;
    private String admin_review;
    private Map<String, Object> ranking;
}
