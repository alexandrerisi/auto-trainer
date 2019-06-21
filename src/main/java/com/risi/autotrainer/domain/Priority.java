package com.risi.autotrainer.domain;

import lombok.Getter;

@Getter
public enum Priority {
    VERY_HIGH(0),
    HIGH(1),
    MEDIUM(2),
    LOW(3),
    VERY_LOW(4);

    private int relevance;

    Priority(int relevance) {
        this.relevance = relevance;
    }
}
