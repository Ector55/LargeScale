package org.example.largescalecazzi.service;

import org.example.largescalecazzi.model.GameMongo;
import org.example.largescalecazzi.model.ReviewMongo;
import org.example.largescalecazzi.repository.GameMongoRepository;
import org.example.largescalecazzi.repository.ReviewMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewMongoRepository reviewMongoRepository;
    @Autowired
    private GameMongoRepository gameMongoRepository;
    @Autowired
    private UserService userService;


    // ADD new REVIEW
    // sia alla Collection, sia al gioco a cui fa riferimento e si aggiorna la media score del gioco.
    public ReviewMongo addReview(String userId, String gameId, Integer score, String description) {

        if (!userService.userExists(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }

        GameMongo game = gameMongoRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found: " + gameId));

        if (score < 1 || score > 5) {
            throw new RuntimeException("Score must be between 1 and 5");
        }

        // Controllo se esiste già una recensione per questo utente per questo gioco
        if (reviewMongoRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw new RuntimeException("Review already present! ");
        }

        ReviewMongo review = new ReviewMongo();
        review.setUserId(userId);
        review.setGameId(gameId);
        review.setScore(score);
        review.setDescription(description);
        review.setTimestamp(Instant.now());

        ReviewMongo savedReview = reviewMongoRepository.save(review);

        try {
            updateGameStats(game, savedReview);
        } catch (Exception e) {
            System.err.println("WARN: Review salvata ID " + savedReview.getId() +
                    " ma fallito aggiornamento stats gioco " + gameId);
        }

        return savedReview;
    }


    //serve per l'ADD di sopra
    private void updateGameStats(GameMongo game, ReviewMongo review) {
        if (game.getLastReviews() == null) game.setLastReviews(new ArrayList<>());
        if (game.getAllGameReviews() == null) game.setAllGameReviews(new ArrayList<>());

        // --- Gestione lista "LAST REVIEWS" (Partial Embedding - Top 10) ---
        GameMongo.LastReviews lastReviewEntry = new GameMongo.LastReviews(
                review.getId(),
                review.getUserId(),
                review.getScore(),
                review.getDescription()
        );

        game.getLastReviews().add(0, lastReviewEntry);
        if (game.getLastReviews().size() > 10) {
            game.getLastReviews().subList(10, game.getLastReviews().size()).clear();
        }

        // ---  Gestione lista linking (Top 100 On Demand) ---
        game.getAllGameReviews().add(0, new GameMongo.AllGameReviews(review.getId()));

        if (game.getAllGameReviews().size() > 100) {
            game.getAllGameReviews().subList(100, game.getAllGameReviews().size()).clear();
        }

        // ---  Ricalcolo Statistiche gioco ---
        int currentCount = game.getReviewsCount() == null ? 0 : game.getReviewsCount();
        int currentTotalScore = game.getTotalScore() == null ? 0 : game.getTotalScore();

        int newCount = currentCount + 1;
        int newTotalScore = currentTotalScore + review.getScore();

        double newAverage = (double) newTotalScore / newCount;
        newAverage = Math.round(newAverage * 100.0) / 100.0;

        game.setReviewsCount(newCount);
        game.setTotalScore(newTotalScore);
        game.setAverageScore(newAverage);

        gameMongoRepository.save(game);
    }

    public void deleteReview(String reviewId, String userId) {
        ReviewMongo review = reviewMongoRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("You can't delete reviews because you are not the owner of this game");
        }

        GameMongo game = gameMongoRepository.findById(review.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found linked to review"));

        // 4. Aggiorno le statistiche e le liste dentro il Gioco
        removeReviewFromGameStats(game, review);

        // 5. Cancello fisicamente la review dalla collection
        reviewMongoRepository.delete(review);
    }

    private void removeReviewFromGameStats(GameMongo game, ReviewMongo review) {
        // Rimuovo dalla lista embedded "LastReviews"
        if (game.getLastReviews() != null) {
            game.getLastReviews().removeIf(r ->
                    r.getReviewId() != null && r.getReviewId().equals(review.getId())
            );
        }

        // Rimuovo dalla lista linked "AllGameReviews"
        if (game.getAllGameReviews() != null) {
            game.getAllGameReviews().removeIf(r ->
                    r.getReviewId() != null && r.getReviewId().equals(review.getId())
            );
        }

        // Ricalcolo statistiche
        int currentCount = game.getReviewsCount() == null ? 0 : game.getReviewsCount();
        int currentTotal = game.getTotalScore() == null ? 0 : game.getTotalScore();

        int newCount = Math.max(0, currentCount - 1);
        int newTotal = Math.max(0, currentTotal - review.getScore());

        double newAverage = 0.0;
        if (newCount > 0) {
            newAverage = (double) newTotal / newCount;
            // Arrotondamento a 2 decimali
            newAverage = Math.round(newAverage * 100.0) / 100.0;
        }

        game.setReviewsCount(newCount);
        game.setTotalScore(newTotal);
        game.setAverageScore(newAverage);

        gameMongoRepository.save(game);
    }
}
