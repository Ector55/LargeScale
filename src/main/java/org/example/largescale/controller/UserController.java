package org.example.largescale.controller;

import org.example.largescale.DTO.LibraryGameDTO;
import org.example.largescale.DTO.UserCardDTO;
import org.example.largescale.model.UserMongo;
import org.example.largescale.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    //---ADD/REGISTRAZIONE new USER---
    // query test POST: "localhost:8080/api/users/{USER_ID}"
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserMongo user, BindingResult bindingResult) {
        // check se ci sono errori di validazione (email vuota, username corto...)
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body("Validation Error: " + errors);
        }

        try {
            UserMongo createdUser = userService.registerUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            // Cattura l'errore "Username o Email già esistenti" dal Service
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //---DELETE USER---
    // query test DELETE: "localhost:8080/api/users/{register}" e mettere il body
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    //--- GETTERS ---
    // Query test GET: localhost:8080/api/users/{USER_ID}/topPlayedGames
    @GetMapping("/{userId}/topPlayedGames")
    public ResponseEntity<List<UserMongo.TopPlayedGames>> getTopPlayedGames(@PathVariable String userId){
        try{
            List<UserMongo.TopPlayedGames> topPlayedGames = userService.getTopGames(userId);
            return ResponseEntity.ok(topPlayedGames);
        } catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    // Query test GET: localhost:8080/api/users/{USER_ID}/library
    @GetMapping("/{userId}/library")
    public ResponseEntity<List<LibraryGameDTO>> getMyGames(@PathVariable String userId){
        try{
            // Il Service ora fa il "Merge" e restituisce i dati pronti per il Frontend
            return ResponseEntity.ok(userService.getMyGames(userId));
        } catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    // ADD game alla libreria
    // Query Test POST: localhost:8080/api/users/{USER_ID}/games/{GAME_ID}
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

    // UPDATE ore di un gioco
    // Query Test PATCH: localhost:8080/api/users/{USER_ID}/games/{GAME_ID}?hours=100
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

    // --- GESTIONE FRIENDS ---
    // Query test POST: localhost:8080/api/users/ID1/friends/ID2
    @PostMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<String> addFriend(
            @PathVariable String userId,
            @PathVariable String friendId
    ) {
        try {
            userService.addFriend(userId, friendId);
            return ResponseEntity.ok("Friend added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    // Query test DELETE: localhost:8080/api/users/ID1/friends/ID2
    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<String> removeFriend(
            @PathVariable String userId,
            @PathVariable String friendId
    ) {
        try {
            userService.removeFriend(userId, friendId);
            return ResponseEntity.ok("Friend removed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    // Query test: GET localhost:8080/api/users/search?q=mario
    @GetMapping("/search")
    public ResponseEntity<List<UserCardDTO>> searchUsers(@RequestParam("q") String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    // Query test: GET localhost:8080/api/users/{id}/friends
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserCardDTO>> getUserFriendsDetails(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userService.getUserFriendsDetails(userId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
