package org.example.largescalecazzi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reviews")
public class ReviewMongo {
    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String gameId;
    private Integer score;
    private String description;
    private Instant timestamp;
}
