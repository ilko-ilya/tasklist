package com.example.tasklist.connection;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class TestConnection {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void testConnection() {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Database connection is successful!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            throw new RuntimeException();
        }
    }
}
