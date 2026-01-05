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
import java.util.Map;
import java.util.stream.Collectors;

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

    public void updateGameHours(String userID, String gameID, double addedHours) {
        UserMongo userMongo = userMongoRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User with id " + userID + " not found"));

        if (userMongo.getMyGames() == null) {
            throw new RuntimeException("Games not found in library!");
        }

        UserMongo.MyGames myGame = userMongo.getMyGames().stream()
                .filter(g -> g.getGameId().equals(gameID))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Game with id " + gameID + " not found"));

        double newTotalHours = myGame.getHours() + addedHours;
        myGame.setHours(newTotalHours);

        List<UserMongo.TopPlayedGames> currentTop = userMongo.getTopPlayedGames();
        if (currentTop == null) currentTop = new ArrayList<>();

        var existingInTop = currentTop.stream()
                .filter(g -> g.getGameId().equals(gameID))
                .findFirst();

        if (existingInTop.isPresent()) {
            existingInTop.get().setHours(newTotalHours);
            currentTop.sort((g1, g2) -> Double.compare(g2.getHours(), g1.getHours()));
            userMongo.setTopPlayedGames(currentTop);

        } else {
            double threshold = 0.0;
            if (currentTop.size() >= 10) {
                threshold = currentTop.get(currentTop.size() - 1).getHours();
            }

            if (currentTop.size() < 10 || newTotalHours > threshold) {
                rebuildTopList(userMongo);
            }
        }

        userMongoRepository.save(userMongo);
    }

    private void rebuildTopList(UserMongo userMongo) {
        List<UserMongo.MyGames> sortedLibrary = new ArrayList<>(userMongo.getMyGames());
        sortedLibrary.sort((g1, g2) -> Double.compare(g2.getHours(), g1.getHours()));

        int limit = Math.min(sortedLibrary.size(), 10);
        List<UserMongo.MyGames> top10Source = sortedLibrary.subList(0, limit);

        List<String> top10Ids = top10Source.stream()
                .map(UserMongo.MyGames::getGameId)
                .collect(Collectors.toList());

        List<GameMongo> gameDetailsList = (List<GameMongo>) gameMongoRepository.findAllById(top10Ids);

        Map<String, GameMongo> gamesMap = gameDetailsList.stream()
                .collect(Collectors.toMap(GameMongo::getId, game -> game));

        List<UserMongo.TopPlayedGames> newTopPlayed = new ArrayList<>();

        for (UserMongo.MyGames source : top10Source) {
            GameMongo details = gamesMap.get(source.getGameId());

            if (details != null) {
                newTopPlayed.add(new UserMongo.TopPlayedGames(
                        details.getId(),
                        details.getTitle(),
                        details.getImg(),
                        source.getHours()
                ));
            }
        }
        userMongo.setTopPlayedGames(newTopPlayed);
    }
}
