package main.model.utils.services.data;

import main.model.data.Car;
import main.model.data.User;
import main.model.utils.DataBaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class CarService extends AbstractService {
    private static final String FIND_ALL = "select * from car";
    private static final String FIND_BY_ID = "select * from car where regno = ? limit 1";
    private static final String ADD_CAR = "insert into car values (?, ?, ?)";
    private static final String DELETE_CAR = "delete from car where regno = ?";
    private static final String UPDATE_CAR_USER = "update car set user_id = ? where regno = ?";

    @Override
    public  List<Car> findAll() {
        List<Car> result = new ArrayList<>();
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(FIND_ALL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int user_id = rs.getInt("user_id");
                User user = Objects.requireNonNull(ServiceFactory.getService(User.class)).findById(user_id);
                int index = users.indexOf(user);
                Car car;
                if (index == -1) {
                    users.add(user);
                    car = new Car(rs.getString("regno"), rs.getString("mark"), user);
                } else {
                    car = new Car(rs.getString("regno"), rs.getString("mark"), users.get(index));
                }
                result.add(car);
            }
        } catch (SQLException ignored) {}
        return result;
    }

    @Override
    public Car findById(Object id) {
        String regno = String.valueOf(id);
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(FIND_BY_ID)) {
            ps.setString(1, regno);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String mark = rs.getString("mark");
                User owner = Objects.requireNonNull(ServiceFactory.getService(User.class)).findById(rs.getInt("user_id"));
                return new Car(regno, mark, owner);
            } else return null;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<Car> findByFK(int id) {
        List<Car> result = new ArrayList<>();
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(FIND_ALL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int user_id = rs.getInt("user_id");
                if (user_id!=id) continue;
                User user = Objects.requireNonNull(ServiceFactory.getService(User.class)).findById(user_id);
                int index = users.indexOf(user);
                Car car;
                if (index == -1) {
                    users.add(user);
                    car = new Car(rs.getString("regno"), rs.getString("mark"), user);
                } else {
                    car = new Car(rs.getString("regno"), rs.getString("mark"), users.get(index));
                }
                result.add(car);
            }
        } catch (SQLException ignored) {}
        return result;
    }

    @Override
    public boolean persist(Object o) {
        Car c = (Car) o;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(ADD_CAR)) {
            ps.setString(1, c.getRegno());
            ps.setString(2, c.getMark());
            ps.setInt(3, c.getOwner().getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void delete(Object o) {
        Car c = (Car) o;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(DELETE_CAR)) {
            ps.setString(1, c.getRegno());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Object o) {
        Car c = (Car) o;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(UPDATE_CAR_USER)) {
            ps.setInt(1, c.getOwner().getId());
            ps.setString(2, c.getRegno());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
