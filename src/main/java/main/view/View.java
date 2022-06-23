package main.view;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import lombok.Data;

import main.model.data.Car;
import main.model.data.ParkingOrder;
import main.model.data.ParkingSpace;
import main.model.data.User;
import main.model.data.adapter.ParkingOrderDTO;
import main.model.ParkingData;
import main.model.utils.DataBaseManager;
import main.model.utils.services.data.AbstractService;
import main.model.utils.services.data.ParkingSpaceService;
import main.model.utils.services.data.ServiceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Data
public class View {

    private ListView<ParkingSpace> spaceListView;
    private ListView<Car> carListView;
    private ListView<ParkingSpace> mySpacesListView;
    private ListView<Car> registeredCarListView;
    private ListView<ParkingSpace> freeSpacesView;

    private TableView<User> userTableForAdmin;
    private TableView<ParkingOrderDTO> myOrdersTableView;
    private TableView<ParkingOrderDTO> allParkingTableView;

    private Label userLabel;

    private List<TextField> textFields = new ArrayList<>();

    public void update() {
        User user = ParkingData.getInstance().getUser();
        userLabel.setText("User: " + user.getName() + " (" + user.getLikes() +")");
        updateSpaceListView();
        updateUserTableForAdmin();
        updateCarListView();
        updateMySpacesListView();
        updateRegisteredCarsListView();
        updateFreeSpacesListView();
        updateMyParkingOrderTable();
        if (ParkingData.getInstance().getUser().isAdmin()) {
            updateAllParkingOrderTable();
        }
        clearAllTextFields();
    }

    private void updateMyParkingOrderTable() {
        User user = ParkingData.getInstance().getUser();
        AbstractService service = ServiceFactory.getService(ParkingOrder.class);
        List<ParkingOrder> list = service.findByFK(user.getId());
        myOrdersTableView.setItems(FXCollections.observableList(list.stream().map(ParkingOrderDTO::from).toList()));
    }

    private void updateAllParkingOrderTable() {
        List<ParkingOrderDTO> list = ServiceFactory.getService(ParkingOrder.class).findAll().stream().map(p -> ParkingOrderDTO.from((ParkingOrder) p)).toList();
        allParkingTableView.setItems(FXCollections.observableList(list));
    }

    private void updateFreeSpacesListView() {
        ParkingSpaceService service = (ParkingSpaceService) ServiceFactory.getService(ParkingSpace.class);
        freeSpacesView.setItems(FXCollections.observableList(service.findFree()));
    }

    private void updateRegisteredCarsListView() {
        int id = ParkingData.getInstance().getUser().getId();
        AbstractService carService = ServiceFactory.getService(Car.class);
        List<Car> list = carService.findByFK(id);
        ParkingSpaceService psService = (ParkingSpaceService) ServiceFactory.getService(ParkingSpace.class);
        Set<String> parkedCarsNumbers = psService.findAll().stream().map(ParkingSpace::getOccupied).collect(Collectors.toSet());

        registeredCarListView.setItems(FXCollections.observableList(list.stream().filter(car -> !parkedCarsNumbers.contains(car.getRegno())).toList()));
    }

    private void updateMySpacesListView() {
        int id = ParkingData.getInstance().getUser().getId();
        ParkingSpaceService service = (ParkingSpaceService) ServiceFactory.getService(ParkingSpace.class);
        mySpacesListView.setItems(FXCollections.observableList(Objects.requireNonNull(service).findByUserId(id)));
    }

    public void addTextFields(TextField... tf) {
        textFields.addAll(List.of(tf));
    }

    private void clearAllTextFields() {
        textFields.forEach(TextInputControl::clear);
    }

    private void updateUserTableForAdmin() {
        userTableForAdmin.setItems(FXCollections.observableList(Objects.requireNonNull(ServiceFactory.getService(User.class)).findAll()));
    }

    private void updateSpaceListView() {
        spaceListView.setItems(FXCollections.observableList(Objects.requireNonNull(ServiceFactory.getService(ParkingSpace.class)).findAll()));
    }

    public void updateCarListView() {
        User user = userTableForAdmin.getSelectionModel().getSelectedItem();
        if (user == null) {
            carListView.setItems(FXCollections.observableList(Objects.requireNonNull(ServiceFactory.getService(Car.class)).findAll()));
        } else {
            carListView.setItems(FXCollections.observableList(Objects.requireNonNull(ServiceFactory.getService(Car.class)).findByFK(user.getId())));
        }
    }
}
