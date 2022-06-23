package main.model.utils.services.data;

import main.model.data.ParkingSpace;
import main.model.utils.DataBaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ParkingSpaceService extends AbstractService {

    private final String FIND_ALL_PLACES = "select * from parking_space";
    private final String FIND_FREE_PLACES = "select * from parking_space where occupied is null or LENGTH(occupied)=0";
    private final String ADD_CODE = "insert into parking_space (code) values (?)";
    private final String FIND_BY_ID = "select * from parking_space where id = ? limit 1";
    private final String SET_OCCUPIED = "update parking_space set occupied = ? where id = ?";
    private final String FIND_BY_USER_ID = "select * from parking_space p\n" +
            "    left join car c on occupied = c.regno\n" +
            "    left join user u on u.id = c.user_id\n" +
            "where LENGTH(occupied)>0 and u.id = ?";

    private List<ParkingSpace> findByQuery(String query, int...params) {
        List<ParkingSpace> result = new ArrayList<>();
        try {
            Connection connection = DataBaseManager.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            if (params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setInt(1+i, params[i]);
                }
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String code = rs.getString("code");
                String occupied = rs.getString("occupied");
                result.add(new ParkingSpace(id, code, occupied));
            }
        } catch (SQLException ignored) {}
        return result;
    }

    public List<ParkingSpace> findByUserId(int userId) {
        return findByQuery(FIND_BY_USER_ID, userId);
    }

    public List<ParkingSpace> findAll() {
        return findByQuery(FIND_ALL_PLACES);
    }

    public List<ParkingSpace> findFree() {
        return findByQuery(FIND_FREE_PLACES);
    }

    public boolean add(String code) {
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(ADD_CODE)) {
            ps.setString(1, code);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removeIfItPossible(ParkingSpace parkingSpace) {
        if (parkingSpace.getOccupied() == null || parkingSpace.getOccupied().isBlank()) {
            try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement("delete from parking_space where id = ?")) {
                ps.setInt(1, parkingSpace.getId());
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public ParkingSpace findById(Object id) {
        int pId = (Integer)id;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, pId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String code = rs.getString("code");
                String occupied = rs.getString("occupied");
                return new ParkingSpace(pId, code, occupied);
            } else return null;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean persist(Object o) {
        ParkingSpace ps = (ParkingSpace) o;
        return add(ps.getCode());
    }

    @Override
    public void delete(Object o) {
        ParkingSpace ps = (ParkingSpace) o;
        removeIfItPossible(ps);
    }

    @Override
    public boolean update(Object o) {
        ParkingSpace p = (ParkingSpace) o;
        try (PreparedStatement ps = DataBaseManager.getInstance().getConnection().prepareStatement(SET_OCCUPIED)) {
            ps.setString(1, p.getOccupied());
            ps.setInt(2, p.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
