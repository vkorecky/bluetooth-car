package org.korecky.bluetooth.car.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.korecky.bluetooth.car.controller.fxml.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vladislav Korecký
 */
public class App extends Application {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {       
        launch(args);
    }

    /**
     * Application starting point
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {        
        stage = new Stage(StageStyle.DECORATED);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
        });
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/org/korecky/bluetooth/car/controller/images/icons_128x128/logo.png")));
        stage.setTitle("Bluetooth CAR | CONTROLLER");        
        Main mainPane = new Main();
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        Screen screen = Screen.getPrimary();
        logger.info("Current system DPI is " + screen.getDpi());
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setMaximized(true);
        stage.show();

    }
}
