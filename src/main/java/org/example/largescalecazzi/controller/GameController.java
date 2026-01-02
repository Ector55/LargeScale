package org.example.largescalecazzi.controller;

import org.example.largescalecazzi.model.GameMongo;
import org.example.largescalecazzi.repository.GameRepository;
import org.example.largescalecazzi.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {
    @Autowired
    private GameService gameService;

    @GetMapping
    public ResponseEntity<Page<GameMongo>> getAllGames(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(gameService.getAllGames(pageNumber, pageSize, sortBy, direction));
    }
}
