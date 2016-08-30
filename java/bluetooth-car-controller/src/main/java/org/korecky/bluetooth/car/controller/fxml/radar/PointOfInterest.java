package org.korecky.bluetooth.car.controller.fxml.radar;

/**
 *
 * @author vkorecky
 */
public class PointOfInterest {

    double angel;
    double distance;

    public PointOfInterest() {
    }

    public PointOfInterest(double angel, double distance) {
        this.angel = angel;
        this.distance = distance;
    }

    public double getAngel() {
        return angel;
    }

    public void setAngel(double angel) {
        this.angel = angel;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}
