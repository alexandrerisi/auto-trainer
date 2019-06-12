package com.risi.autotrainer.ui;

import com.risi.autotrainer.service.TrainingSessionService;
import com.risi.autotrainer.service.UserProfileService;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

@PWA(name = "Auto Trainer", shortName="Trainer")
@Route("main")
@Theme(value = Material.class, variant = Material.DARK)
public class MainPage extends Div {

    public MainPage(UserProfileService profileService, TrainingSessionService trainingSessionService) {
        getLocale();
        var todayWorkout = new Tab("Suggested Workout for Today");
        var addWorkout = new Tab("Add Workout");
        var previousWorkouts = new Tab("Previous Workouts");
        var goals = new Tab("Profile");
        var tabs = new Tabs(todayWorkout, addWorkout, previousWorkouts, goals);
        add(tabs);

        var content = new VerticalLayout();
        add(content);

        tabs.addSelectedChangeListener((ComponentEventListener<Tabs.SelectedChangeEvent>) event -> {
            var selectedTab = event.getSource().getSelectedTab();
            if (selectedTab.equals(todayWorkout)) {
                // todo
                System.out.println("1");
            } else if (selectedTab.equals(addWorkout)) {
                content.removeAll();
                content.add(new AddWorkoutUI(trainingSessionService, profileService));
            } else if (selectedTab.equals(previousWorkouts)) {
                content.removeAll();
                content.add(new PreviousWorkoutsUI(trainingSessionService, profileService));
            } else {
                content.removeAll();
                content.add(new ProfileUI(profileService));
            }
        });
    }
}
