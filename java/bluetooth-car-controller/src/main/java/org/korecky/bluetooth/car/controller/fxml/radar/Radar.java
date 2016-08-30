package org.korecky.bluetooth.car.controller.fxml.radar;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;

/**
 *
 * @author vkorecky
 */
public class Radar extends StackPane {

    private Canvas mainCanvas;
    private Canvas signalCanvas;
    private Canvas poisCanvas;
    private double currentRadarAngel = 0;
    private double signalAngelSize = 45;
    private double canvasWidth = 0;
    private double canvasHeight = 0;
    private List<PointOfInterest> pointsOfInterest = new ArrayList<>();

    /**
     *
     * @param signalAngelSize size of angel of radar signal
     */
    public Radar(double signalAngelSize) {
        this.signalAngelSize = signalAngelSize;
        this.setStyle("-fx-background-color: #1b1b1b");
        mainCanvas = new Canvas();
        this.getChildren().add(mainCanvas);
        signalCanvas = new Canvas();
        this.getChildren().add(signalCanvas);
        poisCanvas = new Canvas();
        this.getChildren().add(poisCanvas);
    }

    public void redraw(double width, double height) {
        canvasWidth = width;

        if (width > (height * 2)) {
            canvasWidth = (height * 2);
        }
        canvasHeight = canvasWidth / 2;

        mainCanvas.setWidth(canvasWidth);
        mainCanvas.setHeight(canvasHeight);

        signalCanvas.setWidth(canvasWidth);
        signalCanvas.setHeight(canvasHeight);

        poisCanvas.setWidth(canvasWidth);
        poisCanvas.setHeight(canvasHeight);

        drawBackground();
        drawRadarSignal(currentRadarAngel);
        drawPointsOfInterest();
    }

    private void drawBackground() {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        // Delete canvas
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        // Set stroke color
        gc.setStroke(Color.web("#dd4814"));
        // Create axis
        gc.strokeLine(0, canvasHeight, canvasWidth, canvasHeight);
        gc.strokeLine(canvasHeight, 0, canvasHeight, canvasHeight);
        // Draw Arc
        for (double i = 0; i <= 1.0; i += 0.25) {
            double arcWidth = canvasWidth * (1.0 - i);
            double position = (canvasHeight) * i;
            gc.strokeArc(position, position, arcWidth, arcWidth, 0, 180, ArcType.OPEN);
        }
    }

    public void drawRadarSignal(double radarAngel) {
        currentRadarAngel = radarAngel;
        GraphicsContext gc = signalCanvas.getGraphicsContext2D();
        // Delete canvas
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        double startAngel = radarAngel - (signalAngelSize / 2);
        double endAngel = radarAngel + (signalAngelSize / 2);

        Point2D pointCenter = claculateXYPoints(canvasWidth, canvasHeight, radarAngel);
        Point2D point1 = claculateXYPoints(canvasWidth, canvasHeight, startAngel);
        Point2D point2 = claculateXYPoints(canvasWidth, canvasHeight, endAngel);

        int nPoints = 4;
        double[] xPoints = new double[]{canvasWidth / 2, point1.getX(), pointCenter.getX(), point2.getX()};
        double[] yPoints = new double[]{canvasHeight, point1.getY(), pointCenter.getY(), point2.getY()};

        LinearGradient linearGradient = new LinearGradient(canvasWidth / 2, canvasHeight, pointCenter.getX(), pointCenter.getY(), false,
                CycleMethod.REFLECT,
                new Stop(0.0, Color.web("#dd4814")),
                new Stop(1.0, Color.TRANSPARENT));

        gc.setGlobalAlpha(0.5);
        gc.setStroke(linearGradient);
        gc.setFill(linearGradient);
        gc.fillPolygon(xPoints, yPoints, nPoints);
    }

    public void drawPointsOfInterest() {
        double poiSize = 50;
        double zoom = 10;
        GraphicsContext gc = poisCanvas.getGraphicsContext2D();
        // Delete canvas
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        for (PointOfInterest pointOfInterest : pointsOfInterest) {
            Point2D poi2D = claculateXYPoints(pointOfInterest.getDistance() * 10, pointOfInterest.getAngel(), canvasWidth / 2, canvasHeight);
            double x = poi2D.getX();
            double y = poi2D.getY();
            // Set colors
            RadialGradient radialGradient = new RadialGradient(0, 0.1, x, y, poiSize, false,
                    CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.web("#dd4814")),
                    new Stop(1.0, Color.TRANSPARENT));
            gc.setFill(radialGradient);
            
            x = x + (poiSize / 2);
            y = y + (poiSize / 2);
            gc.fillOval(x, y, poiSize, poiSize);
        }
    }

    private Point2D claculateXYPoints(double distance, double angel, double originX, double originY) {
        double xPoint = 0;
        double yPoint = 0;

        if (angel > 90) {
            angel = 180 - angel;
        }

        xPoint = abs(cos(angel)) * distance;
        yPoint = abs(sin(angel)) * distance;

        yPoint = originY - yPoint;

        if (angel > 90) {
            xPoint = originX - xPoint;
        } else {
            xPoint = originX + xPoint;
        }

        if (angel == 90) {
            xPoint = originX;
        }

        return new Point2D(xPoint, yPoint);
    }

    private Point2D claculateXYPoints(double maxWith, double maxHeight, double angel) {
        double xPoint = 0;
        double yPoint = 0;
        if (angel < 0) {
            xPoint = maxWith;
            yPoint = maxHeight;
        }
        if ((angel >= 0) && (angel <= 45)) {
            xPoint = maxWith;
            yPoint = maxHeight - (maxHeight / 45) * angel;
        }
        if ((angel > 45) && (angel <= 135)) {
            xPoint = maxWith - (maxWith / 90) * (angel - 45);
            yPoint = 0;
        }
        if ((angel > 135) && (angel <= 180)) {
            xPoint = 0;
            yPoint = (maxHeight / 45) * (angel - 135);
        }
        if (angel > 180) {
            xPoint = 0;
            yPoint = maxHeight;
        }
        return new Point2D(xPoint, yPoint);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }

}
