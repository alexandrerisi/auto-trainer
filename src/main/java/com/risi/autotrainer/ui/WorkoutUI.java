package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.Exercise;
import com.risi.autotrainer.domain.ExerciseSet;
import com.risi.autotrainer.domain.TrainingSession;
import com.risi.autotrainer.domain.UserProfile;
import com.risi.autotrainer.service.TrainingSessionService;
import com.risi.autotrainer.service.UserProfileService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Hr;
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

@Route(value = "workout", layout = Layout.class)
class WorkoutUI extends VerticalLayout {

    private DatePicker to;
    private DatePicker from;
    private ComboBox<Exercise> cbExercise;
    private VerticalLayout contentLayout = new VerticalLayout();
    private List<TrainingSession> trainingSessions;
    private TrainingSessionService trainingSessionService;
    private UserProfile profile;
    private NumberField numberResults;

    WorkoutUI(TrainingSessionService trainingSessionService, UserProfileService profileService) {
        var profile = profileService.getUserProfile().isPresent() ? profileService.getUserProfile().get() : null;
        this.trainingSessionService = trainingSessionService;
        this.profile = profile;

        var addWorkoutLayout = new HorizontalLayout();
        var createWorkoutDatePicker = new DatePicker();
        createWorkoutDatePicker.setValue(LocalDate.now());
        createWorkoutDatePicker.setMax(LocalDate.now());
        var createWorkoutButton = new Button("Create Workout");
        addWorkoutLayout.add(createWorkoutDatePicker, createWorkoutButton);

        var dateSelectorLayout = new HorizontalLayout();
        var firstHr = new Hr();
        firstHr.setSizeFull();
        add(addWorkoutLayout, firstHr,dateSelectorLayout);

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

        createWorkoutButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            if (createWorkoutDatePicker.getValue() != null) {
                var session = trainingSessionService.getSingleTrainingSessionByDate(createWorkoutDatePicker.getValue());
                if (session.isEmpty()) {
                    var ts = new TrainingSession();
                    ts.setDate(createWorkoutDatePicker.getValue());
                    trainingSessionService.saveTrainingSession(ts);
                }
                cbExercise.setValue(null);
                from.setValue(createWorkoutDatePicker.getValue().minusDays(1));
                to.setValue(createWorkoutDatePicker.getValue());
                searchButton.click();
            }
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
                class DeleteConfirmation extends Dialog {
                    private Label label = new Label("Are you sure you want to delete the session?");
                    private Button no = new Button("No");
                    private Button yes = new Button("Yes");

                    private DeleteConfirmation() {
                        var vertical = new VerticalLayout();

                        yes.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent1 -> {
                            var componentToRemove = contentLayout.getChildren().filter(component ->
                                    component.getId().isPresent() && component.getId().get().equals(id)).findFirst();
                            componentToRemove.ifPresent(component -> {
                                trainingSessionService.deleteTrainingSession(dateOfTraining);
                                contentLayout.remove(component);
                            });
                            close();
                        });

                        no.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent12 -> close());

                        var horizontal = new HorizontalLayout();
                        horizontal.add(no, yes);
                        vertical.add(label, horizontal);
                        add(vertical);
                    }
                }
                var dialog = new DeleteConfirmation();
                UI.getCurrent().add(dialog);
                dialog.open();
            });

            var addSet = new Button("Add Set");
            addSet.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
                ExerciseSet set;
                if (exerciseSets.size() > 0)
                    set = exerciseSets.get(exerciseSets.size() - 1);
                else
                    set = new ExerciseSet();
                var dialog = new EditSetDialog(set, trainingSessionService, profile, exerciseSets, dateOfTraining,
                        grid, true);
                contentLayout.add(dialog);
                dialog.open();
            });

            titleLayout.add(new Label(id), deleteButton, addSet);

            var hr = new Hr();
            hr.setSizeFull();

            layout.add(titleLayout, grid, hr);
            contentLayout.add(layout);

            for (TrainingSession ts : daySession) {
                Collections.sort(ts.getSets());
                exerciseSets.addAll(ts.getSets());
            }
            grid.setItems(exerciseSets);
            grid.setSelectionMode(Grid.SelectionMode.SINGLE);
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


