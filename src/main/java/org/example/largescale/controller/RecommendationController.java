package org.example.largescale.controller;

import org.example.largescale.DTO.GameCardDTO;
import org.example.largescale.DTO.UserCardDTO;
import org.example.largescale.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TUTTA LA CLASSE DA TESTARE ANCORA
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    // GET: Consigli basati sugli amici
    @GetMapping("/social/{userId}")
    public ResponseEntity<List<GameCardDTO>> getSocialRecommendations(@PathVariable String userId) {
        return ResponseEntity.ok(recommendationService.getFriendRecommendations(userId));
    }

    // GET: Consigli basati sui generi giocati
    @GetMapping("/content/{userId}")
    public ResponseEntity<List<GameCardDTO>> getContentRecommendations(@PathVariable String userId) {
        return ResponseEntity.ok(recommendationService.getContentRecommendations(userId));
    }

    // GET: Giochi influenti di un genere
    // Query Test GET: localhost:8080/api/recommendations/influential?genre=Free To Play
    @GetMapping("/influential")
    public ResponseEntity<List<GameCardDTO>> getInfluentialGames(@RequestParam String genre) {
        return ResponseEntity.ok(recommendationService.getInfluentialGames(genre));
    }

    // GET: Possibili amici
    @GetMapping("/people/{userId}")
    public ResponseEntity<List<UserCardDTO>> getPotentialFriends(@PathVariable String userId) {
        return ResponseEntity.ok(recommendationService.getPotentialFriends(userId));
    }
}