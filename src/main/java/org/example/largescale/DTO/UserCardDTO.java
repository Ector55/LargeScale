package org.example.largescale.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCardDTO {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
}
