package com.risi.autotrainer.ui;

import com.risi.autotrainer.service.UserService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;

@Route("")
public class FirstPage extends HorizontalLayout {

    private UserService userService;
    private PasswordField passwordField;
    private PasswordField passwordConfirm;
    private Button saveUserButton;

    public FirstPage(UserService userService) {

        this.userService = userService;

        VerticalLayout span = new VerticalLayout();
        span.setWidth("40%");
        add(span);

        FormLayout formLayout = new FormLayout();
        setWidth("150px");
        Label label = new Label("Create Your User");
        formLayout.addFormItem(label, "");

        EmailField email = new EmailField();
        email.setRequiredIndicatorVisible(true);
        email.setPlaceholder("Your email");
        formLayout.addFormItem(email, "Email");

        passwordField = new PasswordField();
        passwordField.setRequired(true);
        passwordField.setPlaceholder("Your password");
        formLayout.addFormItem(passwordField, "Password");

        passwordConfirm = new PasswordField();
        passwordConfirm.setRequired(true);
        passwordConfirm.setPlaceholder("Your password");
        formLayout.addFormItem(passwordConfirm, "Retype Password");

        saveUserButton = new Button("Create");
        formLayout.addFormItem(saveUserButton, "");
        add(formLayout);

        Button forgotPassword = new Button("Forgot Password");
        formLayout.addFormItem(forgotPassword, "");
        add(formLayout);

        String mainUiLink = RouteConfiguration.forApplicationScope().getUrl(MainPage.class);
        Anchor link = new Anchor(mainUiLink, "Log In");
        formLayout.addFormItem(link, "");

        addPasswordListener(passwordField);
        addPasswordListener(passwordConfirm);
    }

    private void addPasswordListener(PasswordField field) {
        PasswordField checkAgainst = field.equals(passwordField) ? passwordConfirm : passwordField;
        field.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<PasswordField, String>>) ev -> {
            if (!checkAgainst.getValue().isEmpty() && !field.getValue().equals(checkAgainst.getValue())) {
                field.setInvalid(true);
                saveUserButton.setEnabled(false);
            } else {
                field.setInvalid(false);
                saveUserButton.setEnabled(true);
            }
        });
    }

}
