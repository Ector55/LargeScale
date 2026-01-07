package org.example.largescalecazzi.repository;

import org.example.largescalecazzi.model.ReviewMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewMongoRepository extends MongoRepository<ReviewMongo,String> {
    // Mi serve per sapere se ci sono dei duplicati quando creo nel service una nuova review
    boolean existsByUserIdAndGameId(String userId, String gameId);
}
