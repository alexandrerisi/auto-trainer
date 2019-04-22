package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.*;
import com.risi.autotrainer.service.UserProfileService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class ProfileUI extends HorizontalLayout {

    private ComboBox<String> gender;
    private ComboBox<Goal> goal;
    private NumberField age;
    private NumberField weight;
    private NumberField height;
    private ComboBox<BodyPreference> bodyPreference;
    private Button saveButton;
    private UserProfileService userProfileService;
    private boolean isProfileLoaded = false;
    private UserProfile userProfile;
    private Grid<Exercise> exerciseGrid;
    private Set<Exercise> userExercises;

    ProfileUI(UserProfileService userProfileService) {

        this.userProfileService = userProfileService;

        var vl = new VerticalLayout();
        vl.setWidth("200px");
        add(vl);

        gender = new ComboBox<>("Gender", "Male", "Female");
        gender.setRequired(true);
        gender.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<String>,
                        String>>) event -> saveButtonActivation());
        vl.add(gender);

        age = new NumberField("Age");
        age.setValue(15d);
        age.setMin(15);
        age.setMax(250);
        age.setHasControls(true);
        age.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>>) event ->
                        saveButtonActivation());
        vl.add(age);

        weight = new NumberField("Weight");
        weight.setSuffixComponent(new Span("kg"));
        weight.setValue(35d);
        weight.setMin(35);
        weight.setMax(250);
        weight.setHasControls(true);
        weight.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>>) event ->
                        saveButtonActivation());
        vl.add(weight);

        height = new NumberField("Height");
        height.setSuffixComponent(new Span("cm"));
        height.setValue(100d);
        height.setMin(100);
        height.setMax(250);
        height.setHasControls(true);
        height.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<NumberField, Double>>) event ->
                        saveButtonActivation());
        vl.add(height);

        goal = new ComboBox<>("Goal", Goal.values());
        goal.setRequired(true);
        goal.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<Goal>,
                        Goal>>) event -> saveButtonActivation());
        vl.add(goal);

        bodyPreference = new ComboBox<>("Body Preference", BodyPreference.values());
        bodyPreference.setRequired(true);
        bodyPreference.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<BodyPreference>,
                        BodyPreference>>) event -> saveButtonActivation());
        vl.add(bodyPreference);

        var gridButtonsLayout = new HorizontalLayout();
        var addExerciseButton = new Button("New Exercise");
        var removeExerciseButton = new Button("Remove Selected");
        gridButtonsLayout.add(addExerciseButton, removeExerciseButton);
        vl.add(gridButtonsLayout);

        exerciseGrid = new Grid<>();
        exerciseGrid.addColumn(Exercise::getExerciseName).setHeader("Exercise Name").setWidth("150px");
        exerciseGrid.addColumn(Exercise::getMuscle).setHeader("Targeted Muscles").setWidth("550px");
        exerciseGrid.addColumn(Exercise::getPriority).setHeader("Priority").setWidth("150px");
        exerciseGrid.setWidth("850px");
        exerciseGrid.setHeightByRows(true);
        exerciseGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        vl.add(exerciseGrid);

        addExerciseButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            var dialog = new CreateExerciseDialog(exerciseGrid, userExercises, saveButton);
            add(dialog);
            dialog.open();
        });

        removeExerciseButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            Optional<Exercise> selectedExercise = exerciseGrid.getSelectedItems().stream().findFirst();
            if (selectedExercise.isPresent()) {
                userExercises.remove(selectedExercise.get());
                exerciseGrid.setItems(userExercises);
                saveButton.setEnabled(true);
            }
        });

        saveButton = new Button("Save");
        saveButton.setEnabled(false);
        saveButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if (userProfile == null)
                userProfile = new UserProfile();
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            userProfile.setUserId(user.getId());
            userProfile.setMale(gender.getValue().toLowerCase().equals("male"));
            userProfile.setWeight(weight.getValue().floatValue());
            userProfile.setHeight(height.getValue().floatValue());
            userProfile.setAge(age.getValue().shortValue());
            userProfile.setGoal(goal.getValue());
            userProfile.setBodyPreference(bodyPreference.getValue());
            userProfile.setExercises(userExercises);
            userProfileService.saveUserProfile(userProfile);
            saveButton.setEnabled(false);
        });
        vl.add(saveButton);
        saveButtonActivation();

        loadProfile();
    }

    private void saveButtonActivation() {

        if (isProfileLoaded && !saveButton.isEnabled()) {
            saveButton.setEnabled(true);
            return;
        }

        if (gender.getValue() != null && goal.getValue() != null && bodyPreference.getValue() != null)
            saveButton.setEnabled(true);
        else
            saveButton.setEnabled(false);
    }

    private void loadProfile() {

        var userProfile = userProfileService.getUserProfile();

        if (userProfile.isPresent()) {
            this.userProfile = userProfile.get();
            gender.setValue(this.userProfile.isMale() ? "Male" : "Female");
            goal.setValue(this.userProfile.getGoal());
            age.setValue((double) this.userProfile.getAge());
            weight.setValue((double) this.userProfile.getWeight());
            height.setValue((double) this.userProfile.getHeight());
            bodyPreference.setValue(this.userProfile.getBodyPreference());
            if (this.userProfile.getExercises() != null)
                userExercises = this.userProfile.getExercises();
            else
                userExercises = new HashSet<>();
            exerciseGrid.setItems(userExercises);
        }
        isProfileLoaded = true;
        saveButton.setEnabled(false);
    }
}
