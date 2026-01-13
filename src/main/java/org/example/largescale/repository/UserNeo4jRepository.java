package org.example.largescale.repository;

import org.example.largescale.model.UserNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserNeo4jRepository extends Neo4jRepository<UserNeo4j, String> {

    @Query("MATCH (u:User {userId: $userId})-[:IS_FRIEND_WITH]-(f:User) RETURN f.userId")
    List<String> findFriendIds(@Param("userId") String userId);

    // Friends Discovery: Trova utenti simili
    @Query("MATCH (me:User {userId: $userId})-[myPlay:PLAYS]->(commonGame:Game) " +
            "WHERE myPlay.hours > 50 AND (myPlay.score IS NULL OR myPlay.score >= 4) " +

            "WITH me, commonGame, myPlay " +
            "ORDER BY myPlay.hours DESC LIMIT 10 " +

            "MATCH (commonGame)<-[theirPlay:PLAYS]-(candidate:User) " +
            "WHERE candidate.userId <> $userId " +
            "AND NOT (me)-[:IS_FRIEND_WITH]-(candidate) " +
            "AND theirPlay.hours > 50 AND (theirPlay.score IS NULL OR theirPlay.score >= 4) " +
            "WITH candidate, count(distinct commonGame) AS compatibility " +
            "ORDER BY compatibility DESC LIMIT 10 " +
            "RETURN candidate")
    List<UserNeo4j> findPotentialFriends(@Param("userId") String userId);


    // --- SEZIONE CRUD ---

    // Aggiungi Gioco alla libreria (Crea relazione PLAYS)
    // MERGE assicura che non creiamo doppioni se l'arco esiste già
    @Query("MATCH (u:User {userId: $userId}) " +
            "MATCH (g:Game {gameId: $gameId}) " +
            "MERGE (u)-[r:PLAYS]->(g) " +
            "ON CREATE SET r.hours = 0.0")
    void addGameToLibrary(@Param("userId") String userId, @Param("gameId") String gameId);

    // Aggiorna ore di gioco
    @Query("MATCH (u:User {userId: $userId})-[r:PLAYS]->(g:Game {gameId: $gameId}) " +
            "SET r.hours = $hours")
    void updateGameHours(@Param("userId") String userId, @Param("gameId") String gameId, @Param("hours") double hours);

    // Aggiorna Score
    @Query("MATCH (u:User {userId: $userId}) " +
            "MATCH (g:Game {gameId: $gameId}) " +
            "MERGE (u)-[r:PLAYS]->(g) " +
            "SET r.score = $score")
    void updateGameScore(@Param("userId") String userId, @Param("gameId") String gameId, @Param("score") int score);

    // Remove score (per cancellazione di una review)
    @Query("MATCH (u:User {userId: $userId})-[r:PLAYS]->(g:Game {gameId: $gameId}) " +
            "REMOVE r.score")
    void removeGameScore(@Param("userId") String userId, @Param("gameId") String gameId);

    // Crea Amicizia
    @Query("MATCH (u1:User {userId: $userId}) " +
            "MATCH (u2:User {userId: $friendId}) " +
            "MERGE (u1)-[:IS_FRIEND_WITH]->(u2)")
    void addFriend(@Param("userId") String userId, @Param("friendId") String friendId);

    // Rimuovi Amico (Cancellazione Relazione)
    // Nota: Uso il pattern senza direzione "-[...]-" per essere sicuro di cancellare l'arco
    // indipendentemente da come è stato orientato (A->B o B->A).
    @Query("MATCH (u1:User {userId: $userId})-[r:IS_FRIEND_WITH]-(u2:User {userId: $friendId}) " +
            "DELETE r")
    void removeFriend(@Param("userId") String userId, @Param("friendId") String friendId);

    // Rimuovi Gioco dalla Libreria (Cancellazione Relazione)
    // Qui la direzione è importante: cancello solo se è l'utente a giocare quel gioco
    @Query("MATCH (u:User {userId: $userId})-[r:PLAYS]->(g:Game {gameId: $gameId}) " +
            "DELETE r")
    void removeGameFromLibrary(@Param("userId") String userId, @Param("gameId") String gameId);

    // --- SEZIONE CHECK ---

    // check "Sono amici?"
    // Restituisce true/false istantaneamente senza caricare gli oggetti User.
    @Query("RETURN EXISTS( (:User {userId: $userId})-[:IS_FRIEND_WITH]-(:User {userId: $friendId}) )")
    boolean areFriends(@Param("userId") String userId, @Param("friendId") String friendId);

    // check "Possiede questo gioco?"
    @Query("RETURN EXISTS( (:User {userId: $userId})-[:PLAYS]->(:Game {gameId: $gameId}) )")
    boolean doesUserOwnGame(@Param("userId") String userId, @Param("gameId") String gameId);
}