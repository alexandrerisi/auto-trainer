package com.risi.autotrainer.service;

import com.risi.autotrainer.domain.*;
import com.risi.autotrainer.repository.TrainingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.risi.autotrainer.config.SecurityConfig.*;

@Service
public class TrainingSessionService {

    @Autowired
    private TrainingSessionRepository repository;

    public void saveTrainingSession(TrainingSession session) {
        session.setUserId(getAuthenticatedUser().getId());
        repository.save(session);
    }

    public List<TrainingSession> getTrainingSessionByDate(LocalDate from, LocalDate to, int limit) {
        return repository.findByDateBetween(getAuthenticatedUser().getId(),
                from.atStartOfDay(),
                to.atStartOfDay(),
                limit);
    }

    public Optional<TrainingSession> getSingleTrainingSessionByDate(LocalDate date) {
        return repository.findSingleTrainingSessionByDate(
                getAuthenticatedUser().getId(),
                date.atStartOfDay().plusHours(1));
    }

    public List<TrainingSession> getTrainingSessionByExerciseAndDate(Exercise exercise,
                                                                     LocalDate from,
                                                                     LocalDate to,
                                                                     int limit) {
        return repository.findByUserIdAndExerciseAndDateBetween(getAuthenticatedUser().getId(),
                exercise,
                from.atStartOfDay(),
                to.atStartOfDay(),
                limit);
    }

    public void deleteTrainingSession(LocalDate date) {
        repository.deleteByDate(date.atStartOfDay());
    }

    public List<TrainingSession> getByExercise(Exercise exercise, int limit) {
        return repository.findByExercise(exercise, limit);
    }

    public void updateTrainingSession(LocalDate from, List<ExerciseSet> sets) {
        var session = getSingleTrainingSessionByDate(from);
        if (session.isPresent()) {
            var ts = session.get();
            ts.setSets(sets);
            saveTrainingSession(ts);
        } else {
            // todo throw exception
        }
    }
}
