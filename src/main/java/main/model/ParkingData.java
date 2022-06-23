package main.model;

import lombok.Getter;
import lombok.Setter;
import main.model.data.User;


public class ParkingData {
    private static ParkingData instance;

    private ParkingData() {}

    public static ParkingData getInstance() {
        if (instance == null) instance = new ParkingData();
        return instance;
    }

    @Getter
    @Setter
    private User user;
}
