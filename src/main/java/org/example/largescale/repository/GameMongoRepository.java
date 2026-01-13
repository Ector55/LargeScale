package org.example.largescale.repository;

import org.example.largescale.DTO.GameCardDTO;
import org.example.largescale.DTO.TrendingGameDTO;
import org.example.largescale.model.GameMongo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameMongoRepository extends MongoRepository<GameMongo, String> {

    //Query:La query seleziona i giochi con un voto positivo filtrandoli per genere (se specificato),
    // li ordina dal voto migliore al peggiore e restituisce solo i primi dieci. Controlla che l'avarage score>0.

    @Aggregation(pipeline = {
            "{ '$match': { '$and': [ " +
                    "{ '$expr': { '$or': [ { '$eq': [?0, null] }, { '$in': [?0, '$genres'] } ] } }, " +
                    "{ 'average_score': { '$gt': 0 } }" +
                    "] } }",

            "{ '$sort': { 'average_score': -1 } }",

            "{ '$limit': 10 }",

            "{ '$project': { " +
                    "'id': '$_id', " +
                    "'title': 1, " +
                    "'img': 1, " +
                    "'average_score': 1, " +
                    "'reviews_count': 1 " +
                    "} }"
    })
    List<GameCardDTO> findTop10BestRatedGames(String genre);


    // TOP DECLINING
    @Aggregation(pipeline = {
            // 1. Calcola media recente
            "{ '$addFields': { 'recentAverageScore': { '$avg': { '$slice': ['$lastReviews.score', 5] } } } }",

            "{ '$match': { 'recentAverageScore': { '$ne': null }, 'average_score': { '$ne': null } } }",

            // 3. Calcola Gap (Storico - Recente)
            "{ '$addFields': { 'scoreDifference': { '$subtract': ['$average_score', '$recentAverageScore'] } } }",

            // 4. Solo positivi
            "{ '$match': { 'scoreDifference': { '$gt': 0 } } }",

            // 5. Ordina e limita
            "{ '$sort': { 'scoreDifference': -1 } }",
            "{ '$limit': 10 }",

            // 6. Restituiamo il campo "average_score" esattamente come si chiama nel DB.
            "{ '$project': { " +
                    "'id': '$_id', " +
                    "'title': 1, " +
                    "'img': 1, " +
                    "'average_score': 1, " +
                    "'recentAverageScore': 1, " +
                    "'scoreDifference': 1 " +
                    "} }"
    })
    List<TrendingGameDTO> findTopDecliningGames();


    // TOP IMPROVING
    @Aggregation(pipeline = {
            "{ '$addFields': { 'recentAverageScore': { '$avg': { '$slice': ['$lastReviews.score', 5] } } } }",
            "{ '$match': { 'recentAverageScore': { '$ne': null }, 'average_score': { '$ne': null } } }",

            // Calcola Gap (Recente - Storico)
            "{ '$addFields': { 'scoreDifference': { '$subtract': ['$recentAverageScore', '$average_score'] } } }",

            "{ '$match': { 'scoreDifference': { '$gt': 0 } } }",
            "{ '$sort': { 'scoreDifference': -1 } }",
            "{ '$limit': 10 }",

            "{ '$project': { " +
                    "'id': '$_id', " +
                    "'title': 1, " +
                    "'img': 1, " +
                    "'average_score': 1, " +
                    "'recentAverageScore': 1, " +
                    "'scoreDifference': 1 " +
                    "} }"
    })
    List<TrendingGameDTO> findTopImprovingGames();

    // Query Hidden Gems: Trova i giochi con score alti (score > 4) ma poco conosciuti (poche recensioni, < 20 recensioni )
    @Aggregation(pipeline = {
            "{ '$match': { 'average_score': { '$gte': 4.0 }, 'reviews_count': { '$lt': 20, '$gte': 2 } } }",
            "{ '$sort': { 'average_score': -1 } }",
            "{ '$limit': 10 }",

            // PROIEZIONE PER GAME CARD
            "{ '$project': { " +
                    "'id': '$_id', " +
                    "'title': 1, " +
                    "'img': 1, " +
                    "'average_score': 1, " +
                    "'reviews_count': 1 " +
                    "} }"
    })
    List<GameCardDTO> findHiddenGems();

    // SEARCH BAR
    // ricerca per titolo, restituisce solo dati per la Card --> proiezione: Esclude descrizione, generi, recensioni.
    @Query(value = "{ 'title': { '$regex': ?0, '$options': 'i' } }",
            fields = "{ 'title': 1, 'img': 1, 'average_score': 1, 'reviews_count': 1, '_id': 1 }")
    List<GameMongo> findByTitleRegex(String regexPattern);

    // Visualizzazione LIBRERIA: seleziono solo i campi che mi servono
    @Query(value = "{ '_id': { '$in': ?0 } }",
            fields = "{ 'title': 1, 'img': 1, 'average_score': 1, 'reviews_count': 1, '_id': 1 }")
    List<GameMongo> findBasicInfoByIds(List<String> ids);

    // Scarichiamo solo la lista degli ID delle recensioni linkate allGameReviews e '_id'.
    @Query(value = "{ '_id': ?0 }", fields = "{ 'showOtherReviews': 1, '_id': 1 }")
    GameMongo findLinkedReviewsOnly(String gameId);
}

