package com.risi.autotrainer.domain;

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Exercise {

    private String exerciseName;
    private Collection<Muscle> muscle;
    private Priority priority;
    private String userId;

    @Override
    public String toString() {
        return exerciseName;
    }
}
