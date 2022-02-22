package contests.client;


import contests.client.gui.LoginController;
import contests.client.gui.MainController;
import contests.services.IContestsServices;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartRPCClientFX extends Application {
    // Dragging variables
    private double xOffset = 0;
    private double yOffset = 0;


    private static final int defaultContestsPort = 55555;
    private static final String defaultServer = "localhost";


    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-client.xml");

        IContestsServices server = (IContestsServices) factory.getBean("contestsService");

        FXMLLoader loginLoader = new FXMLLoader(getClass().getClassLoader().getResource("LoginView.fxml"));
        Parent loginRoot = loginLoader.load();

        LoginController loginCtrl = loginLoader.<LoginController>getController();
        loginCtrl.setServer(server);

        FXMLLoader mainLoader = new FXMLLoader(getClass().getClassLoader().getResource("MainView.fxml"));
        Parent mainRoot = mainLoader.load();

        MainController mainCtrl = mainLoader.getController();
        mainCtrl.setServer(server);


        loginCtrl.setMainController(mainCtrl);
        loginCtrl.setParent(mainRoot);
        loginCtrl.setPrimaryStage(primaryStage);

        loginRoot.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        loginRoot.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(loginRoot));
        primaryStage.show();

    }
}
