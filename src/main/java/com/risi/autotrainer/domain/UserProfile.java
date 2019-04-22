package com.risi.autotrainer.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
public class UserProfile {

    @Id
    private String id;
    private String userId;
    private boolean isMale;
    private float weight;
    private float height;
    private short age;
    private Goal goal;
    private BodyPreference bodyPreference;
    private Set<Exercise> exercises;
}
