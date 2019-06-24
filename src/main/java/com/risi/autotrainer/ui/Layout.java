package com.risi.autotrainer.ui;

import com.vaadin.flow.component.applayout.AbstractAppRouterLayout;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes")
@PWA(name = "Auto Trainer", shortName="Trainer")
@Theme(value = Material.class, variant = Material.DARK)
public class Layout extends AbstractAppRouterLayout {


    @Override
    protected void configure(AppLayout appLayout, AppLayoutMenu appLayoutMenu) {
        appLayout.setBranding(new Span("Auto Trainer"));
        AppLayoutMenu menu = appLayout.createMenu();
        Image img = new Image("frontend/barbell.png", "Auto Trainer");
        img.setHeight("44px");
        appLayout.setBranding(img);

        var todayWorkout = new AppLayoutMenuItem("Suggested Workout for Today");
        var addWorkout = new AppLayoutMenuItem("Add Workout", "add-workout");
        var previousWorkouts = new AppLayoutMenuItem("Previous Workouts", "previous-workout");
        var profile = new AppLayoutMenuItem("Profile", "profile");
        menu.addMenuItems(todayWorkout,
                addWorkout,
                previousWorkouts,
                profile);
    }
}
