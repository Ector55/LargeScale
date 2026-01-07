package org.example.largescalecazzi.model;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "UserId can't be blank")
    private String userId;
    @NotBlank(message = "GameId can't be blank")
    private String gameId;
    @NotBlank(message = "Score can't be blank")
    private Integer score;
    private String description;
    private Instant timestamp;
}
