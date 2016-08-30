package org.korecky.bluetooth.car.controller.fxml;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;

/**
 *
 * @author vkorecky
 */
public class Radar extends StackPane {

    private Canvas mainCanvas;
    private Canvas signalCanvas;
    private double currentRadarAngel;
    private double signalAngelSize;
    private double canvasWidth;
    private double canvasHeight;

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

        drawBackground();
        drawRadarSignal(currentRadarAngel);
    }

    public void drawRadarSignal(double radarAngel) {
        currentRadarAngel = radarAngel;
        GraphicsContext gc = signalCanvas.getGraphicsContext2D();
        // Delete canvas
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        double startAngel = radarAngel - (signalAngelSize / 2);
        double endAngel = radarAngel + (signalAngelSize / 2);

        Point2D pointCenter = claculateXYPointsFromAngel(radarAngel);
        Point2D point1 = claculateXYPointsFromAngel(startAngel);
        Point2D point2 = claculateXYPointsFromAngel(endAngel);

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

    private Point2D claculateXYPointsFromAngel(double angel) {
        double xPoint = 0;
        double yPoint = 0;
        if (angel < 0) {
            xPoint = canvasWidth;
            yPoint = canvasHeight;            
        }
        if ((angel >= 0) && (angel <= 45)) {
            xPoint = canvasWidth;
            yPoint = canvasHeight - (canvasHeight / 45) * angel;
        }
        if ((angel > 45) && (angel <= 135)) {
            xPoint = canvasWidth - (canvasWidth / 90) * (angel - 45);
            yPoint = 0;
        }
        if ((angel > 135) && (angel <= 180)) {
            xPoint = 0;
            yPoint = (canvasHeight / 45) * (angel - 135);
        }
        if (angel > 180) {
            xPoint = 0;
            yPoint = canvasHeight;
        }
        return new Point2D(xPoint, yPoint);
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

    @Override
    public boolean isResizable() {
        return true;
    }
}
