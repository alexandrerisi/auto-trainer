package com.risi.autotrainer.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;

@Route("changePassword")
public class ChangePasswordPage extends HorizontalLayout {

    public ChangePasswordPage() {
        VerticalLayout span = new VerticalLayout();
        span.setWidth("40%");
        add(span);

        FormLayout formLayout = new FormLayout();
        setWidth("150px");
        Label label = new Label("Change Your Password");
        formLayout.addFormItem(label,"");

        EmailField email = new EmailField();
        email.setPlaceholder("Your username");
        formLayout.addFormItem(email, "Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPlaceholder("Old password");
        formLayout.addFormItem(passwordField, "Password");

        PasswordField passwordConfirm = new PasswordField();
        passwordConfirm.setPlaceholder("New password");
        formLayout.addFormItem(passwordConfirm, "Password");

        Button saveUser = new Button("Save");
        formLayout.addFormItem(saveUser, "");
        add(formLayout);
    }
}
