package org.korecky.bluetooth.car.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author vkorecky
 */
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
    private final String APPLICATION_HOME_PATH = System.getProperty("user.home") + System.getProperty("file.separator") + ".bluetooth-car-controller";
    private final String APPLICATION_CONFIGURATION_PATH = APPLICATION_HOME_PATH + System.getProperty("file.separator") + "configuration.properties";
    private final String CONF_KEY_BLUETOOTH_DEVICE_ADDRESS = "BluetoothDeviceAddress";

    private static Configuration instance;

    private String bluetoothDeviceAddress;

    private Configuration() {
        load();
    }

    public static Configuration getConfiguration() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private void load() {
        Properties props = new Properties();
        InputStream is = null;

        // First try loading from the current directory
        try {
            File f = new File(APPLICATION_CONFIGURATION_PATH);
            if (f.exists()) {
                is = new FileInputStream(f);
                props.load(is);
                bluetoothDeviceAddress = props.getProperty(CONF_KEY_BLUETOOTH_DEVICE_ADDRESS);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Cannot open configuration file. '%s'", APPLICATION_CONFIGURATION_PATH), e);
        }
    }

    public void save() {
        try {
            // Check APPLICATION_HOME_PATH 
            File appFolder = new File(APPLICATION_HOME_PATH);
            if (!appFolder.exists()) {
                appFolder.mkdirs();
            }

            // Safe configuration file
            Properties props = new Properties();
            props.setProperty(CONF_KEY_BLUETOOTH_DEVICE_ADDRESS, getBluetoothDeviceAddress());
            File f = new File(APPLICATION_CONFIGURATION_PATH);
            OutputStream out = new FileOutputStream(f);
            props.store(out, "Bluetooth CAR | CONTROLLER configuration");
        } catch (Exception e) {
            LOGGER.error(String.format("Cannot save configuration file. '%s'", APPLICATION_CONFIGURATION_PATH), e);
        }
    }

    public String getBluetoothDeviceAddress() {
        return bluetoothDeviceAddress;
    }

    public void setBluetoothDeviceAddress(String bluetoothDeviceAddress) {
        this.bluetoothDeviceAddress = bluetoothDeviceAddress;
    }

}
