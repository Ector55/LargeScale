package org.example.largescale.repository;

import org.example.largescale.model.UserMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo,String> {
    List<UserMongo> findByUsernameContainingIgnoreCase(String partialUsername);

    @Query(value = "{ '_id': { '$in': ?0 } }", fields = "{ 'username': 1, 'firstName': 1, 'lastName': 1, '_id': 1 }")
    List<UserMongo> findFriendsNameAndIdByIds(List<String> ids);

    //Per recuperare solo la libreria di giochi dell'utente
    @Query(value = "{ '_id': ?0 }", fields = "{ 'myGames': 1, '_id': 1 }")
    UserMongo findMyGamesOnly(String userId);
}
