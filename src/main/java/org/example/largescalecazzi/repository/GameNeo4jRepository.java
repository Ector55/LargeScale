package org.example.largescalecazzi.repository;

import org.example.largescalecazzi.model.GameNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface GameNeo4jRepository extends Neo4jRepository<GameNeo4j, String> {
}
