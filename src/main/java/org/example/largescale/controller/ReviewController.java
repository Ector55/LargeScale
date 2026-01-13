package org.example.largescale.controller;

import org.example.largescale.model.ReviewMongo;
import org.example.largescale.service.ReviewService;
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

    // DELETE: Cancella recensione
    // Query test DELETE: localhost:8080/api/reviews/{reviewId}?userId={USER_ID}
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable String reviewId,
            @RequestParam String userId
    ) {
        try {
            reviewService.deleteReview(reviewId, userId);
            return ResponseEntity.ok("Review deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
