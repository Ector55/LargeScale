package org.example.largescalecazzi.controller;

import org.example.largescalecazzi.model.GameMongo;
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

    @GetMapping("/{gameId}/lastReviews")
    public ResponseEntity<List<GameMongo.LastReviews>> getLastReviews(@PathVariable String gameId){
        try{
            List<GameMongo.LastReviews> lastReviews = gameService.getLastReviews(gameId);
            return ResponseEntity.ok(lastReviews);
        } catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{gameId}/allReviews")
    public ResponseEntity<List<GameMongo.AllGameReviews>> getAllGameReviews(@PathVariable String gameId){
        try{
            List<GameMongo.AllGameReviews> allGameReviews = gameService.getAllReviews(gameId);
            return ResponseEntity.ok(allGameReviews);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
