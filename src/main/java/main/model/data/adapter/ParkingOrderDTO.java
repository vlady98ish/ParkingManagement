package main.model.data.adapter;

import lombok.Builder;
import lombok.Data;
import main.model.data.ParkingOrder;

import java.time.format.DateTimeFormatter;


@Data
@Builder
public class ParkingOrderDTO {
    private final static String FMT = "HH:mm dd-MMM-yyyy";

    public static ParkingOrderDTO from(ParkingOrder parkingOrder) {
        return ParkingOrderDTO.builder()
                .id(parkingOrder.getId())
                .car(parkingOrder.getCar().getRegno())
                .user(parkingOrder.getCar().getOwner().getName() + " (" + parkingOrder.getCar().getOwner().getLogin() +")")
                .space(parkingOrder.getSpace().getCode())
                .startTime(parkingOrder.getStartDateTime().format(DateTimeFormatter.ofPattern(FMT)))
                .endTime(parkingOrder.getEndDateTime() == null ? "" : parkingOrder.getStartDateTime().format(DateTimeFormatter.ofPattern(FMT)))
                .build();
    }

    private int id;
    private String car;
    private String space;
    private String startTime;
    private String endTime;
    private String user;
}
