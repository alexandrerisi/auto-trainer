package com.risi.autotrainer.repository;

import com.risi.autotrainer.domain.TrainingSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainingSessionRepositoryInterface extends MongoRepository<TrainingSession, String> {

    List<TrainingSession> findByDateBetweenOrderByDateDesc(LocalDateTime from, LocalDateTime to, Pageable pageable);

    void deleteByDateBetween(LocalDateTime from, LocalDateTime to);
}
