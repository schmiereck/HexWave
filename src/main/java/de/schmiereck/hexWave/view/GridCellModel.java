package de.schmiereck.hexWave.view;

import javafx.scene.shape.Circle;

public class GridCellModel {
    private final double screenPosX;
    private final double screenPosY;
    private Circle shape;
    private Circle shape2;

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
        return this.shape2;
    }

    public void setShape2(final Circle shape2) {
        this.shape2 = shape2;
    }
}
