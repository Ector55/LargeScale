package org.example.largescale.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Node("Genre")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreNeo4j {
    @Id
    private String name;
}
