package org.example.largescalecazzi.repository;

import org.example.largescalecazzi.model.UserNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserNeo4jRepository extends Neo4jRepository<UserNeo4j,String> {
}
