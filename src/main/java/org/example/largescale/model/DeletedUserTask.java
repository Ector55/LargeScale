package org.example.largescale.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "cleanup_queue")
public class DeletedUserTask {
    @Id
    private String id;
    private String userId;
    private Instant deletedTime;

}
