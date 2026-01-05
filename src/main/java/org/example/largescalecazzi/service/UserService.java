package org.example.largescalecazzi.service;

import org.example.largescalecazzi.model.GameMongo;
import org.example.largescalecazzi.model.UserMongo;
import org.example.largescalecazzi.repository.GameMongoRepository;
import org.example.largescalecazzi.repository.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMongoRepository userMongoRepository;
    @Autowired
    private GameMongoRepository gameMongoRepository;

    public List<UserMongo.TopPlayedGames> getTopGames(String userID){
        UserMongo userMongo = userMongoRepository.findById(userID)
                .orElseThrow(()-> new RuntimeException("User with id" + userID + "not found"));
        List<UserMongo.TopPlayedGames> topGames = userMongo.getTopPlayedGames();
        if(topGames == null){
            return Collections.emptyList();
        }
        return topGames;
    }

    public List<UserMongo.MyGames> getMyGames(String userID){
        UserMongo userMongo = userMongoRepository.findById(userID)
                .orElseThrow(()-> new RuntimeException("User with id" + userID + "not found"));
        List<UserMongo.MyGames> myGames = userMongo.getMyGames();
        if(myGames == null){
            return Collections.emptyList();
        }
        return myGames;
    }

    public void addGameToLibrary(String userID, String gameID){
        UserMongo userMongo = userMongoRepository.findById(userID)
                .orElseThrow(()-> new RuntimeException("User with id" + userID + "not found"));

        if(userMongo.getMyGames() == null){
            userMongo.setMyGames(new ArrayList<>());
        }

        if(!gameMongoRepository.existsById(gameID)){
            throw new RuntimeException("Game with id" + gameID + " not found");
        }

        boolean alreadyExists = userMongo.getMyGames().stream().anyMatch(g -> g.getGameId().equals(gameID));
        if(alreadyExists){
            throw new RuntimeException("You just own this game!");
        }

        UserMongo.MyGames myGames = new UserMongo.MyGames(gameID, 0.0);
        userMongo.getMyGames().add(myGames);

        userMongoRepository.save(userMongo);


    }

    public void updateGameHours(String userID, String gameID, double hours){
        UserMongo userMongo = userMongoRepository.findById(userID)
                .orElseThrow(()-> new RuntimeException("User with id" + userID + "not found"));

        if(userMongo.getMyGames() == null){
            throw new RuntimeException("Games not found in library!");
        }

        UserMongo.MyGames game = userMongo.getMyGames().stream()
                .filter(g -> g.getGameId().equals(gameID))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Game with id" + gameID + " not found"));

        double currentHours = game.getHours();
        game.setHours(currentHours + hours);

        updateTopList(userMongo);
        userMongoRepository.save(userMongo);
    }

    private void updateTopList(UserMongo userMongo){
        List<UserMongo.MyGames> sortedLibrary = new ArrayList<>(userMongo.getMyGames());
        sortedLibrary.sort((g1, g2) -> Double.compare(g2.getHours(), g1.getHours()));

        int limit = Math.min(sortedLibrary.size(), 10);
        List<UserMongo.MyGames> top10 = sortedLibrary.subList(0, limit);

        List<UserMongo.TopPlayedGames> newTopPlayed = new ArrayList<>();
        for(UserMongo.MyGames myGame : top10){
            UserMongo.TopPlayedGames existingGame = null;
            if(userMongo.getTopPlayedGames() != null){
                existingGame = userMongo.getTopPlayedGames().stream()
                        .filter(g -> g.getGameId().equals(myGame.getGameId()))
                        .findFirst()
                        .orElse(null);
            }

            if(existingGame != null){
                existingGame.setHours(myGame.getHours());
                newTopPlayed.add(existingGame);
            } else {
                GameMongo gameMongo = gameMongoRepository.findById(myGame.getGameId())
                        .orElseThrow(()-> new RuntimeException("Game with id" + myGame.getGameId() + "not found"));

                UserMongo.TopPlayedGames topPlayedGames = new UserMongo.TopPlayedGames(
                        gameMongo.getId(),
                        gameMongo.getTitle(),
                        gameMongo.getImg(),
                        myGame.getHours()
                );
                newTopPlayed.add(topPlayedGames);
            }
        }

        userMongo.setTopPlayedGames(newTopPlayed);
    }
}
