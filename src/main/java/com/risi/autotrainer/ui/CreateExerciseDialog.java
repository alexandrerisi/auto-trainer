package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.Exercise;
import com.risi.autotrainer.domain.Muscle;
import com.risi.autotrainer.domain.Priority;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class CreateExerciseDialog extends Dialog {

    private List<Muscle> targetedMuscles = new ArrayList<>();

    CreateExerciseDialog(Grid<Exercise> exerciseGrid, Set<Exercise> userExercises, Button saveProfileButton) {

        var vLayout = new VerticalLayout();
        var exerciseName = new TextField("Exercise Name");

        var priorityComboBox = new ComboBox<>("Relevance of the exercise.", Priority.values());
        priorityComboBox.setWidth("280px");

        var muscleComboBox = new ComboBox<>("Targeted Muscle, add in relevance order.", Muscle.values());
        muscleComboBox.setWidth("330px");

        var muscleListBox = new ListBox<Muscle>();
        var addMuscleButton = new Button("Add Targeted Muscle");
        addMuscleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if (muscleComboBox.getValue() != null && !targetedMuscles.contains(muscleComboBox.getValue())) {
                targetedMuscles.add(muscleComboBox.getValue());
                muscleListBox.setItems(targetedMuscles);
            }
        });

        var removeSelectedMuscleButton = new Button("Remove Targeted Muscle");
        removeSelectedMuscleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if (muscleListBox.getValue() != null) {
                targetedMuscles.remove(muscleListBox.getValue());
                muscleListBox.setItems(targetedMuscles);
            }
        });

        var saveButton = new Button("Save");
        saveButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if (!exerciseName.getValue().isBlank()
                    && targetedMuscles != null
                    && targetedMuscles.size() > 0
                    && targetedMuscles.get(0) != null
                    && priorityComboBox.getValue() != null) {
                var exercise = new Exercise(exerciseName.getValue(),
                        targetedMuscles,
                        priorityComboBox.getValue(),
                        null);
                userExercises.add(exercise);
                exerciseGrid.setItems(userExercises);
                saveProfileButton.setEnabled(true);
                close();
            }

        });

        vLayout.add(exerciseName,
                priorityComboBox,
                muscleComboBox,
                addMuscleButton,
                removeSelectedMuscleButton,
                muscleListBox,
                saveButton);
        add(vLayout);
    }
}
