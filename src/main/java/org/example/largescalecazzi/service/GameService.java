package org.example.largescalecazzi.service;

import org.example.largescalecazzi.DTO.GameCardDTO;
import org.example.largescalecazzi.DTO.TrendingGameDTO;
import org.example.largescalecazzi.model.GameMongo;
import org.example.largescalecazzi.repository.GameMongoRepository;
import org.example.largescalecazzi.repository.ReviewMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameMongoRepository gameMongoRepository;
    @Autowired
    private ReviewMongoRepository reviewMongoRepository;

    public Page<GameMongo> getAllGames(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        if(pageSize > 50){
            pageSize = 50;
        }

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        return gameMongoRepository.findAll(pageRequest);
    }

    public List<GameMongo.LastReviews> getLastReviews(String gameId) {
        GameMongo gameMongo = gameMongoRepository.findById(gameId).
                orElseThrow(()-> new RuntimeException("Game with id" + gameId + "not found"));
        List<GameMongo.LastReviews> lastReviews = gameMongo.getLastReviews();
        if (lastReviews == null || lastReviews.isEmpty()){
            return Collections.emptyList();
        }
        return lastReviews;
    }

    public List<GameMongo.AllGameReviews> getAllReviews(String gameId) {
        GameMongo gameMongo = gameMongoRepository.findById(gameId)
                .orElseThrow(()-> new RuntimeException("Game with id" + gameId + "not found"));
        List<GameMongo.AllGameReviews> allGameReviews = gameMongo.getAllGameReviews();
        if (allGameReviews == null ||allGameReviews.isEmpty()){
            return Collections.emptyList();
        }
        return allGameReviews;
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
}
