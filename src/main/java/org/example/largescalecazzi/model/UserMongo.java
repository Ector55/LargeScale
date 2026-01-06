package org.example.largescalecazzi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class UserMongo {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String nickname;
    private String email;
    private String password;
    private String role;

    private List<String> friends;

    private List<MyGames> myGames;
    private List<TopPlayedGames> topPlayedGames;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyGames {
        private String gameId;
        private Double hours;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopPlayedGames {
        private String gameId;
        private String title;
        private String img;
        private Double hours;
    }
}
