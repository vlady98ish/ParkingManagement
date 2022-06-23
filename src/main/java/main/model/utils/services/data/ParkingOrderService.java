package main.model.utils.services.data;

import main.model.data.Car;
import main.model.data.ParkingOrder;
import main.model.data.ParkingSpace;
import main.model.utils.DataBaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ParkingOrderService extends AbstractService {
    private static final String FIND_ALL = "select * from parking_order";
    private static final String FIND_BY_ID = "select * from parking_order where id = ? limit 1";
    private static final String ADD_ORDER = "insert into parking_order (car_id, start_time, space_id) values (?, ?, ?)";
    private static final String DELETE_ORDER = "delete from parking_order where id = ?";
    private static final String FIND_BY_USER_ID = "select * from parking_order p left join car c on c.regno = p.car_id where c.user_id = ?";
    private static final String UPDATE_FINISH_TIME = "update parking_order set finish_time = ? where id = ?";

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy MM dd HH mm");
    private static final String FIND_ACTIVE_PARKING_BY_SPACE = "select * from parking_order where space_id = ? and finish_time is null limit 1";


    @Override
    public List<ParkingOrder> findAll() {
        List<ParkingOrder> orders = new ArrayList<>();
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(FIND_ALL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ParkingOrder po = getParkingOrder(rs);
                orders.add(po);
            }
        } catch (SQLException ignored) {}
        return orders;
    }

    private ParkingOrder getParkingOrder(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Car car = ServiceFactory.getService(Car.class).findById(rs.getString("car_id"));
        ParkingSpace space = ServiceFactory.getService(ParkingSpace.class).findById(rs.getInt("space_id"));
        LocalDateTime startTime = LocalDateTime.parse(rs.getString("start_time"), fmt);
        String finish_time = rs.getString("finish_time");
        LocalDateTime finishTime = finish_time == null ? null : LocalDateTime.parse(finish_time, fmt);
        ParkingOrder po;
        if (finishTime != null) {
            po = new ParkingOrder(id, car, space, startTime, finishTime);
        } else {
            po = new ParkingOrder(id, car, space, startTime);
        }
        return po;
    }

    @Override
    public ParkingOrder findById(Object id) {
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(FIND_BY_ID)) {
            int pid = (Integer)id;
            ps.setInt(1, pid);
            ResultSet rs = ps.executeQuery(FIND_BY_ID);
            if (rs.next()) {
                return getParkingOrder(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<ParkingOrder> findByFK(int id) {
        List<ParkingOrder> result = new ArrayList<>();
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(FIND_BY_USER_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ParkingOrder po = getParkingOrder(rs);
                result.add(po);
            }
        } catch (SQLException ignored) {}
        return result;
    }

    @Override
    public boolean persist(Object o) {
        ParkingOrder p = (ParkingOrder) o;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(ADD_ORDER)) {
            ps.setString(1, p.getCar().getRegno());
            ps.setString(2, p.getStartDateTime().format(fmt));
            ps.setInt(3, p.getSpace().getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void delete(Object o) {
        ParkingOrder p = (ParkingOrder) o;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(DELETE_ORDER)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Only finish time can be updated
     * @param o - record with updated data
     * @return true if success, or false if not
     */
    @Override
    public boolean update(Object o) {
        ParkingOrder p = (ParkingOrder) o;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(UPDATE_FINISH_TIME)) {
            ps.setString(1, p.getEndDateTime().format(fmt));
            ps.setInt(2, p.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean setFinishedBySpace(int spaceId) {
        Connection connection = DataBaseManager.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(FIND_ACTIVE_PARKING_BY_SPACE)) {
            ps.setInt(1, spaceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                try (PreparedStatement ps2 = connection.prepareStatement(UPDATE_FINISH_TIME)) {
                    ps2.setString(1, LocalDateTime.now().format(fmt));
                    ps2.setInt(2, rs.getInt("id"));
                    ps2.executeUpdate();
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
