package org.example.largescale.service;

import org.example.largescale.DTO.UserCardDTO;
import org.example.largescale.model.DeletedUserTask;
import org.example.largescale.model.GameMongo;
import org.example.largescale.model.UserMongo;
import org.example.largescale.model.UserNeo4j;
import org.example.largescale.repository.CleanupQueueRepository;
import org.example.largescale.repository.GameMongoRepository;
import org.example.largescale.repository.UserMongoRepository;
import org.example.largescale.repository.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;

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
    @Autowired
    private UserNeo4jRepository userNeo4jRepository;
    @Autowired
    private CleanupQueueRepository cleanupQueueRepository;

    // --- REGISTRAZIONE ---
    public UserMongo registerUser(UserMongo newUser) {
        UserMongo savedUser;

        //Salvataggio su MongoDb
        try {
            savedUser = userMongoRepository.save(newUser);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Errore: Username o Email già esistenti");
        }

        // Salvataggio Neo4j
        try {
            UserNeo4j neoUser = new UserNeo4j(savedUser.getId(), savedUser.getUsername());
            userNeo4jRepository.save(neoUser);
        } catch (Exception e) {
            System.err.println("WARN: Failed to sync user to Neo4j: " + e.getMessage());
        }

        return savedUser;
    }

    // --- DELETE USER ---
    public void deleteUser(String userId) {

        if (!userMongoRepository.existsById(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }

        userMongoRepository.deleteById(userId);

        userNeo4jRepository.deleteById(userId);

        cleanupQueueRepository.save(new DeletedUserTask(null, userId, java.time.Instant.now()));
    }

    // --- GETTERS ---
    public List<UserMongo.TopPlayedGames> getTopGames(String userID){
        UserMongo userMongo = userMongoRepository.findById(userID)
                .orElseThrow(()-> new RuntimeException("User with id" + userID + "not found"));
        List<UserMongo.TopPlayedGames> topGames = userMongo.getTopPlayedGames();
        if(topGames == null){
            return Collections.emptyList();
        }
        return topGames;
    }

    public Page<GameMongo> getMyGames(String userId, int page, int size, String sortBy, String direction) {
        // 1. Recupera la lista degli ID dell'utente (usando la query leggera che avevamo già)
        UserMongo user = userMongoRepository.findMyGamesOnly(userId);

        // Gestione casi vuoti
        if (user == null || user.getMyGames() == null || user.getMyGames().isEmpty()) {
            return Page.empty();
        }

        // 2. Estrai solo la lista di Stringhe (ID)
        List<String> gameIds = user.getMyGames().stream()
                .map(UserMongo.MyGames::getGameId)
                .collect(Collectors.toList());

        // 3. Crea il Pageable standard
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        // 4. DELEGA TOTALE A MONGO: "Dammi i giochi che sono in questa lista, ma solo pagina X"
        return gameMongoRepository.findByIdIn(gameIds, pageable);
    }

    // --- LIBRERIA GIOCHI ---
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
            throw new RuntimeException("You already own this game!");
        }

        UserMongo.MyGames myGames = new UserMongo.MyGames(gameID, 0.0);
        userMongo.getMyGames().add(myGames);

        // Save su Mongo
        userMongoRepository.save(userMongo);

        // Save su Neo4j (viene creata relazione PLAYS)
        userNeo4jRepository.addGameToLibrary(userID, gameID);
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

        // Salvataggio Mongo
        userMongoRepository.save(userMongo);

        //Salvataggio Neo4j
        userNeo4jRepository.updateGameHours(userID, gameID, myGame.getHours());
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


    // --- 4. GESTIONE FRIENDS ---
    public void addFriend(String userId, String friendId) {
        if (userId.equals(friendId)) throw new RuntimeException("Non puoi essere amico di te stesso!");

        // 1. PRIMA su Neo4j (Obbligatorio)
        userNeo4jRepository.addFriend(userId, friendId);

        // 2. POI su Mongo (Best Effort / Backup)
        try {
            UserMongo user = userMongoRepository.findById(userId).orElseThrow();
            UserMongo friend = userMongoRepository.findById(friendId).orElseThrow();

            if (user.getFriends() == null) user.setFriends(new ArrayList<>());
            if (friend.getFriends() == null) friend.setFriends(new ArrayList<>());

            boolean updated = false;
            if (!user.getFriends().contains(friendId)) {
                user.getFriends().add(friendId);
                userMongoRepository.save(user);
                updated = true;
            }
            if (!friend.getFriends().contains(userId)) {
                friend.getFriends().add(userId);
                userMongoRepository.save(friend);
                updated = true;
            }
        } catch (Exception e) {
            System.err.println("WARN: Sync Mongo fallito (Amicizia salvata solo su Neo4j): " + e.getMessage());
        }
    }

    public void removeFriend(String userId, String friendId) {
        // 1. Delete Neo4j
        userNeo4jRepository.removeFriend(userId, friendId);

        // 2. Delete Mongo (Best Effort)
        try {
            UserMongo user = userMongoRepository.findById(userId).orElse(null);
            UserMongo friend = userMongoRepository.findById(friendId).orElse(null);

            if (user != null && user.getFriends() != null && user.getFriends().remove(friendId)) {
                userMongoRepository.save(user);
            }
            if (friend != null && friend.getFriends() != null && friend.getFriends().remove(userId)) {
                userMongoRepository.save(friend);
            }
        } catch (Exception e) {
            System.err.println("WARN: Rimozione Mongo fallita: " + e.getMessage());
        }
    }

    // --- Search Bar --- per la ricerca di Users ---
    public List<UserCardDTO> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) return Collections.emptyList();

        List<UserMongo> users = userMongoRepository.findByUsernameContainingIgnoreCase(query);

        return mapToDTO(users);
    }

    // --- Show Details Friend ---
    public List<UserCardDTO> getUserFriendsDetails(String userId) {
        // 1. Neo4j (Verità)
        List<String> realFriendIds = userNeo4jRepository.findFriendIds(userId);

        if (realFriendIds.isEmpty()) return Collections.emptyList();

        // 2. Mongo (Dettagli)
        List<UserMongo> friendsObjects = userMongoRepository.findAllById(realFriendIds);

        // 3. Mapping (usando il metodo helper sicuro)
        return mapToDTO(friendsObjects);
    }

    //Metodo che ci serve sia per getUserFriendsDetails() che in searchUsers(), che prende solo alcune informazioni, e non carica tutti i dati utente
    private List<UserCardDTO> mapToDTO(List<UserMongo> users) {
        if (users == null || users.isEmpty()) return Collections.emptyList();

        return users.stream()
                .filter(java.util.Objects::nonNull) // PROTEZIONE AGGIUNTA QUI
                .map(u -> new UserCardDTO(u.getId(), u.getUsername(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
    }

    public boolean userExists(String userId) {
        return userMongoRepository.existsById(userId);
    }

}
