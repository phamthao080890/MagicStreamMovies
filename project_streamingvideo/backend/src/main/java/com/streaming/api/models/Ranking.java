package com.streaming.api.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "rankings")
public class Ranking {
    @Id
    private String id;
    
    private String ranking_value;
    private String rangking_name;
}
