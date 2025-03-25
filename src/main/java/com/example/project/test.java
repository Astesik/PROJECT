package com.example.project;

import com.example.project.models.User;
import com.example.project.services.UserService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class test extends Application {

    private final UserService userService = new UserService();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rejestracja i Logowanie - JavaFX");

        // Zakładki (Rejestracja i Logowanie)
        TabPane tabPane = new TabPane();

        Tab registerTab = new Tab("Rejestracja");
        registerTab.setContent(createRegistrationForm());
        registerTab.setClosable(false);

        Tab loginTab = new Tab("Logowanie");
        loginTab.setContent(createLoginForm());
        loginTab.setClosable(false);

        tabPane.getTabs().addAll(registerTab, loginTab);

        // Scena i konfiguracja okna
        Scene scene = new Scene(tabPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createRegistrationForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Pola formularza
        Label usernameLabel = new Label("Nazwa użytkownika:");
        TextField usernameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Hasło:");
        PasswordField passwordField = new PasswordField();
        Button registerButton = new Button("Zarejestruj");
        Label resultLabel = new Label();

        // Obsługa przycisku rejestracji
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                resultLabel.setText("Wszystkie pola muszą być wypełnione!");
                return;
            }

            try {
                userService.registerUser(username, email, password);
                resultLabel.setText("Rejestracja zakończona sukcesem.");
            } catch (Exception ex) {
                resultLabel.setText("Błąd: " + ex.getMessage());
            }
        });

        // Układ
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(registerButton, 1, 3);
        grid.add(resultLabel, 1, 4);

        return grid;
    }

    private GridPane createLoginForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Pola formularza
        Label usernameLabel = new Label("Nazwa użytkownika:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Hasło:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Zaloguj");
        Label resultLabel = new Label();

        // Obsługa przycisku logowania
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                resultLabel.setText("Wszystkie pola muszą być wypełnione!");
                return;
            }

            try {
                User user = userService.loginUser(username, password);
                if (user != null) {
                    resultLabel.setText("Zalogowano użytkownika: " + user.getUsername());
                } else {
                    resultLabel.setText("Logowanie nieudane. Sprawdź dane.");
                }
            } catch (Exception ex) {
                resultLabel.setText("Błąd: " + ex.getMessage());
            }
        });

        // Układ
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(resultLabel, 1, 3);

        return grid;
    }

    public static void main(String[] args) {
        launch(args);
    }
}