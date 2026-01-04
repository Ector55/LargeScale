package org.example.largescalecazzi.repository;

import org.example.largescalecazzi.model.ReviewMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewMongoRepository extends MongoRepository<ReviewMongo,String> {
}
