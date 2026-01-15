package org.example.largescale.model;

import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Node("User")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNeo4j {
    @Id
    private String userId;

    private String username;

    @Relationship(type = "IS_FRIEND_WITH")
    private List<UserNeo4j> friends;

    // RELAZIONE CON I GIOCHI
    @Relationship(type = "PLAYS", direction = Relationship.Direction.OUTGOING)
    private List<PlaysRelationship> gamesPlayed;

    public UserNeo4j(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
