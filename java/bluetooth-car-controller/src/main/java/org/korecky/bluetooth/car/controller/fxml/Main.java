package org.korecky.bluetooth.car.controller.fxml;

import org.korecky.bluetooth.car.controller.fxml.radar.Radar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
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
import org.korecky.bluetooth.car.controller.Configuration;
import org.korecky.bluetooth.car.controller.fxml.dialogs.SelectBluetoothDeviceDialog;
import org.korecky.bluetooth.car.controller.fxml.radar.PointOfInterest;
import org.korecky.bluetooth.client.hc06.BluetoothScanThread;
import org.korecky.bluetooth.client.hc06.RFCommClientThread;
import org.korecky.bluetooth.client.hc06.entity.RFCommBluetoothDevice;
import org.korecky.bluetooth.client.hc06.event.ErrorEvent;
import org.korecky.bluetooth.client.hc06.event.MessageReceivedEvent;
import org.korecky.bluetooth.client.hc06.event.ProgressUpdatedEvent;
import org.korecky.bluetooth.client.hc06.event.ScanFinishedEvent;
import org.korecky.bluetooth.client.hc06.listener.BluetoothScanEventListener;
import org.korecky.bluetooth.client.hc06.listener.RFCommClientEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vladislav Korecký
 */
public class Main extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private RFCommBluetoothDevice selectedDevice;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lblStatus;

    private Radar radar;

    /**
     *
     * @throws javax.bluetooth.BluetoothStateException
     */
    public Main() throws BluetoothStateException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        radar = new Radar(45);
        setCenter(radar);

        sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener(observable -> radar.redraw(newScene.getWidth(), newScene.getHeight() - 100));
                newScene.heightProperty().addListener(observable -> radar.redraw(newScene.getWidth(), newScene.getHeight() - 100));
            }
        });

        scanBluetoothDevices();
    }

    @FXML
    void btnSettingsOnAction(ActionEvent event) throws BluetoothStateException {
        scanBluetoothDevices();
    }

    private void scanBluetoothDevices() throws BluetoothStateException {
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
                String deviceAddress = Configuration.getConfiguration().getBluetoothDeviceAddress();
                if ((deviceAddress != null) && (deviceAddress.trim().length() > 0)) {
                    if (evt.getFoundDevices() != null) {
                        for (RFCommBluetoothDevice device : evt.getFoundDevices()) {
                            if (deviceAddress.equalsIgnoreCase(device.getAddress())) {
                                selectedDevice = device;
                                break;
                            }
                        }
                    }
                }

                if (selectedDevice == null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Optional<RFCommBluetoothDevice> result = new SelectBluetoothDeviceDialog(
                                    "Settings",
                                    "Settings",
                                    new Image(this.getClass().getResourceAsStream("/org/korecky/bluetooth/car/controller/images/icons_48x48/bluetooth.png")),
                                    evt.getFoundDevices()).showAndWait();
                            if (result.isPresent()) {
                                selectedDevice = result.get();
                                Configuration.getConfiguration().setBluetoothDeviceAddress(selectedDevice.getAddress());
                                Configuration.getConfiguration().save();
                            }
                        }
                    });
                }

                if (selectedDevice != null) {

                    try {
                        // Listen bluetooth device
                        RFCommClientThread commThread = new RFCommClientThread(selectedDevice.getUrl(), '\n', new RFCommClientEventListener() {
                            @Override
                            public void error(ErrorEvent evt) {
                                LOGGER.error("Communication error", evt.getError());
                            }

                            @Override
                            public void messageReceived(MessageReceivedEvent evt) {
                                LOGGER.debug(String.format("[%s] %s", new Date(), evt.getMessage()));
                                if (evt.getMessage() != null) {
                                    String[] data = evt.getMessage().split("\\:");
                                    if (data.length > 1) {
                                        try {
                                            double angel = Double.parseDouble(data[0].trim());
                                            double distance = Double.parseDouble(data[1].trim());
                                            radar.drawRadarSignal(angel);

                                            if (distance >= 0) {
                                                // Delete old pois in angel
                                                List<PointOfInterest> pointsOfInterest = new ArrayList<>();
                                                for (PointOfInterest pointOfInterest : radar.getPointsOfInterest()) {
                                                    if (pointOfInterest.getAngel() != angel) {
                                                        pointsOfInterest.add(pointOfInterest);
                                                    }
                                                }
                                                pointsOfInterest.add(new PointOfInterest(angel, distance));
                                                radar.setPointsOfInterest(pointsOfInterest);
                                                radar.drawPointsOfInterest();
                                            }
                                        } catch (Throwable exc) {
                                            LOGGER.error("Cannot parse data from car.", exc);
                                        }
                                    }
                                }
                            }
                        });
                        commThread.start();
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
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
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
