package org.example.largescalecazzi.controller;

import org.example.largescalecazzi.model.Game;
import org.example.largescalecazzi.repo.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {
    @Autowired
    private GameRepo gameRepo;

    @GetMapping
    public List<Game> findAll() {
        return gameRepo.findAll();
    }
}
