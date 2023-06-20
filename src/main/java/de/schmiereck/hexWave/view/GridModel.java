package de.schmiereck.hexWave.view;

import javafx.scene.shape.Circle;

public class GridModel {
    private int nodeCountX;
    private int nodeCountY;
    private GridCellModel[][] gridCellArr;

    public void init(final int nodeCountX, final int nodeCountY) {
        this.nodeCountX = nodeCountX;
        this.nodeCountY = nodeCountY;
        this.gridCellArr = new GridCellModel[nodeCountX][nodeCountY];

        for (int posY = 0; posY < this.nodeCountY; posY++) {
            for (int posX = 0; posX < this.nodeCountX; posX++) {
                final double screenPosX = calcScreenPosX(posX, posY);
                final double screenPosY = calcScreenPosY(posX, posY);
                this.gridCellArr[posX][posY] = new GridCellModel(screenPosX, screenPosY);
            }
        }
    }

    public void setShape(final int posX, final int posY, final Circle shape) {
        this.gridCellArr[posX][posY].setShape(shape);
    }

    public void setShape2(final int posX, final int posY, final Circle shape) {
        this.gridCellArr[posX][posY].setShape2(shape);
    }

    public void setShape3(final int posX, final int posY, final Circle shape) {
        this.gridCellArr[posX][posY].setShape3(shape);
    }

    public void setShape4(final int posX, final int posY, final Circle shape) {
        this.gridCellArr[posX][posY].setShape4(shape);
    }

    public void setShape5(final int posX, final int posY, final Circle shape) {
        this.gridCellArr[posX][posY].setShape5(shape);
    }

    public static final double StepX = 16.0D;
    public static final double StepHalfX = StepX / 2.0D;
    public static final double StepY = Math.sqrt(Math.pow(StepX, 2.0D) - Math.pow(StepHalfX, 2.0D));

    private static double calcScreenPosX(final int posX, final int posY) {
        return (posX * StepX) + ((posY % 2) * StepHalfX);
    }

    private static double calcScreenPosY(final int posX, final int posY) {
        return (posY * StepY);
    }

    public GridCellModel getGridCellModel(final int posX, final int posY) {
        return this.gridCellArr[posX][posY];
    }

    public int getNodeCountX() {
        return this.nodeCountX;
    }

    public int getNodeCountY() {
        return this.nodeCountY;
    }
}
