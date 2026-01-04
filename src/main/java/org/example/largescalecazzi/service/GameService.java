package org.example.largescalecazzi.service;

import org.example.largescalecazzi.model.GameMongo;
import org.example.largescalecazzi.repository.GameMongoRepository;
import org.example.largescalecazzi.repository.ReviewMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
}
