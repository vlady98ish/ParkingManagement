package main.model.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DataBaseManager {
    private static DataBaseManager instance;
    private final Connection connection;

    private DataBaseManager() {
        try (BufferedReader br = new BufferedReader(new FileReader("parking.props"))) {
            Properties props = new Properties();
            props.load(br);
            String url = props.getProperty("url", "jdbc:mysql://localhost:3306/parking");
            connection = DriverManager.getConnection(url, props);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
