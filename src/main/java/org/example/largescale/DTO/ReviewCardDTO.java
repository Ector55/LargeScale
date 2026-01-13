package org.example.largescale.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCardDTO {
    private String id;
    private String userId;
    private Integer score;
    private String description;
    private Instant timestamp;
}