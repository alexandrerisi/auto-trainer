package com.risi.autotrainer.service;

import com.risi.autotrainer.domain.Exercise;
import com.risi.autotrainer.domain.ExerciseSet;
import com.risi.autotrainer.domain.TrainingSession;
import com.risi.autotrainer.domain.User;
import com.risi.autotrainer.repository.TrainingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingSessionService {

    @Autowired
    private TrainingSessionRepository repository;

    public void saveTrainingSession(TrainingSession session) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        session.setUserId(user.getId());
        repository.save(session);
    }

    public List<TrainingSession> getTrainingSessionByDate(LocalDate from, LocalDate to, int limit) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return repository.findByDateBetween(user.getId(), from, to, limit);
    }

    public Optional<TrainingSession> getSingleTrainingSessionByDate(LocalDate date) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return repository.findSingleTrainingSessionByDate(user.getId(), date.atStartOfDay().plusHours(1));
    }

    public List<TrainingSession> getTrainingSessionByExerciseAndDate(Exercise exercise,
                                                                     LocalDate from,
                                                                     LocalDate to,
                                                                     int limit) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return repository.findByUserIdAndExerciseAndDateBetween(user.getId(), exercise, from, to, limit);
    }

    public void deleteTrainingSession(LocalDate from) {
        repository.deleteByDateBetween(from, from.plusDays(1));
    }

    public List<TrainingSession> getByExercise(Exercise exercise, int limit) {
        return repository.findByExercise(exercise, limit);
    }

    public void updateTrainingSession(LocalDate from, List<ExerciseSet> sets) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var to = from.plusDays(1);
        var session = repository.findSingleTrainingSessionByDate(user.getId(), from.atStartOfDay());
        if (session.isPresent()) {
            var ts = session.get();
            ts.setSets(sets);
            saveTrainingSession(ts);
        } else {
            // todo throw exception
        }
    }
}
