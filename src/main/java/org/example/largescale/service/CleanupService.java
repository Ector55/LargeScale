package org.example.largescale.service;

import org.example.largescale.model.DeletedUserTask;
import org.example.largescale.model.UserMongo;
import org.example.largescale.repository.CleanupQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CleanupService {
    @Autowired
    CleanupQueueRepository cleanupQueueRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final int BATCH_SIZE = 50;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupGhostFriendship() {
        int totalProcessed = 0;
        boolean hasMoreTasks = true;

        while (hasMoreTasks) {
            Page<DeletedUserTask> chunk = cleanupQueueRepository.findAll(PageRequest.of(0, BATCH_SIZE));

            if (chunk.isEmpty()) {
                hasMoreTasks = false;
                break; // Coda vuota, lavoro finito
            }

            List<DeletedUserTask> tasks = chunk.getContent();
            System.out.println("Processing chunk di " + tasks.size() + " utenti...");

            for (DeletedUserTask task : tasks) {
                processSingleTask(task);
            }

            cleanupQueueRepository.deleteAll(tasks);

            totalProcessed += tasks.size();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processSingleTask(DeletedUserTask task) {
        try {
            String targetId = task.getUserId();

            Query query = new Query(Criteria.where("friends").is(targetId));

            Update update = new Update().pull("friends", targetId);

            mongoTemplate.updateMulti(query, update, UserMongo.class);

        } catch (Exception e) {
            System.err.println("ERRORE pulizia per user " + task.getUserId() + ": " + e.getMessage());
        }
    }
}
