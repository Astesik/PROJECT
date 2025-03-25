package com.example.project.services;

import com.example.project.database.DatabaseConnection;
import com.example.project.models.User;
import com.example.project.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    // Rejestracja nowego użytkownika
    public void registerUser(String username, String email, String rawPassword) throws SQLException {
        // Gdy użytkownik się rejestruje, zastosuj PBKDF2 do hasła
        String hashedPassword = PasswordUtils.hashPassword(rawPassword);
        String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, 'USER')";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
        }
    }


    // Logowanie użytkownika
    public User loginUser(String username, String rawPassword) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");

                    // Weryfikuj hasło za pomocą PBKDF2
                    if (PasswordUtils.verifyPassword(rawPassword, storedPassword)) {
                        // Tworzenie użytkownika, jeśli hasło pasuje
                        return new User(
                                resultSet.getInt("id"),
                                resultSet.getString("username"),
                                resultSet.getString("email"),
                                resultSet.getString("password"),
                                resultSet.getString("role"),
                                resultSet.getBoolean("is_active")
                        );
                    }
                }
            }
        }

        return null; // Logowanie nieudane
    }
}