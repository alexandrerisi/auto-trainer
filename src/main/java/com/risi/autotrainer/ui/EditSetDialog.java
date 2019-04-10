package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.Exercise;
import com.risi.autotrainer.domain.ExerciseSet;
import com.risi.autotrainer.service.TrainingSessionService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
class EditSetDialog extends Dialog {

    private ExerciseSet exerciseSetToSave;
    private VerticalLayout vLayout;
    private ComboBox<Exercise> exercise;
    private NumberField repetitions;
    private NumberField weight;
    private NativeButton okButton;
    private NativeButton cancelButton;
    private TrainingSessionService service;
    private List<ExerciseSet> sets;
    private ExerciseSet selectedSet;
    private LocalDate date;
    private Grid<ExerciseSet> grid;

    EditSetDialog(ExerciseSet selectedSet, TrainingSessionService service, List<ExerciseSet> sets, LocalDate date,
                  Grid<ExerciseSet> grid) {

        this.grid = grid;
        this.selectedSet = selectedSet;
        this.service = service;
        this.sets = sets;
        this.date = date;
        add(new Label("Exercise Set"));
        vLayout = new VerticalLayout();
        exercise = new ComboBox<>("Exercise", Exercise.values());
        repetitions = new NumberField("Repetitions");
        repetitions.setHasControls(true);
        repetitions.setMin(1);
        weight = new NumberField("Weight");
        weight.setHasControls(true);
        weight.setMin(0);
        weight.setStep(0.1);
        vLayout.add(exercise, repetitions, weight);

        var hLayout = new HorizontalLayout();
        okButton = new NativeButton("Save");
        okButton.addClickListener(
                (ComponentEventListener<ClickEvent<NativeButton>>) nativeButtonClickEvent -> saveExerciseSet());
        cancelButton = new NativeButton("Cancel");
        cancelButton.addClickListener(
                (ComponentEventListener<ClickEvent<NativeButton>>) nativeButtonClickEvent -> close());
        hLayout.add(okButton, cancelButton);

        add(vLayout, hLayout);

        if (selectedSet != null)
            loadSet(selectedSet);
    }

    private void loadSet(ExerciseSet set) {
        exercise.setValue(set.getExercise());
        repetitions.setValue((double) set.getRepetitions());
        weight.setValue((double) set.getWeight());
    }

    private void saveExerciseSet() {
        exerciseSetToSave = new ExerciseSet();
        exerciseSetToSave.setExercise(exercise.getValue());
        exerciseSetToSave.setRepetitions(repetitions.getValue().shortValue());
        exerciseSetToSave.setWeight(weight.getValue().floatValue());
        sets.remove(selectedSet);
        sets.add(exerciseSetToSave);
        service.updateTrainingSession(date, sets);
        grid.setItems(sets);
        close();
    }
}
