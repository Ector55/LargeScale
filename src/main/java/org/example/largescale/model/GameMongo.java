package org.example.largescale.model;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class GameMongo {
    @Id
    private String id;

    @Indexed
    @NotBlank(message = "Title can't be blank")
    private String title;
    @NotBlank(message = "Description can't be blank")
    private String description;
    private String img;
    @NotBlank(message = "Genres can't be blank")
    private List<String> genres;

    private List<LastReviews> lastReviews;

    @Field("showOtherReviews")
    private List<AllGameReviews> allGameReviews;

    @Field("total_score")
    private Integer totalScore;

    @Field("reviews_count")
    private Integer reviewsCount;

    @Field("average_score")
    private Double averageScore;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastReviews{
        @Field("_id")
        private String reviewId;
        private String userId;
        private Integer score;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllGameReviews{
        private String reviewId;
    }

}
