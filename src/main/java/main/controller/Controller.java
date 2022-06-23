package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import main.model.data.Car;
import main.model.data.ParkingOrder;
import main.model.data.ParkingSpace;
import main.model.data.User;
import main.model.data.adapter.ParkingOrderDTO;
import main.model.ParkingData;
import main.model.utils.services.LoginService;
import main.model.utils.services.data.*;
import main.model.utils.services.tabstrategy.StrategyFactory;
import main.view.View;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class Controller {

    @FXML
    private ListView<Car> registeredCarListView;
    @FXML
    private ListView<ParkingSpace> freeSpacesListView;
    @FXML
    private ListView<ParkingSpace> mySpacesListView;
    @FXML
    private TableView<ParkingOrderDTO> myOrdersTableView;
    @FXML
    private TableColumn<ParkingOrderDTO, String> carColumn;
    @FXML
    private TableColumn<ParkingOrderDTO, String> spaceColumn;
    @FXML
    private TableColumn<ParkingOrderDTO, String> startColumn;
    @FXML
    private TableColumn<ParkingOrderDTO, String> endColumn;
    @FXML
    private Label userLabel;
    @FXML
    private TextField markField;
    @FXML
    private TextField regNoField;
    @FXML
    private ListView<Car> carListView;
    @FXML
    private CheckBox adminCheckBox;
    @FXML
    private PasswordField passwordFieldForNewUser;
    @FXML
    private TextField nameFieldForNewUser;
    @FXML
    private TextField loginFieldForNewUser;
    @FXML
    private TableColumn<User, Integer> userIdColForAdmin;
    @FXML
    private TableColumn<User, String> userNameColForAdmin;
    @FXML
    private TableColumn<User, String> userLoginColForAdmin;
    @FXML
    private TableColumn<User, Integer> userLikesColForAdmin;
    @FXML
    private TableView<User> userTableForAdmin;
    @FXML
    private TextField newCodeField;
    @FXML
    private ListView<ParkingSpace> spacesListView;
    @FXML
    private TabPane tabPane;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField loginField;
    @FXML
    private Tab opTab;
    @FXML
    private Tab adminTab;
    @FXML
    private Tab viewAllParkingTab;

    @FXML
    private TableView<ParkingOrderDTO> allParkingTableView;
    @FXML
    private TableColumn<ParkingOrderDTO, String> allParkingUser;
    @FXML
    private TableColumn<ParkingOrderDTO, String> allParkingCar;
    @FXML
    private TableColumn<ParkingOrderDTO, String> allParkingSpace;
    @FXML
    private TableColumn<ParkingOrderDTO, String> allParkingStart;
    @FXML
    private TableColumn<ParkingOrderDTO, String> allParkingEnd;

    private User user;

    private View view;

    public void initialize() {
        opTab.setDisable(true);
        adminTab.setDisable(true);
        viewAllParkingTab.setDisable(true);
        view = new View();
        view.setUserLabel(userLabel);
        view.setSpaceListView(spacesListView);
        view.setUserTableForAdmin(userTableForAdmin);
        view.setCarListView(carListView);
        view.setMySpacesListView(mySpacesListView);
        view.setRegisteredCarListView(registeredCarListView);
        view.setFreeSpacesView(freeSpacesListView);
        userIdColForAdmin.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColForAdmin.setCellValueFactory(new PropertyValueFactory<>("name"));
        userLoginColForAdmin.setCellValueFactory(new PropertyValueFactory<>("login"));
        userLikesColForAdmin.setCellValueFactory(new PropertyValueFactory<>("likes"));

        view.setMyOrdersTableView(myOrdersTableView);
        carColumn.setCellValueFactory(new PropertyValueFactory<>("car"));
        spaceColumn.setCellValueFactory(new PropertyValueFactory<>("space"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        view.setAllParkingTableView(allParkingTableView);
        allParkingCar.setCellValueFactory(new PropertyValueFactory<>("car"));
        allParkingSpace.setCellValueFactory(new PropertyValueFactory<>("space"));
        allParkingUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        allParkingStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        allParkingEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        userTableForAdmin.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> view.updateCarListView());

        view.addTextFields(nameFieldForNewUser, loginFieldForNewUser, passwordFieldForNewUser, markField, regNoField);
    }

    public void start() {
        LoginService loginService = new LoginService();
        Optional<User> optionalUser = loginService.login(loginField.getText().trim(), passwordField.getText().trim());
        if (optionalUser.isEmpty()) return;
        user = optionalUser.get();
        ParkingData.getInstance().setUser(user);
        showTabs(StrategyFactory.getTabShowStrategy(user).tabNames());
    }

    private void showTabs(Set<String> tabNames) {
        List<Tab> tabsToClose = tabPane.getTabs().stream().filter(t -> !tabNames.contains(t.getId())).toList();
        for (Tab tab : tabsToClose) {
            tabPane.getTabs().remove(tab);
        }
        tabPane.getTabs().forEach(t -> t.setDisable(false));
        tabPane.getSelectionModel().select(0);
        StrategyFactory.getTabShowStrategy(user).fillTabs(view);
    }

    public void addNewCode() {
        String text = newCodeField.getText();
        ParkingSpaceService pss = (ParkingSpaceService) ServiceFactory.getService(ParkingSpace.class);
        pss.add(text);
        newCodeField.clear();
        view.update();
    }

    public void removeCode() {
        ParkingSpace parkingSpace = spacesListView.getSelectionModel().getSelectedItem();
        if (parkingSpace != null) {
            ParkingSpaceService pss = (ParkingSpaceService) ServiceFactory.getService(ParkingSpace.class);
            if (pss.removeIfItPossible(parkingSpace)) {
                view.update();
            }
        }
    }

    public void addUser() {
        String login = loginFieldForNewUser.getText();
        String name = nameFieldForNewUser.getText();
        String password = passwordFieldForNewUser.getText();
        boolean admin = adminCheckBox.isSelected();
        UserService us = (UserService) ServiceFactory.getService(User.class);
        if (us.addUserIfItPossible(name, login, password, admin)) {
            view.update();
        }
    }

    public void deleteUser() {
        User u = userTableForAdmin.getSelectionModel().getSelectedItem();
        if (u != null) {
            UserService us = (UserService) ServiceFactory.getService(User.class);
            if (us.deleteUserIfItPossible(u)) {
                view.update();
            }
        }
    }

    public void addNewCar() {
        User u = userTableForAdmin.getSelectionModel().getSelectedItem();
        if (u != null) {
            String regno = regNoField.getText();
            String mark = markField.getText();
            Car car = new Car(regno, mark, u);
            AbstractService service = ServiceFactory.getService(Car.class);
            service.persist(car);
            view.update();
        }
    }

    public void deleteSelectedCar() {
        Car selectedCar = carListView.getSelectionModel().getSelectedItem();
        if (selectedCar != null) {
            ServiceFactory.getService(Car.class).delete(selectedCar);
            view.update();
        }
    }

    public void clearUserSelection() {
        userTableForAdmin.getSelectionModel().clearSelection();
        view.update();
    }

    public void park() {
        Car car = registeredCarListView.getSelectionModel().getSelectedItem();
        if (car == null) return;
        ParkingSpace parkingSpace = freeSpacesListView.getSelectionModel().getSelectedItem();
        if (parkingSpace == null) return;
        doPark(car, parkingSpace);
    }

    private void doPark(Car car, ParkingSpace parkingSpace) {
        parkingSpace.setOccupied(car.getRegno());
        addParkingOrder(car, parkingSpace);
        view.update();
    }

    private void addParkingOrder(Car car, ParkingSpace parkingSpace) {
        ParkingOrder p = new ParkingOrder(0, car, parkingSpace, LocalDateTime.now());
        ServiceFactory.getService(ParkingOrder.class).persist(p);
        ServiceFactory.getService(ParkingSpace.class).update(parkingSpace);
    }

    public void leave() {
        ParkingSpace parkingSpace = mySpacesListView.getSelectionModel().getSelectedItem();
        if (parkingSpace == null) return;
        ParkingOrderService service = (ParkingOrderService) ServiceFactory.getService(ParkingOrder.class);
        if (service.setFinishedBySpace(parkingSpace.getId())) {
            parkingSpace.setOccupied("");
            ServiceFactory.getService(ParkingSpace.class).update(parkingSpace);
            user.setLikes(user.getLikes() + 1);
            ServiceFactory.getService(User.class).update(user);
            view.update();
        }
    }
}
