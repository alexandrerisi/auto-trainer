package com.risi.autotrainer.ui;

import com.risi.autotrainer.service.TrainingSessionService;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.time.LocalDate;

class PreviousWorkoutsUI extends HorizontalLayout {

    private DatePicker to;
    private DatePicker from;
    private Button search;

    PreviousWorkoutsUI(TrainingSessionService trainingSessionService) {

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
        search.addClickListener((ComponentEventListener<ClickEvent<Button>>) event ->
                trainingSessionService.getTrainingSessionByDate(from.getValue(), to.getValue()));
        search.setEnabled(false);

        add(from, to, search);
    }

    private void searchButtonActivation() {
        if (from.getValue() != null && to.getValue() != null)
            search.setEnabled(true);
    }
}
