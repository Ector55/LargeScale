package org.example.largescale.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.mapping.Field;

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

    //@Indexed(unique = true)
    @NotBlank(message = "Username can't be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Field("nickname") //perché sul json è stato chiamato nickname
    @Indexed
    private String username;

    //@Indexed(unique = true)
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Email should be valid")
    @Indexed
    private String email;

    @NotBlank(message = "Password can't be blank")
    private String password;

    @NotBlank(message = "Role can't be blank")
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
