package org.example.largescalecazzi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.largescalecazzi.model.GameMongo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailDTO {
    private String id;
    private String title;
    private String description;
    private String img;
    private List<String> genres;
    private Double averageScore;
    private Integer reviewsCount;
    private List<GameMongo.LastReviews> lastReviews;
}