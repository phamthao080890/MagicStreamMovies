package com.streaming.api.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection ="genres")
public class Genre {
    @Id
    private String id;
    private Integer genre_id;
    private String genre_name;
}
