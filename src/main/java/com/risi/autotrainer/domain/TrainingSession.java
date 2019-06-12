package com.risi.autotrainer.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class TrainingSession {

    @Id
    private String id;
    private List<ExerciseSet> sets = new ArrayList<>();
    private LocalDateTime date;
    private String userId;

    public void setDate(LocalDate date) {
        // this is meant to correct the problem cause by the database saving all dates in UTC.
        this.date = date.atStartOfDay().plusHours(1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingSession that = (TrainingSession) o;
        return id.equals(that.id);
    }

    public void removeExerciseSet(ExerciseSet set) {
        sets.remove(set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
