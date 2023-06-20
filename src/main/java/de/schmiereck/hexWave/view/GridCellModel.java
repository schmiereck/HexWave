package de.schmiereck.hexWave.view;

import javafx.scene.shape.Circle;

public class GridCellModel {
    private final double screenPosX;
    private final double screenPosY;
    private Circle shape;
    private Circle[] shapeArr = new Circle[4];

    public GridCellModel(final double screenPosX, final double screenPosY) {
        this.screenPosX = screenPosX;
        this.screenPosY = screenPosY;
    }

    public double getScreenPosX() {
        return this.screenPosX;
    }

    public double getScreenPosY() {
        return this.screenPosY;
    }

    public Circle getShape() {
        return this.shape;
    }

    public void setShape(final Circle shape) {
        this.shape = shape;
    }

    public Circle getShape2() {
        return this.shapeArr[0];
    }

    public void setShape2(final Circle shape) {
        this.shapeArr[0] = shape;
    }

    public Circle getShape3() {
        return this.shapeArr[1];
    }

    public void setShape3(final Circle shape) {
        this.shapeArr[1] = shape;
    }

    public Circle getShape4() {
        return this.shapeArr[2];
    }

    public void setShape4(final Circle shape) {
        this.shapeArr[2] = shape;
    }

    public Circle getShape5() {
        return this.shapeArr[3];
    }

    public void setShape5(final Circle shape) {
        this.shapeArr[3] = shape;
    }
}
