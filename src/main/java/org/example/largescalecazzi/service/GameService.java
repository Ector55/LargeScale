package org.example.largescalecazzi.service;

import org.example.largescalecazzi.DTO.GameCardDTO;
import org.example.largescalecazzi.DTO.GameDetailDTO;
import org.example.largescalecazzi.DTO.ReviewCardDTO;
import org.example.largescalecazzi.DTO.TrendingGameDTO;
import org.example.largescalecazzi.model.GameMongo;
import org.example.largescalecazzi.model.ReviewMongo;
import org.example.largescalecazzi.repository.GameMongoRepository;
import org.example.largescalecazzi.repository.ReviewMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameMongoRepository gameMongoRepository;
    @Autowired
    private ReviewMongoRepository reviewMongoRepository;


    public Page<GameMongo> getAllGames(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        if (pageSize > 50) {
            pageSize = 50;
        }

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        return gameMongoRepository.findAll(pageRequest);
    }

    public List<GameMongo.LastReviews> getLastReviews(String gameId) {
        GameMongo gameMongo = gameMongoRepository.findById(gameId).
                orElseThrow(() -> new RuntimeException("Game with id" + gameId + "not found"));
        List<GameMongo.LastReviews> lastReviews = gameMongo.getLastReviews();
        if (lastReviews == null || lastReviews.isEmpty()) {
            return Collections.emptyList();
        }
        return lastReviews;
    }

    public List<ReviewCardDTO> getAllReviews(String gameId) {
        GameMongo gameMongo = gameMongoRepository.findLinkedReviewsOnly(gameId);

        if (gameMongo.getAllGameReviews() == null || gameMongo.getAllGameReviews().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> reviewIds = gameMongo.getAllGameReviews().stream()
                .map(GameMongo.AllGameReviews::getReviewId)
                .collect(Collectors.toList());

        List<ReviewMongo> reviews = reviewMongoRepository.findBasicInfoByIds(reviewIds);

        return reviews.stream()
                .map(r -> new ReviewCardDTO(
                        r.getId(),
                        r.getUserId(),
                        r.getScore(),
                        r.getDescription(),
                        r.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    public List<GameCardDTO> getTopRatedGames(String genre) {
        String filterGenre = (genre == null || genre.trim().isEmpty()) ? null : genre;
        return gameMongoRepository.findTop10BestRatedGames(filterGenre);
    }

    public List<TrendingGameDTO> getDecliningGames() {
        return gameMongoRepository.findTopDecliningGames();
    }

    public List<TrendingGameDTO> getImprovingGames() {
        return gameMongoRepository.findTopImprovingGames();
    }

    public List<GameCardDTO> getHiddenGems() {
        return gameMongoRepository.findHiddenGems();
    }

    // 1. Visualizzare i dettagli di un gioco
    public GameDetailDTO getGameDetails(String gameId) {
        GameMongo g = gameMongoRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        return new GameDetailDTO(
                g.getId(), g.getTitle(), g.getDescription(), g.getImg(),
                g.getGenres(), g.getAverageScore(), g.getReviewsCount(),
                g.getLastReviews() != null ? g.getLastReviews() : Collections.emptyList()
        );
    }

    // SEARCH game by titolo
    public List<GameCardDTO> searchGames(String query) {
        if (query == null || query.trim().isEmpty()) return Collections.emptyList();

        // "^" per la regex "Cerca titoli che INIZIANO con questa parola"
        String regex = "^" + query;
        List<GameMongo> games = gameMongoRepository.findByTitleRegex(regex);

        return games.stream()
                .map(g -> new GameCardDTO(
                        g.getId(), g.getTitle(), g.getImg(),
                        g.getAverageScore(), g.getReviewsCount()
                ))
                .collect(Collectors.toList());
    }


}
