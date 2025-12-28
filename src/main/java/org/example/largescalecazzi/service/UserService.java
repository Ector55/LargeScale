package org.example.largescalecazzi.service;

import org.example.largescalecazzi.repository.GameRepository;
import org.example.largescalecazzi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;
}
