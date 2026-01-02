package org.example.largescalecazzi.service;

import org.example.largescalecazzi.model.UserMongo;
import org.example.largescalecazzi.repository.GameRepository;
import org.example.largescalecazzi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;

    public List<UserMongo.TopPlayedGames> getTopGames(String userID){
        UserMongo userMongo = userRepository.findById(userID)
                .orElseThrow(()-> new RuntimeException("User with id" + userID + "not found"));
        List<UserMongo.TopPlayedGames> topGames = userMongo.getTopPlayedGames();
        if(topGames == null){
            return Collections.emptyList();
        }
        return topGames;
    }

    public List<UserMongo.MyGames> getMyGames(String userID){
        UserMongo userMongo = userRepository.findById(userID)
                .orElseThrow(()-> new RuntimeException("User with id" + userID + "not found"));
        List<UserMongo.MyGames> myGames = userMongo.getMyGames();
        if(myGames == null){
            return Collections.emptyList();
        }
        else return myGames;
    }
}
