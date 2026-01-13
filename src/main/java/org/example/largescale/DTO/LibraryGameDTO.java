package org.example.largescale.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryGameDTO {
    private String id;
    private String title;
    private String img;
    private Double hoursPlayed;
}