package org.example.largescalecazzi.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class GameMongo {
    @Id
    private String id;
    private String title;
    private String description;
    private String img;
    private List<String> genres;

    private List<LastReviews> lastReviews;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastReviews{
        private String reviewId;
        private String userId;
        private Integer score;
        private String description;
    }

}
