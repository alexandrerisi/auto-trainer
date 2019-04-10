package com.risi.autotrainer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseSet implements Comparable<ExerciseSet> {

    private short repetitions;
    private float weight;
    private Exercise exercise;

    @Override
    public int compareTo(ExerciseSet o) {
        return exercise.name().compareTo(o.exercise.name());
    }
}
