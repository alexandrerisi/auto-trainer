package com.risi.autotrainer.service;

import com.risi.autotrainer.domain.TrainingSession;
import com.risi.autotrainer.repository.TrainingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TrainingSessionService {

    @Autowired
    private TrainingSessionRepository repository;

    public void saveTrainingSession(TrainingSession session) {
        repository.save(session);
    }

    public List<TrainingSession> getTrainingSessionByDate(LocalDate from, LocalDate to) {
        return repository.findByDateBetween(from.atStartOfDay(), to.atStartOfDay());
    }

    public void deleteTrainingSession(LocalDate from) {
        var to = from.plusDays(1).atStartOfDay();
        repository.deleteByDateBetween(from.atStartOfDay(), to.minus(1, ChronoUnit.MINUTES));
    }
}
