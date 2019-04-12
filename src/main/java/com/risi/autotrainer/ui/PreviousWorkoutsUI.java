package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.Exercise;
import com.risi.autotrainer.domain.ExerciseSet;
import com.risi.autotrainer.domain.TrainingSession;
import com.risi.autotrainer.service.TrainingSessionService;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PreviousWorkoutsUI extends VerticalLayout {

    private DatePicker to;
    private DatePicker from;
    private Button searchByDate;
    private ComboBox<Exercise> comboBoxExercise;
    private VerticalLayout contentLayout = new VerticalLayout();
    private List<TrainingSession> trainingSessions;
    private TrainingSessionService trainingSessionService;
    private NumberField numberResults;

    PreviousWorkoutsUI(TrainingSessionService trainingSessionService) {
        this.trainingSessionService = trainingSessionService;
        HorizontalLayout dateSelectorLayout = new HorizontalLayout();
        add(dateSelectorLayout);

        from = new DatePicker();
        from.setMin(LocalDate.of(2019, 2, 1));
        from.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>>) ev -> {
                    if (to.getValue() != null && !from.getValue().isBefore(to.getValue()))
                        to.setValue(null);
                    if (to.getValue() != null)
                        to.setMin(from.getValue().plusDays(1));
                    searchButtonActivation();
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
                    searchButtonActivation();
                });
        to.setLabel("To Day");

        searchByDate = new Button("Search By Date");
        searchByDate.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            trainingSessions = trainingSessionService.getTrainingSessionByDate(
                    from.getValue(), to.getValue(), numberResults.getValue().intValue());
            contentLayout.removeAll();
            if (trainingSessions != null) {
                loadTrainingSessions(trainingSessions);
            }
        });
        searchByDate.setEnabled(false);

        dateSelectorLayout.add(from, to, searchByDate);

        var exerciseSelectorLayout = new HorizontalLayout();
        comboBoxExercise = new ComboBox<>("Exercise", Exercise.values());
        Button searchByExercise = new Button("Search By Exercise");
        searchByExercise.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            if (comboBoxExercise.getValue() != null) {
                contentLayout.removeAll();
                loadTrainingSessions(trainingSessionService.getByExercise(
                        comboBoxExercise.getValue(), numberResults.getValue().intValue()));
            }
        });
        exerciseSelectorLayout.add(comboBoxExercise, searchByExercise);

        numberResults = new NumberField("Number of training sessions showed.");
        numberResults.setStep(1);
        numberResults.setMax(20);
        numberResults.setMin(1);
        numberResults.setValue(5d);
        numberResults.setHasControls(true);

        add(dateSelectorLayout, exerciseSelectorLayout, numberResults, contentLayout);
    }

    private void searchButtonActivation() {
        if (from.getValue() != null && to.getValue() != null)
            searchByDate.setEnabled(true);
    }

    private void loadTrainingSessions(List<TrainingSession> sessions) {

        var sessionsByDay = createSessionsListByDay(sessions);
        var dtf = DateTimeFormatter.ISO_LOCAL_DATE;

        for (List<TrainingSession> daySession : sessionsByDay) {
            var layout = new VerticalLayout();
            var titleLayout = new HorizontalLayout();
            var grid = new Grid<>(ExerciseSet.class);
            var exerciseSets = new ArrayList<ExerciseSet>();
            var id = daySession.get(0).getDate().format(dtf);
            var dateInfo = id.split("-");
            var dateOfTraining = LocalDate.of(Integer.parseInt(dateInfo[0]),
                    Integer.parseInt(dateInfo[1]),
                    Integer.parseInt(dateInfo[2]));
            grid.addColumn(new NativeButtonRenderer<>("Edit", clickedItem -> {
                var dialog = new EditSetDialog(clickedItem,
                        trainingSessionService,
                        exerciseSets,
                        dateOfTraining,
                        grid,
                        false);
                contentLayout.add(dialog);
                dialog.open();
            }));
            grid.addColumn(new NativeButtonRenderer<>("Remove", clickedItem -> {
                exerciseSets.remove(clickedItem);
                trainingSessionService.updateTrainingSession(dateOfTraining, exerciseSets);
                grid.setItems(exerciseSets);
            }));
            layout.setId(id);

            var deleteButton = new Button("Delete");
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
                        trainingSessionService, exerciseSets, dateOfTraining, grid, true);
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
