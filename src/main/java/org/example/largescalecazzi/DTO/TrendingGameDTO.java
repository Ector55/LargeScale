package org.example.largescalecazzi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendingGameDTO {
    private String id;
    private String title;
    private String img;

    @Field("average_score")
    private Double averageScore;

    private Double recentAverageScore;
    private Double scoreDifference;
}