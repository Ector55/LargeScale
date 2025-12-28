package org.example.largescalecazzi.controller;

import org.example.largescalecazzi.model.GameMongo;
import org.example.largescalecazzi.repository.GameRepository;
import org.example.largescalecazzi.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {
    @Autowired
    private GameService gameService;

    @GetMapping
    public List<GameMongo> findAll() {
        return gameService.findAllGame();
    }
}
