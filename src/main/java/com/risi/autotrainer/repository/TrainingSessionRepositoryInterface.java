package com.risi.autotrainer.repository;

import com.risi.autotrainer.domain.TrainingSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainingSessionRepositoryInterface extends MongoRepository<TrainingSession, String> {

    List<TrainingSession> findByUserIdAndDateBetweenOrderByDateDesc(String userId,
                                                                    LocalDateTime from,
                                                                    LocalDateTime to,
                                                                    Pageable pageable);

    void deleteByDate(LocalDateTime date);

    Optional<TrainingSession> findByUserIdAndDate(String userId, LocalDateTime from);
}
