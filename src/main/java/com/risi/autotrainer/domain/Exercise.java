package com.risi.autotrainer.domain;

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Exercise implements Comparable<Exercise> {

    private String exerciseName;
    private Collection<Muscle> muscle;
    private Priority priority;
    private String userId;

    @Override
    public String toString() {
        return exerciseName;
    }

    @Override
    public int compareTo(Exercise exercise) {

        if (priority == exercise.priority)
            return 0;

        Priority[] array = Priority.values();
        int thisIndex = -1;
        for (int i = 0; i < array.length; i++)
            if (array[i] == priority)
                thisIndex = i;

        int exerciseIndex = -1;
        for (int i = 0; i < array.length; i++)
            if (array[i] == exercise.priority)
                exerciseIndex = i;

        if (thisIndex > exerciseIndex)
            return 1;

        return -1;
    }
}
