package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.Exercise;
import com.risi.autotrainer.domain.ExerciseSet;
import com.risi.autotrainer.domain.TrainingSession;
import com.risi.autotrainer.domain.UserProfile;
import com.risi.autotrainer.service.TrainingSessionService;
import com.risi.autotrainer.service.UserProfileService;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(value = "previous-workout", layout = Layout.class)
class PreviousWorkoutsUI extends VerticalLayout {

    private DatePicker to;
    private DatePicker from;
    private ComboBox<Exercise> cbExercise;
    private VerticalLayout contentLayout = new VerticalLayout();
    private List<TrainingSession> trainingSessions;
    private TrainingSessionService trainingSessionService;
    private UserProfile profile;
    private NumberField numberResults;

    PreviousWorkoutsUI(TrainingSessionService trainingSessionService, UserProfileService profileService) {
        var profile = profileService.getUserProfile().isPresent() ? profileService.getUserProfile().get() : null;
        this.trainingSessionService = trainingSessionService;
        this.profile = profile;
        HorizontalLayout dateSelectorLayout = new HorizontalLayout();
        add(dateSelectorLayout);

        from = new DatePicker();
        from.setMin(LocalDate.of(2019, 2, 1));
        from.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>>) ev -> {
                    if (to.getValue() != null && !from.getValue().isBefore(to.getValue()))
                        to.setValue(null);
                    if (to.getValue() != null)
                        to.setMin(from.getValue());
                });
        from.setLabel("From Day");

        to = new DatePicker();
        to.setMax(LocalDate.now());
        to.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>>) ev -> {
                    if (from.getValue() != null && !from.getValue().isBefore(to.getValue()))
                        from.setValue(null);
                    if (from.getValue() != null)
                        from.setMax(to.getValue().minusDays(1));
                });
        to.setLabel("To Day");

        Button searchButton = new Button("Search");
        var singleExerciseCheck = new Checkbox("Only this exercise.");
        searchButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if (cbExercise.getValue() != null && from.getValue() != null && to.getValue() != null)
                trainingSessions = trainingSessionService.getTrainingSessionByExerciseAndDate(cbExercise.getValue(),
                        from.getValue(), to.getValue(), numberResults.getValue().intValue(),
                        singleExerciseCheck.getValue());
            else if (cbExercise.getValue() != null && (from.getValue() == null || to.getValue() == null))
                trainingSessions = trainingSessionService.getByExercise(cbExercise.getValue(),
                        numberResults.getValue().intValue(), singleExerciseCheck.getValue());
            else if (cbExercise.getValue() == null && from.getValue() != null && to.getValue() != null)
                trainingSessions = trainingSessionService.getTrainingSessionByDate(
                        from.getValue(), to.getValue(), numberResults.getValue().intValue());
            else return;

            contentLayout.removeAll();
            if (trainingSessions != null)
                loadTrainingSessions(trainingSessions);
        });

        dateSelectorLayout.add(from, to);

        var exerciseSelectorLayout = new HorizontalLayout();
        if (profile != null) {
            var exercisesCollection = new ArrayList<>(profile.getExercises());
            Collections.sort(exercisesCollection);
            cbExercise = new ComboBox<>("Exercise", exercisesCollection);
        }
        exerciseSelectorLayout.setAlignItems(Alignment.CENTER);
        exerciseSelectorLayout.add(cbExercise, singleExerciseCheck);

        numberResults = new NumberField("Number of training sessions showed.");
        numberResults.setStep(1);
        numberResults.setMax(20);
        numberResults.setMin(1);
        numberResults.setValue(5d);
        numberResults.setHasControls(true);

        add(dateSelectorLayout, exerciseSelectorLayout, numberResults, searchButton, contentLayout);
    }

    private void loadTrainingSessions(List<TrainingSession> sessions) {

        var sessionsByDay = createSessionsListByDay(sessions);
        var dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (List<TrainingSession> daySession : sessionsByDay) {
            var layout = new VerticalLayout();
            var titleLayout = new HorizontalLayout();
            var exerciseSets = new ArrayList<ExerciseSet>();
            var grid = new Grid<>(ExerciseSet.class);
            var id = daySession.get(0).getDate().format(dtf);
            var dateInfo = id.split("/");
            var dateOfTraining = LocalDate.of(Integer.parseInt(dateInfo[2]),
                    Integer.parseInt(dateInfo[1]),
                    Integer.parseInt(dateInfo[0]));
            grid.addItemClickListener((ComponentEventListener<ItemClickEvent<ExerciseSet>>) event -> {
                var dialog = new EditSetDialog(event.getItem(),
                        trainingSessionService,
                        profile,
                        exerciseSets,
                        dateOfTraining,
                        grid,
                        false);
                contentLayout.add(dialog);
                dialog.open();
            });
            layout.setId(id);

            var deleteButton = new Button("Delete Session");
            deleteButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
                var componentToRemove = contentLayout.getChildren().filter(component ->
                        component.getId().isPresent() && component.getId().get().equals(id)).findFirst();
                componentToRemove.ifPresent(component -> {
                    trainingSessionService.deleteTrainingSession(dateOfTraining);
                    contentLayout.remove(component);
                });
            });

            var addSet = new Button("Add Set");
            addSet.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
                var dialog = new EditSetDialog(exerciseSets.get(exerciseSets.size() - 1),
                        trainingSessionService, profile, exerciseSets, dateOfTraining, grid, true);
                contentLayout.add(dialog);
                dialog.open();
            });

            titleLayout.add(new Label(id), deleteButton, addSet);
            layout.add(titleLayout, grid);
            contentLayout.add(layout);

            for (TrainingSession ts : daySession) {
                Collections.sort(ts.getSets());
                exerciseSets.addAll(ts.getSets());
            }
            grid.setItems(exerciseSets);
        }
    }

    private List<List<TrainingSession>> createSessionsListByDay(List<TrainingSession> sessions) {

        var returnList = new ArrayList<List<TrainingSession>>();
        LocalDateTime localDateTime = null;
        List<TrainingSession> sessionList = null;

        for (TrainingSession ts : sessions) {
            if (localDateTime == null || !localDateTime.isEqual(ts.getDate())) {
                sessionList = new ArrayList<>();
                returnList.add(sessionList);
                localDateTime = ts.getDate();
            }
            sessionList.add(ts);
        }
        return returnList;
    }
}
