package org.example.largescalecazzi.service;

import org.example.largescalecazzi.model.GameMongo;
import org.example.largescalecazzi.repository.GameRepository;
import org.example.largescalecazzi.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public List<GameMongo> findAllGame(){
        return gameRepository.findAll();
    }
}
