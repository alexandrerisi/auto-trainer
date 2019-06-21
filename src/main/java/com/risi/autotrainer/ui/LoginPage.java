package com.risi.autotrainer.ui;

import com.risi.autotrainer.domain.User;
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
public class LoginPage extends HorizontalLayout {

    private UserService userService;
    private EmailField email;
    private PasswordField passwordField;
    private PasswordField passwordConfirm;
    private Button saveUserButton;

    public LoginPage(UserService userService) {

        this.userService = userService;

        var span = new VerticalLayout();
        span.setWidth("40%");
        add(span);

        var formLayout = new FormLayout();
        setWidth("150px");
        Label label = new Label("Create Your User");
        formLayout.addFormItem(label, "");

        email = new EmailField();
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
        saveUserButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> createUser());
        formLayout.addFormItem(saveUserButton, "");
        add(formLayout);

        var forgotPassword = new Button("Forgot Password");
        forgotPassword.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            if (email.getValue() != null)
                userService.emailTemporaryPassword(new User(email.getValue(), ""));
        });
        formLayout.addFormItem(forgotPassword, "");
        add(formLayout);

        var mainUiLink = RouteConfiguration.forApplicationScope().getUrl(MainPage.class);
        var link = new Anchor(mainUiLink, "Log In");
        formLayout.addFormItem(link, "");

        enableSave(passwordField);
        enableSave(passwordConfirm);
    }

    private void createUser() {
        var user = new User(email.getValue(), passwordField.getValue());
        userService.saveUser(user);
        UI.getCurrent().getPage().executeJavaScript("window.location.replace('/main');");
    }

    private void enableSave(PasswordField field) {
        var checkAgainst = field.equals(passwordField) ? passwordConfirm : passwordField;
        field.addValueChangeListener(
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<PasswordField, String>>) ev -> {
            if (!checkAgainst.getValue().isEmpty() && !field.getValue().equals(checkAgainst.getValue())) {
                field.setInvalid(true);
                field.setErrorMessage("Passwords do not match!");
                saveUserButton.setEnabled(false);
            } else {
                field.setInvalid(false);
                field.setErrorMessage(null);
                saveUserButton.setEnabled(true);
            }
        });
    }

}
