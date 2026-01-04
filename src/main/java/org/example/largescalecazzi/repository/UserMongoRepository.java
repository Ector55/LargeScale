package org.example.largescalecazzi.repository;

import org.example.largescalecazzi.model.UserMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo,String> {
}
