package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.ExerciseSet;
import com.risi.autotrainer.domain.TrainingSession;
import com.risi.autotrainer.service.TrainingSessionService;
import com.risi.autotrainer.service.UserProfileService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class AddWorkoutUI extends VerticalLayout {

    private List<ExerciseSet> exerciseSets;

    AddWorkoutUI(TrainingSessionService trainingSessionService, UserProfileService profileService) {
        var profile = profileService.getUserProfile().isPresent() ? profileService.getUserProfile().get() : null;
        var workoutDay = new DatePicker();
        workoutDay.setValue(LocalDate.now());
        workoutDay.setMax(LocalDate.now());
        var createWorkoutButton = new Button("Create Workout");
        var workoutGrid = new Grid<>(ExerciseSet.class);
        var addSetButton = new Button("Add Set");
        addSetButton.setEnabled(false);
        var commandLayout = new HorizontalLayout();
        commandLayout.add(workoutDay, createWorkoutButton);
        add(commandLayout, addSetButton, workoutGrid);
        workoutGrid.setVisible(false);

        workoutGrid.addColumn(new NativeButtonRenderer<>("Remove", clickedItem -> {
            exerciseSets.remove(clickedItem);
            trainingSessionService.updateTrainingSession(workoutDay.getValue(), exerciseSets);
            workoutGrid.setItems(exerciseSets);
        }));

        createWorkoutButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if (workoutDay.getValue() != null) {
                var session = trainingSessionService.getSingleTrainingSessionByDate(workoutDay.getValue());
                if (session.isPresent()) {
                    exerciseSets = session.get().getSets();
                    workoutGrid.setItems(exerciseSets);
                } else {
                    var ts = new TrainingSession();
                    ts.setDate(workoutDay.getValue());
                    trainingSessionService.saveTrainingSession(ts);
                    exerciseSets = new ArrayList<>();
                }
            }
            addSetButton.setEnabled(true);
            workoutGrid.setVisible(true);
        });

        addSetButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            var lastExerciseSet = exerciseSets.size() > 0 ? exerciseSets.get(exerciseSets.size() - 1) : null;
            var dialog = new EditSetDialog(lastExerciseSet,
                    trainingSessionService,
                    profile,
                    exerciseSets,
                    workoutDay.getValue(),
                    workoutGrid,
                    true);
            add(dialog);
            dialog.open();
        });
    }
}