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
        var span = new VerticalLayout();
        span.setWidth("40%");
        add(span);

        var formLayout = new FormLayout();
        setWidth("150px");
        Label label = new Label("Change Your Password");
        formLayout.addFormItem(label,"");

        var email = new EmailField();
        email.setPlaceholder("Your username");
        formLayout.addFormItem(email, "Username");

        var passwordField = new PasswordField();
        passwordField.setPlaceholder("Old password");
        formLayout.addFormItem(passwordField, "Password");

        var passwordConfirm = new PasswordField();
        passwordConfirm.setPlaceholder("New password");
        formLayout.addFormItem(passwordConfirm, "Password");

        var saveUser = new Button("Save");
        formLayout.addFormItem(saveUser, "");
        add(formLayout);
    }
}
