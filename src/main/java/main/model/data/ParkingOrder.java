package main.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ParkingOrder {
    public ParkingOrder(int id, Car car, ParkingSpace space, LocalDateTime startDateTime) {
        this.id = id;
        this.car = car;
        this.space = space;
        this.startDateTime = startDateTime;
    }

    private int id;
    private Car car;
    private ParkingSpace space;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
