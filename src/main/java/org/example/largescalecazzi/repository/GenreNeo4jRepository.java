package org.example.largescalecazzi.repository;

import org.example.largescalecazzi.model.GenreNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface GenreNeo4jRepository extends Neo4jRepository<GenreNeo4j,String> {
}
