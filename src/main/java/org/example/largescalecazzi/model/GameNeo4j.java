package org.example.largescalecazzi.model;

import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Node("Game")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameNeo4j {
    @Id
    private String gameId; // Deve coincidere con Mongo ID

    private String title;

    @Relationship(type = "HAS_GENRE", direction = Relationship.Direction.OUTGOING)
    private List<GenreNeo4j> genres;
}