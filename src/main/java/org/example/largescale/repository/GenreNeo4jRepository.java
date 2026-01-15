package org.example.largescale.repository;

import org.example.largescale.model.GenreNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.List;

public interface GenreNeo4jRepository extends Neo4jRepository<GenreNeo4j,String> {
    List<GenreNeo4j> findAllByOrderByNameAsc();
}
