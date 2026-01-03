package org.example.largescalecazzi.controller;

import org.example.largescalecazzi.model.UserMongo;
import org.example.largescalecazzi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}/topPlayedGames")
    public ResponseEntity<List<UserMongo.TopPlayedGames>> getTopPlayedGames(@PathVariable String userId){
        try{
            List<UserMongo.TopPlayedGames> topPlayedGames = userService.getTopGames(userId);
            return ResponseEntity.ok(topPlayedGames);
        } catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/library")
    public ResponseEntity<List<UserMongo.MyGames>> getMyGames(@PathVariable String userId){
        try{
            List<UserMongo.MyGames> myGames = userService.getMyGames(userId);
            return ResponseEntity.ok(myGames);
        } catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/games/{gameId}")
    public ResponseEntity<String> addGame(
            @PathVariable String userId,
            @PathVariable String gameId
    ){
        try {
            userService.addGameToLibrary(userId, gameId);
            return ResponseEntity.ok("Game added");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    @PatchMapping("/{userId}/games/{gameId}")
    public ResponseEntity<String> updateGameHours(
            @PathVariable String userId,
            @PathVariable String gameId,
            @RequestParam double hours
    ){
        try{
            userService.updateGameHours(userId, gameId, hours);
            return ResponseEntity.ok("Game hours updated");
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }
}
