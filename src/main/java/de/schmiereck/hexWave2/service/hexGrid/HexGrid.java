package de.schmiereck.hexWave2.service.hexGrid;

public class HexGrid {
    public static final int GridModX = 7;
    public static final int GridModY = 14;
    private final int nodeCountX;
    private final int nodeCountY;
    private final GridNode[][] grid;

    public HexGrid(final int sizeX, final int sizeY, final int maxAreaDistance) {
        this.nodeCountX = sizeX * GridModX;
        this.nodeCountY = sizeY * GridModY;
        this.grid = new GridNode[this.nodeCountX][this.nodeCountY];

        for (int posY = 0; posY < this.nodeCountY; posY++) {
            for (int posX = 0; posX < this.nodeCountX; posX++) {
                this.grid[posX][posY] = new GridNode(posX, posY, maxAreaDistance);
            }
        }
    }

    public GridNode getGridNode(final int posX, final int posY) {
        return this.grid[this.calcPosX(posX)][this.calcPosY(posY)];
    }

    private int calcPosX(final int posX) {
        if (posX >= 0) {
            return posX % this.nodeCountX;
        } else {
            return this.nodeCountX + (posX % this.nodeCountX);
        }
    }

    private int calcPosY(final int posY) {
        if (posY >= 0) {
            return posY % this.nodeCountY;
        } else {
            return this.nodeCountY + (posY % this.nodeCountY);
        }
    }

    public int getNodeCountX() {
        return this.nodeCountX;
    }

    public int getNodeCountY() {
        return this.nodeCountY;
    }
}
