package org.example.largescalecazzi.repository;

import org.example.largescalecazzi.model.GenreNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.List;

public interface GenreNeo4jRepository extends Neo4jRepository<GenreNeo4j,String> {
    // restituisce la lista ordinata di generi
    List<GenreNeo4j> findAllByOrderByNameAsc();
}
