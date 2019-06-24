package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.User;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "welcome", layout = Layout.class)
public class WelcomePage extends Div {

    public WelcomePage() {
        var user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        var layout = new HorizontalLayout();
        var welcomeMessage = "Hello " + user + "!";
        layout.add(new Span(), new Label(welcomeMessage));
        add(layout);
        var line2 = new HorizontalLayout();
        line2.add(new Span(), new Label("Welcome to Auto Trainer!"));
        add(line2);
    }
}
