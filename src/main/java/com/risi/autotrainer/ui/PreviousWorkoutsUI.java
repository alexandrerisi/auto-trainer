package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.ExerciseSet;
import com.risi.autotrainer.domain.TrainingSession;
import com.risi.autotrainer.service.TrainingSessionService;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class PreviousWorkoutsUI extends VerticalLayout {

    private DatePicker to;
    private DatePicker from;
    private Button search;
    private VerticalLayout contentLayout = new VerticalLayout();
    private List<TrainingSession> trainingSessions;

    PreviousWorkoutsUI(TrainingSessionService trainingSessionService) {

        HorizontalLayout selectorLayout = new HorizontalLayout();
        add(selectorLayout);

        from = new DatePicker();
        from.setMin(LocalDate.of(2019, 2, 1));
        from.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>>) ev -> {
                    if (to.getValue() != null && !from.getValue().isBefore(to.getValue()))
                        to.setValue(null);
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
                    from.setMax(to.getValue().minusDays(1));
                    searchButtonActivation();
                });
        to.setLabel("To Day");

        search = new Button("Search");
        search.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            trainingSessions = trainingSessionService.getTrainingSessionByDate(from.getValue(), to.getValue());
            contentLayout.removeAll();
            if (trainingSessions != null) {
                loadTrainingSessions(trainingSessions);
            }
        });
        search.setEnabled(false);

        selectorLayout.add(from, to, search);
        add(selectorLayout);
        add(contentLayout);
    }

    private void searchButtonActivation() {
        if (from.getValue() != null && to.getValue() != null)
            search.setEnabled(true);
    }

    private void loadTrainingSessions(List<TrainingSession> sessions) {

        List<List<TrainingSession>> sessionsByDay = createSessionsListByDay(sessions);
        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;

        for (List<TrainingSession> daySession : sessionsByDay) {
            var grid = new Grid<>(ExerciseSet.class);
            contentLayout.add(new Label(daySession.get(0).getDate().format(dtf)), grid);

            var exerciseSets = new ArrayList<ExerciseSet>();
            for (TrainingSession ts : daySession)
                exerciseSets.addAll(ts.getSets());

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
