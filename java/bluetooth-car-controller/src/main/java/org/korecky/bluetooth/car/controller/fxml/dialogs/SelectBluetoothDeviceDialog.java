package org.korecky.bluetooth.car.controller.fxml.dialogs;

import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.korecky.bluetooth.client.hc06.entity.RFCommBluetoothDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Vladislav Korecký
 */
public class SelectBluetoothDeviceDialog extends Dialog<RFCommBluetoothDevice> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectBluetoothDeviceDialog.class);

    /**
     *
     * @param dialogTitle
     * @param headerText
     * @param icon
     * @param bluetoothDevices
     */
    public SelectBluetoothDeviceDialog(String dialogTitle, String headerText, Image icon, List<RFCommBluetoothDevice> bluetoothDevices) {
        super();
        initializeUI(dialogTitle, headerText, icon, bluetoothDevices);
    }

    private void initializeUI(String title, String headerText, Image icon, List<RFCommBluetoothDevice> bluetoothDevices) {
        this.setTitle(title);
        this.setHeaderText(headerText);

        // Get the Stage.
        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();

        if (icon != null) {
            // Add a custom icon.
            this.setGraphic(new ImageView(icon));
            stage.getIcons().add(icon);
        }

        // Set the button types.
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create UI
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ListView<RFCommBluetoothDevice> devicesList = new ListView<>(FXCollections.observableList(bluetoothDevices));
        devicesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.add(devicesList, 0, 0);

        this.getDialogPane().setContent(grid);

        // Enable/Disable OK button
        Node okButton = this.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Do password validation
        devicesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue == null);
        });

        // Request focus on default field.
        Platform.runLater(() -> devicesList.requestFocus());

        // Convert the result to a username-password-pair when the login button
        // is clicked.
        this.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return devicesList.getSelectionModel().getSelectedItem();
            }
            return null;
        });
    }
}
