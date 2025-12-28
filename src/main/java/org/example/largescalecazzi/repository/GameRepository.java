package org.example.largescalecazzi.repository;

import org.example.largescalecazzi.model.GameMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<GameMongo, String> {

}
