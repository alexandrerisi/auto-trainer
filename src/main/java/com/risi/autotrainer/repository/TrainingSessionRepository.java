package com.risi.autotrainer.repository;

import com.risi.autotrainer.domain.TrainingSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainingSessionRepository extends MongoRepository<TrainingSession, String> {

    List<TrainingSession> findByDateBetween(LocalDateTime from, LocalDateTime to);
}
