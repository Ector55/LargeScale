package org.example.largescale.repository;
import org.example.largescale.model.GameNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GameNeo4jRepository extends Neo4jRepository<GameNeo4j, String> {

    // 1. Social: Giochi popolari tra gli amici
    @Query("MATCH (me:User {userId: $userId}) " +
            "MATCH (me)-[:IS_FRIEND_WITH]->(friend:User) " +
            "WITH me, friend ORDER BY rand() LIMIT 100 " +
            "MATCH (friend)-[play:PLAYS]->(recGame:Game) " +
            "WHERE play.hours > 50 AND (play.score IS NULL OR play.score >= 4) " +
            "AND NOT (me)-[:PLAYS]->(recGame) " +
            "WITH recGame, count(distinct friend) AS score " +
            "ORDER BY score DESC LIMIT 10 " +
            "RETURN recGame")
    List<GameNeo4j> findGameRecommendationsAmongFriends(@Param("userId") String userId);

    // 2. Content-Based: Giochi simili per genere
    @Query("MATCH (me:User {userId: $userId})-[:PLAYS]->(:Game)-[:HAS_GENRE]->(g:Genre) " +
            "WITH me, g, count(*) AS frequency ORDER BY frequency DESC LIMIT 3 " +
            "WITH me, collect(g) AS myTopGenres " +
            "UNWIND myTopGenres AS genre " +
            "CALL { " +
            "  WITH me, genre " +
            "  MATCH (game:Game)-[:HAS_GENRE]->(genre) " +
            "  WITH game, rand() AS rng ORDER BY rng LIMIT 50 " +
            "  WHERE NOT (me)-[:PLAYS]->(game) " +
            "  RETURN game " +
            "} " +
            "WITH game, count(*) AS overlap " +
            "ORDER BY overlap DESC LIMIT 10 " +
            "RETURN game")
    List<GameNeo4j> findGameRecommendationsByGenere(@Param("userId") String userId);

    // 3. Global query: Giochi influenti di un certo genere
    @Query("MATCH (genre:Genre {name: $genreName}) " +
            "MATCH (genre)<-[:HAS_GENRE]-(g:Game) " +
            "RETURN g, COUNT { (g)<-[:PLAYS]-() } AS influence " +
            "ORDER BY influence DESC LIMIT 10")
    List<GameNeo4j> findMostInfluentialGameByGenre(@Param("genreName") String genreName);

    // Perché non mappare anche le operazioni CRUD? Le operazioni mappate qui servono per non caricare dati pesanti
    // in memoria per fare le operazioni crud, ma Game è leggero, non serve codice costumizzato

}