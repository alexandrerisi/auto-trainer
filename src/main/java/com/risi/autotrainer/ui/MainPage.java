package com.risi.autotrainer.ui;

import com.risi.autotrainer.service.TrainingSessionService;
import com.risi.autotrainer.service.UserProfileService;
import com.vaadin.flow.component.ComponentEventListener;
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
public class MainPage extends VerticalLayout {

    public MainPage(UserProfileService userProfileService, TrainingSessionService trainingSessionService) {

        //setWidth("50%");
        //User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //add("Hi " + user.getUsername() + " welcome to the Auto Trainer!");

        Tab todayWorkout = new Tab("Suggested Workout for Today");
        Tab previousWorkouts = new Tab("Previous Workouts");
        Tab goals = new Tab("Profile");
        Tabs tabs = new Tabs(todayWorkout, previousWorkouts, goals);
        add(tabs);

        VerticalLayout content = new VerticalLayout();
        add(content);

        tabs.addSelectedChangeListener((ComponentEventListener<Tabs.SelectedChangeEvent>) event -> {
            Tab selectedTab = event.getSource().getSelectedTab();
            if (selectedTab.equals(todayWorkout)) {
                // todo
                System.out.println("1");
            } else if (selectedTab.equals(previousWorkouts)) {
                content.removeAll();
                content.add(new PreviousWorkoutsUI(trainingSessionService));
            } else {
                content.removeAll();
                content.add(new ProfileUI(userProfileService));
            }
        });
    }
}
