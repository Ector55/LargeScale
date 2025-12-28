package org.example.largescalecazzi.service;

import org.example.largescalecazzi.repository.GameRepository;
import org.example.largescalecazzi.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private GameRepository gameRepository;
}
