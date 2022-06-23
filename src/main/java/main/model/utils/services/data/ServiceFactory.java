package main.model.utils.services.data;

import main.model.data.Car;
import main.model.data.ParkingOrder;
import main.model.data.ParkingSpace;
import main.model.data.User;

import java.util.HashMap;
import java.util.Map;


public class ServiceFactory {

    private final static Map<Class<?>, AbstractService> serviceCash = new HashMap<>();

    public static AbstractService getService(Class<?> clazz) {
        if (serviceCash.containsKey(clazz)) return serviceCash.get(clazz);
        else {
            AbstractService as = get(clazz);
            if (as != null) serviceCash.put(clazz, as);
            return as;
        }
    }

    private static AbstractService get(Class<?> clazz) {
        if (clazz == ParkingSpace.class) {
            return new ParkingSpaceService();
        } else if (clazz == User.class) {
            return new UserService();
        } else if (clazz == Car.class) {
            return new CarService();
        } else if (clazz == ParkingOrder.class) {
            return new ParkingOrderService();
        }
        return null;
    }
}
