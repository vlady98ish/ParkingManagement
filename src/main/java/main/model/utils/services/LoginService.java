package main.model.utils.services;

import main.model.data.User;
import main.model.utils.DataBaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LoginService {
    private static final String FIND_USER_BY_CREDENTIALS = "select * from user where login=? and password=?";

    public Optional<User> login(String username, String password) {
        try {
            Connection connection = DataBaseManager.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(FIND_USER_BY_CREDENTIALS);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int likes = rs.getInt("likes");
                boolean admin = rs.getBoolean("admin");
                User user = new User(id, name, username, password, likes, admin);
                return Optional.of(user);
            } else return Optional.empty();
        } catch (SQLException e) {
            return Optional.empty();
        }

    }
}
