package org.example.largescalecazzi.controller;

import org.example.largescalecazzi.model.ReviewMongo;
import org.example.largescalecazzi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    // POST: localhost:8080/api/reviews/game/{gameId}
    @PostMapping("/game/{gameId}")
    public ResponseEntity<?> addReview(
            @PathVariable String gameId,
            @RequestBody ReviewMongo reviewBody
    ) {
        try {
            ReviewMongo createdReview = reviewService.addReview(
                    reviewBody.getUserId(),
                    gameId,
                    reviewBody.getScore(),
                    reviewBody.getDescription()
            );

            return ResponseEntity.ok(createdReview);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

}
