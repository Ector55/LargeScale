package org.example.largescale.repository;

import org.example.largescale.model.ReviewMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

@Repository
public interface ReviewMongoRepository extends MongoRepository<ReviewMongo,String> {
    // Mi serve per sapere se ci sono dei duplicati quando creo nel service una nuova review
    boolean existsByUserIdAndGameId(String userId, String gameId);

    // Scarica solo i dati utili da mostrare
    @Query(value = "{ '_id': { '$in': ?0 } }", fields = "{ 'score': 1, 'description': 1, 'userId': 1, 'timestamp': 1, '_id': 1 }")
    List<ReviewMongo> findBasicInfoByIds(List<String> ids);
}
