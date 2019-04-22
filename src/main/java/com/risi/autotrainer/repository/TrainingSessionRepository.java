package com.risi.autotrainer.repository;

import com.risi.autotrainer.domain.Exercise;
import com.risi.autotrainer.domain.TrainingSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class TrainingSessionRepository {

    @Autowired
    private MongoTemplate template;
    @Autowired
    private TrainingSessionRepositoryInterface repository;

    public List<TrainingSession> findByDateBetween(String userId, LocalDateTime from, LocalDateTime to, int limit) {
        return repository.findByUserIdAndDateBetweenOrderByDateDesc(userId, from, to, PageRequest.of(0, limit));
    }

    public void save(TrainingSession session) {
        repository.save(session);
    }

    public void deleteByDate(LocalDateTime from) {
        repository.deleteByDate(from);
    }

    public List<TrainingSession> findByExercise(Exercise exercise, int limit) {
        Query query = query(where("sets").elemMatch(where("exercise").is(exercise)))
                .with(new Sort(Sort.Direction.DESC, "date"));
        return template.find(query.limit(limit), TrainingSession.class);
    }

    public Optional<TrainingSession> findSingleTrainingSessionByDate(String userId, LocalDateTime date) {
        return repository.findByUserIdAndDate(userId, date);
    }

    public List<TrainingSession> findByUserIdAndExerciseAndDateBetween(String userId,
                                                                       Exercise exercise,
                                                              LocalDateTime from,
                                                              LocalDateTime to,
                                                              int limit) {

        Query query = query(where("sets").elemMatch(where("exercise").is(exercise))
                .and("date").lte(to).gte(from)
                .and("userId").is(userId))
                .with(new Sort(Sort.Direction.DESC, "date"));
        return template.find(query.limit(limit), TrainingSession.class);
    }
}
