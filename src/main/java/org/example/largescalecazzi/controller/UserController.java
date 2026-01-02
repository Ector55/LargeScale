package org.example.largescalecazzi.controller;

import org.example.largescalecazzi.model.UserMongo;
import org.example.largescalecazzi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
