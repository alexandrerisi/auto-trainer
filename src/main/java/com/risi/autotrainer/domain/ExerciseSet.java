package com.risi.autotrainer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseSet implements Comparable<ExerciseSet> {

    private short repetitions;
    private double weight;
    private Exercise exercise;

    @Override
    public int compareTo(ExerciseSet o) {
        return exercise.getExerciseName().compareTo(o.exercise.getExerciseName());
    }
}
