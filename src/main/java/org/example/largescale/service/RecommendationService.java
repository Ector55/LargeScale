package org.example.largescale.service;

import org.example.largescale.DTO.GameCardDTO;
import org.example.largescale.DTO.UserCardDTO;
import org.example.largescale.model.GameMongo;
import org.example.largescale.model.GameNeo4j;
import org.example.largescale.model.UserNeo4j;
import org.example.largescale.repository.GameMongoRepository;
import org.example.largescale.repository.GameNeo4jRepository;
import org.example.largescale.repository.UserMongoRepository;
import org.example.largescale.repository.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//TUTTA LA CLASSE DA TESTARE ANCORA
@Service
public class RecommendationService {

    @Autowired
    private GameNeo4jRepository gameNeo4jRepository;
    @Autowired
    private UserNeo4jRepository userNeo4jRepository;

    // Ci servono le repo di Mongo per recuperare immagini e nomi completi
    @Autowired
    private GameMongoRepository gameMongoRepository;
    @Autowired
    private UserMongoRepository userMongoRepository;

    // 1. Social Recs: Giochi consigliati dagli amici
    public List<GameCardDTO> getFriendRecommendations(String userId) {
        // A. Chiedo al Grafo gli ID dei giochi
        List<GameNeo4j> recommendedNodes = gameNeo4jRepository.findGameRecommendationsAmongFriends(userId);

        // B. Converto in DTO completo (con Immagine da Mongo)
        return mapGamesToDTO(recommendedNodes);
    }

    // 2. Content Recs: Giochi simili per genere
    public List<GameCardDTO> getContentRecommendations(String userId) {
        List<GameNeo4j> recommendedNodes = gameNeo4jRepository.findGameRecommendationsByGenere(userId);
        return mapGamesToDTO(recommendedNodes);
    }

    // 3. Global Recs: Giochi influenti per genere
    public List<GameCardDTO> getInfluentialGames(String genre) {
        List<GameNeo4j> nodes = gameNeo4jRepository.findMostInfluentialGameByGenre(genre);
        return mapGamesToDTO(nodes);
    }

    // 4. Friend Discovery: Utenti simili a te
    public List<UserCardDTO> getPotentialFriends(String userId) {
        // A. Chiedo al Grafo gli ID degli utenti
        List<UserNeo4j> userNodes = userNeo4jRepository.findPotentialFriends(userId);

        if (userNodes.isEmpty()) return Collections.emptyList();

        List<String> userIds = userNodes.stream().map(UserNeo4j::getUserId).collect(Collectors.toList());

        // B. Recupero i dettagli anagrafici da Mongo (Nome, Cognome)
        // (Assicurati di avere findFriendsNameAndIdByIds in UserMongoRepository)
        return userMongoRepository.findFriendsNameAndIdByIds(userIds).stream()
                .map(u -> new UserCardDTO(u.getId(), u.getUsername(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
    }

    // --- HELPER: Merge Neo4j IDs + Mongo Data ---
    private List<GameCardDTO> mapGamesToDTO(List<GameNeo4j> neoNodes) {
        if (neoNodes.isEmpty()) return Collections.emptyList();

        List<String> ids = neoNodes.stream().map(GameNeo4j::getGameId).collect(Collectors.toList());

        // Batch Load da Mongo (Titolo, Img, Voto)
        List<GameMongo> mongoDetails = gameMongoRepository.findBasicInfoByIds(ids);

        Map<String, GameMongo> detailsMap = mongoDetails.stream()
                .collect(Collectors.toMap(GameMongo::getId, g -> g));

        return neoNodes.stream()
                .filter(node -> detailsMap.containsKey(node.getGameId()))
                .map(node -> {
                    GameMongo detail = detailsMap.get(node.getGameId());
                    return new GameCardDTO(
                            detail.getId(),
                            detail.getTitle(),
                            detail.getImg(),
                            detail.getAverageScore(),
                            detail.getReviewsCount()
                    );
                })
                .collect(Collectors.toList());
    }
}
