package com.risi.autotrainer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseSet {

    private short repetitions;
    private float weight;
    private Exercise exercise;
}
