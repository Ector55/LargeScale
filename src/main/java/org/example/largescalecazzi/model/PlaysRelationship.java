package org.example.largescalecazzi.model;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@RelationshipProperties
@Data
@NoArgsConstructor
public class PlaysRelationship {
    @RelationshipId
    private Long id;

    @TargetNode
    private GameNeo4j game;

    private Double hours;
    private Integer score;

    public PlaysRelationship(GameNeo4j game, Double hours) {
        this.game = game;
        this.hours = hours;
    }
}