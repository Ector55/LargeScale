package org.example.largescalecazzi.service;

import org.example.largescalecazzi.repository.GameMongoRepository;
import org.example.largescalecazzi.repository.ReviewMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ReviewMongoRepository reviewMongoRepository;
    @Autowired
    private GameMongoRepository gameMongoRepository;
}
