package org.korecky.bluetooth.car.controller.fxml;

import java.io.IOException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javax.bluetooth.BluetoothStateException;
import org.korecky.bluetooth.car.controller.fxml.dialogs.SelectBluetoothDeviceDialog;
import org.korecky.bluetooth.client.hc06.BluetoothScanThread;
import org.korecky.bluetooth.client.hc06.entity.RFCommBluetoothDevice;
import org.korecky.bluetooth.client.hc06.event.ErrorEvent;
import org.korecky.bluetooth.client.hc06.event.ProgressUpdatedEvent;
import org.korecky.bluetooth.client.hc06.event.ScanFinishedEvent;
import org.korecky.bluetooth.client.hc06.listener.BluetoothScanEventListener;
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

    @FXML
    void btnSettingsOnAction(ActionEvent event) throws BluetoothStateException {
        // Prepare search thread
        BluetoothScanThread scanThread = new BluetoothScanThread(new BluetoothScanEventListener() {
            @Override
            public void error(ErrorEvent evt) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(evt.getError().getMessage());
                alert.setContentText(evt.getError().toString());
                alert.showAndWait();
            }

            @Override
            public void scanFinished(ScanFinishedEvent evt) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Optional<RFCommBluetoothDevice> result = new SelectBluetoothDeviceDialog(
                                "Settings",
                                "Settings",
                                new Image(this.getClass().getResourceAsStream("/org/korecky/bluetooth/car/controller/images/icons_48x48/bluetooth.png")),
                                evt.getFoundDevices()).showAndWait();
                    }
                });

//                System.out.println("");
//                System.out.println("Found RFComm decices");
//                int i = 1;
//                for (RFCommBluetoothDevice device : evt.getFoundDevices()) {
//                    System.out.println(String.format("%d:", i));
//                    System.out.println(String.format("   Address: %s", device.getAddress()));
//                    System.out.println(String.format("   Name: %s", device.getName()));
//                    System.out.println(String.format("   URL: %s", device.getUrl()));
//                    i++;
//                }
//                System.out.println();
//                System.out.print("Device number for communication:");
//                Scanner in = new Scanner(System.in);
//                int selected = in.nextInt();
//
//                if ((selected > 0) && (selected <= evt.getFoundDevices().size())) {
//                    try {
//                        // Listen bluetooth device
//                        RFCommBluetoothDevice selectedDevice = evt.getFoundDevices().get(selected - 1);
//                        RFCommClientThread commThread = new RFCommClientThread(selectedDevice.getUrl(), '\n', new RFCommClientEventListener() {
//                            @Override
//                            public void error(ErrorEvent evt) {
//                                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                            }
//
//                            @Override
//                            public void messageReceived(MessageReceivedEvent evt) {
//                                System.out.println(String.format("[%s] %s", new Date(), evt.getMessage()));
//                            }
//                        });
//                        commThread.start();
//
//                        // Send message to Arduino
//                        System.out.print("What's your name? :");
//                        in = new Scanner(System.in);
//                        String name = in.nextLine();
//                        commThread.send(name);
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException ex) {
//                            logger.error("Cannot sleep main thread.");
//                        }
//                        System.exit(0);
//                    } catch (IOException ex) {
//                        logger.error("Cannot connect to device.", ex);
//                    }
//                } else {
//                    System.out.print("Invalid selection.");
//                }
            }

            @Override
            public void progressUpdated(ProgressUpdatedEvent evt) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lblStatus.setText(evt.getMessage());
                        double oneStep = 1.0 / evt.getWorkMax();
                        double progress = evt.getWorkDone() * oneStep;
                        progressBar.setProgress(progress);
                        LOGGER.debug(String.format("[%d/%d] %s", evt.getWorkDone(), evt.getWorkMax(), evt.getMessage()));
                    }
                });
            }
        });

        // Start search of bluetooth device
        scanThread.start();
    }
}
