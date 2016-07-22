package org.korecky.bluetooth.car.controller.fxml;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vladislav Korecký
 */
public class Main extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
        @FXML
    private ProgressBar progressBar;

    @FXML
    private Label lblStatus;

    @FXML
    private BorderPane mainPane;
    
    /**
     *
     */
    public Main() {        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));        
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }     
    }
}
