package main.model.utils.services.data;

import main.model.data.User;
import main.model.utils.DataBaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserService extends AbstractService {
    private static final String ADD_USER = "insert into user (name, login, password, admin) values (?, ?, ?, ?)";
    private static final String FIND_ALL = "select * from user";
    private static final String DELETE_USER = "delete from user where id = ?";
    private static final String FIND_BY_ID = "select * from user where id = ? limit 1";
    private static final String UPDATE_USER = "update user set name = ?, login = ?, password = ?, likes = ?, admin = ? where id = ?";

    @Override
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(FIND_ALL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String login = rs.getString("login");
                String password = rs.getString("password");
                int likes = rs.getInt("likes");
                boolean admin = rs.getBoolean("admin");
                result.add(new User(id,name,login,password,likes,admin));
            }
        } catch (SQLException ignored) {}
        return result;
    }

    public boolean addUserIfItPossible(String name, String login, String password, boolean admin) {
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(ADD_USER)) {
            ps.setString(1, name);
            ps.setString(2, login);
            ps.setString(3, password);
            ps.setBoolean(4, admin);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteUserIfItPossible(User u) {
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(DELETE_USER)) {
            ps.setInt(1, u.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public User findById(Object id) {
        int userId = (Integer)id;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String login = rs.getString("login");
                String password = rs.getString("password");
                int likes = rs.getInt("likes");
                boolean admin = rs.getBoolean("admin");
                return new User(userId, name, login, password, likes, admin);
            } else return null;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean persist(Object o) {
        User user = (User) o;
        return addUserIfItPossible(user.getName(), user.getLogin(), user.getPassword(), user.isAdmin());
    }

    @Override
    public void delete(Object o) {
        User user = (User) o;
        deleteUserIfItPossible(user);
    }

    @Override
    public boolean update(Object o) {
        User user = (User) o;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(UPDATE_USER)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getLikes());
            ps.setBoolean(5, user.isAdmin());
            ps.setInt(6, user.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
