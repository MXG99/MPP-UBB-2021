package contests.client.gui;

import contests.model.Organizer;
import contests.model.OrganizerCredentials;
import contests.services.ContestsException;
import contests.services.IContestsServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class LoginController {

    // Stage
    private double yOffset = 0;
    private double xOffset = 0;

    // Primary stage
    private Stage primaryStage;

    // Main view variables
    private IContestsServices server;
    private MainController mainCtrl;
    private Parent mainParent;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    void onExitClicked() {
        Platform.exit();
    }


    @FXML
    void onLoginMouseClicked(MouseEvent event) {

        // Extract login credentials
        String email = emailTextField.getText();
        String password = passwordTextField.getText();

        // Helper entity to facilitate login
        OrganizerCredentials organizerCredentials = new OrganizerCredentials(email, password);

        Platform.runLater(() -> {
            try {
                Organizer organizer = server.login(organizerCredentials, mainCtrl);
                if (organizer != null) {
                    Stage stage = initializeMainStage(organizer);
                    stage.show();
                    ((Node) event.getSource()).getScene().getWindow().hide();
                    stage.setOnCloseRequest(this::logOut);
                } else {
                    organizerAlreadyLoggedIn();
                }
            } catch (ContestsException e) {
                e.printStackTrace();
            }
        });
    }

    private Stage initializeMainStage(Organizer organizer) throws ContestsException {
        mainCtrl.init(organizer);
        Stage stage = new Stage();
        stage.setScene(new Scene(mainParent));
        makeDraggable(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        return stage;
    }

    private void organizerAlreadyLoggedIn() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Organizer already logged in");
        alert.show();
    }

    private void makeDraggable(Stage stage) {
        mainParent.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        mainParent.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - xOffset);
            stage.setY(mouseEvent.getScreenY() - yOffset);
        });
    }

    public void setServer(IContestsServices server) {
        this.server = server;
    }

    public void setMainController(MainController mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void setParent(Parent mainRoot) {
        this.mainParent = mainRoot;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void logOut(WindowEvent windowEvent) {
        mainCtrl.logout();
        System.exit(0);
    }
}
