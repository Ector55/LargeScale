package org.example.largescalecazzi.repository;

import org.example.largescalecazzi.model.UserMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserMongo,String> {
}
