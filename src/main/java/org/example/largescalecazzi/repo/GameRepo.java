package org.example.largescalecazzi.repo;

import org.example.largescalecazzi.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepo extends MongoRepository<Game, String> {

}
