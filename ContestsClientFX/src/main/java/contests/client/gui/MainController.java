package contests.client.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import contests.model.Activity;
import contests.model.Category;
import contests.model.Organizer;
import contests.model.Participant;
import contests.model.dtos.ParticipantDTO;
import contests.network.dto.DTOUtils;
import contests.services.ContestsException;
import contests.services.IContestsObserver;
import contests.services.IContestsServices;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.io.Serial;
import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MainController extends UnicastRemoteObject implements IContestsObserver, Serializable {

    private IContestsServices server;
    private Organizer organizer;
    private double xOffset = 0;
    private double yOffset = 0;

    ObservableList<Participant> participants = FXCollections.observableArrayList();

    @FXML
    private Label usernameLabel;

    @FXML
    private Button btnParticipants;

    @FXML
    private Button btnRegistrations;

    @FXML
    private Button btnExit;

    @FXML
    private Pane pnRegistrations;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField ageTextField;

    @FXML
    private ComboBox<Activity> firstActivityCB;

    @FXML
    private ComboBox<Activity> secondActivityCB;

    @FXML
    private Pane pnParticipants;

    @FXML
    private TableView<ParticipantDTO> participantsTableView;

    @FXML
    private TableColumn<ParticipantDTO, String> participantID;

    @FXML
    private TableColumn<ParticipantDTO, String> participantFN;

    @FXML
    private TableColumn<ParticipantDTO, String> participantLN;

    @FXML
    private TableColumn<ParticipantDTO, String> participantFA;

    @FXML
    private TableColumn<ParticipantDTO, String> participantSA;

    @FXML
    private TableColumn<ParticipantDTO, String> participantCategory;

    @FXML
    private ComboBox<Activity> activityCB;

    @FXML
    private ComboBox<Category> categoryCB;


    Callback<ListView<Activity>, ListCell<Activity>> factoryActivity = lv -> new ListCell<Activity>() {
        @Override
        protected void updateItem(Activity item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? "" : item.getName());
        }
    };

    Callback<ListView<Category>, ListCell<Category>> factoryCategory = lv -> new ListCell<Category>() {
        @Override
        protected void updateItem(Category item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? "" : item.getLowerBound().toString() + "-" + item.getUpperBound().toString() + " Years");
        }
    };

    public MainController() throws RemoteException {
    }


    @FXML
    void handleClicks(ActionEvent event) throws ContestsException {
        if (event.getSource() == btnParticipants) {
            pnParticipants.toFront();
            handleSelection(event);
        }
        if (event.getSource() == btnRegistrations) {
            pnRegistrations.toFront();
        }
        if (event.getSource() == btnExit) {
            Platform.exit();
        }
    }

    @FXML
    void handleSelection(ActionEvent event) throws ContestsException {
        Predicate<ParticipantDTO> activityPredicate = participantDTO -> participantDTO.getFirstActivity()
                .equals(activityCB.getSelectionModel().getSelectedItem().getName()) ||
                participantDTO.getSecondActivity().equals(activityCB.getSelectionModel().getSelectedItem().getName());
        Predicate<ParticipantDTO> categoryPredicate = participantDTO -> participantDTO.getCategory()
                .equals(categoryCB.getSelectionModel().getSelectedItem().toString());
        if (categoryCB.getSelectionModel().getSelectedItem() != null &&
                activityCB.getSelectionModel().getSelectedItem() != null) {
            List<ParticipantDTO> participantDTOS = server.getParticipants().stream()
                    .filter(activityPredicate.and(categoryPredicate))
                    .collect(Collectors.toList());
            participantsTableView.getItems().setAll(participantDTOS);
        }
    }

    @FXML
    void onRegisterMouseClicked(MouseEvent event) {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        Integer age = Integer.valueOf(ageTextField.getText());
        Long firstActivityId = firstActivityCB.getSelectionModel().getSelectedItem().getId();
        Long secondActivityId = secondActivityCB.getSelectionModel().getSelectedItem().getId();
        if (!firstActivityId.equals(secondActivityId)) {
            try {
                Participant participant = new Participant(firstName, lastName, firstActivityId, secondActivityId, age);
                server.addParticipant(participant);
            } catch (ContestsException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Activities cannot be the same");
            alert.show();
        }
    }


    public void setServer(IContestsServices server) {
        this.server = server;
    }

    @Override
    public void participantAdded(ParticipantDTO participant) throws ContestsException {
        participantsTableView.getItems().add(participant);
    }

    @FXML
    public void initialize() {
        pnParticipants.toFront();
        activityCB.setCellFactory(factoryActivity);
        activityCB.setButtonCell(factoryActivity.call(null));

        categoryCB.setCellFactory(factoryCategory);
        categoryCB.setButtonCell(factoryCategory.call(null));

        firstActivityCB.setCellFactory(factoryActivity);
        firstActivityCB.setButtonCell(factoryActivity.call(null));

        secondActivityCB.setCellFactory(factoryActivity);
        secondActivityCB.setButtonCell(factoryActivity.call(null));

        participantID.setCellValueFactory(new PropertyValueFactory<ParticipantDTO, String>("id"));
        participantFN.setCellValueFactory(new PropertyValueFactory<ParticipantDTO, String>("firstName"));
        participantLN.setCellValueFactory(new PropertyValueFactory<ParticipantDTO, String>("lastName"));
        participantFA.setCellValueFactory(new PropertyValueFactory<ParticipantDTO, String>("firstActivity"));
        participantSA.setCellValueFactory(new PropertyValueFactory<ParticipantDTO, String>("secondActivity"));
        participantCategory.setCellValueFactory(new PropertyValueFactory<ParticipantDTO, String>("category"));
    }

    public void logout() {
        try {
            server.logOut(organizer, this);

        } catch (ContestsException e) {
            System.out.println("Logout error " + e);
        }
    }

    public void init(Organizer organizer) throws ContestsException {
        //Logged Organizer
        this.organizer = organizer;

        //Initializing
        initialize();

        //Setting username label
        usernameLabel.setText(organizer.getFirstName() + " " + organizer.getLastName());

        //Populating comboboxes
        activityCB.getItems().addAll(server.getAllActivities());
        categoryCB.getItems().addAll(server.getAllCategories());
        firstActivityCB.getItems().addAll(server.getAllActivities());
        secondActivityCB.getItems().addAll(server.getAllActivities());
    }

}
