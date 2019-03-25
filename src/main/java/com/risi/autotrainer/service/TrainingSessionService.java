package com.risi.autotrainer.service;

import com.risi.autotrainer.domain.TrainingSession;
import com.risi.autotrainer.repository.TrainingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrainingSessionService {

    @Autowired
    private TrainingSessionRepository repository;

    public void saveTrainingSession(TrainingSession session) {
        repository.save(session);
    }

    /*public TrainingSession generateTraining() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)authentication.getPrincipal();
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setDate(LocalDateTime.now());
        trainingSession.setUserId(user.getId());

        List<ExerciseSet> session = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            session.add(new ExerciseSet((short) 5, 90f,Exercise.SQUAT));
        trainingSession.setSets(session);
        return trainingSession;
    }*/

    public List<TrainingSession> getTrainingSessionByDate(LocalDate from, LocalDate to) {
        return repository.findByDateBetween(from.atStartOfDay(), to.atStartOfDay());
    }
}
