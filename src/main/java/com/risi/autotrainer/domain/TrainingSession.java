package com.risi.autotrainer.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TrainingSession {

    @Id
    private String id;
    private List<ExerciseSet> sets = new ArrayList<>();
    private LocalDateTime date;
    private String userId;
}
