package com.risi.autotrainer.repository;

import com.risi.autotrainer.domain.Exercise;
import com.risi.autotrainer.domain.TrainingSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class TrainingSessionRepository {

    @Autowired
    private MongoTemplate template;
    @Autowired
    private TrainingSessionRepositoryInterface repository;

    public List<TrainingSession> findByDateBetween(LocalDateTime from, LocalDateTime to, int limit) {
        return repository.findByDateBetweenOrderByDateDesc(from, to, PageRequest.of(0, limit));
    }

    public void save(TrainingSession session) {
        repository.save(session);
    }

    public void deleteByDateBetween(LocalDateTime from, LocalDateTime to) {
        repository.deleteByDateBetween(from, to);
    }

    @org.springframework.data.mongodb.repository.Query(sort = "{ date : 1 }")
    public List<TrainingSession> findByExercise(Exercise exercise, int limit) {
        Query query = query(where("sets").elemMatch(where("exercise").is(exercise)));
        return template.find(query.limit(limit), TrainingSession.class);
    }
}
