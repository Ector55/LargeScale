package org.example.largescale.controller;

import org.example.largescale.DTO.GameCardDTO;
import org.example.largescale.DTO.GameDetailDTO;
import org.example.largescale.DTO.ReviewCardDTO;
import org.example.largescale.DTO.TrendingGameDTO;
import org.example.largescale.model.GameMongo;
import org.example.largescale.service.GameService;
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

    // query per test: localhost:8080/api/games/top-rated
    // query per test: localhost:8080/api/games/top-rated?genre=Action
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
    public ResponseEntity<List<ReviewCardDTO>> getAllGameReviews(@PathVariable String gameId){
        try{
            // Chiama il metodo aggiornato nel service che fa il Batch Load
            return ResponseEntity.ok(gameService.getAllReviews(gameId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // query test: localhost:8080/api/games/top-rated
    @GetMapping("/top-rated")
    public ResponseEntity<List<GameCardDTO>> getTopRatedGames(
            @RequestParam(required = false) String genre
    ) {
        return ResponseEntity.ok(gameService.getTopRatedGames(genre));
    }

    //localhost:8080/api/games/trending/declining
    @GetMapping("/trending/declining")
    public ResponseEntity<List<TrendingGameDTO>> getDecliningGames() {
        return ResponseEntity.ok(gameService.getDecliningGames());
    }

    // query test: localhost:8080/api/games/trending/improving
    @GetMapping("/trending/improving")
    public ResponseEntity<List<TrendingGameDTO>> getImprovingGames() {
        return ResponseEntity.ok(gameService.getImprovingGames());
    }

    // query test: localhost:8080/api/games/hidden-gems
    @GetMapping("/hidden-gems")
    public ResponseEntity<List<GameCardDTO>> getHiddenGems() {
        return ResponseEntity.ok(gameService.getHiddenGems());
    }

    // Dettaglio completo di un singolo gioco (quando si clicca sopra per dettagli)
    // Query test GET: localhost:8080/api/games/{gameId}
    @GetMapping("/{gameId}")
    public ResponseEntity<GameDetailDTO> getGameDetails(@PathVariable String gameId) {
        try {
            // Restituisce descrizione, generi e tutte le info pesanti
            return ResponseEntity.ok(gameService.getGameDetails(gameId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<GameCardDTO>> searchGames(@RequestParam("q") String query) {
        // Restituisce una lista leggera di risultati
        return ResponseEntity.ok(gameService.searchGames(query));
    }
}
