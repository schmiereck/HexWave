package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig3;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitializeLifeService {
    @Autowired
    private LifeService lifeService;

    @Autowired
    private HexGridService hexGridService;
    private PartIdentity wallPartIdentity;

    public void initialize() {
        this.wallPartIdentity = new PartIdentity(0.5D, 1.0D, 1.0D);
    }

    public void initializeWalls() {
        // Left-/ Right-Walls.
        this.createWallY(0, 1, this.hexGridService.getNodeCountY() - 1);
        this.createWallY(this.hexGridService.getNodeCountX() - 1, 1, this.hexGridService.getNodeCountY() - 1);

        // Bottom-Wall.
        this.createWallX(0, this.hexGridService.getNodeCountX() - 1, this.hexGridService.getNodeCountY() - 1);
    }

    public void initializeExtraWalls() {
        final int middleXPos = this.hexGridService.getNodeCountX() / 2;
        final int leftXPos = middleXPos - 20;
        final int rightXPos = middleXPos + 20;
        final int bottomYPos = this.hexGridService.getNodeCountY() - 15;
        final int topYPos = bottomYPos - 25;

        // Middle-Wall.
        this.createWallY(middleXPos, bottomYPos, topYPos);
        this.createWallX(middleXPos - 15, middleXPos - 1, bottomYPos - 10);
        this.createWallX(middleXPos + 15, middleXPos + 1, bottomYPos - 10);
        // Bottom-Wall.
        this.createWallX(leftXPos, rightXPos, bottomYPos);
    }

    private void createWallY(final int middleXPos, final int aYPos, final int bYPos) {
        final int bottomYPos = Math.max(aYPos, bYPos);
        final int topYPos = Math.min(aYPos, bYPos);

        for (int posY = topYPos; posY <= bottomYPos; posY++) {
            this.createWall(middleXPos, posY);
        }
    }

    private void createWallX(final int aXPos, final int bXPos, final int bottomYPos) {
        final int leftXPos = Math.min(aXPos, bXPos);
        final int rightXPos = Math.max(aXPos, bXPos);

        for (int posX = leftXPos; posX <= rightXPos; posX++) {
            this.createWall(posX, bottomYPos);
        }
    }

    public void createWall(final int xPos, final int posY) {
        final GridNode wallGridNode = this.hexGridService.getGridNode(xPos, posY);
        final Part wallPart = new Part(Part.PartType.Wall, MainConfig3.InitialWallPartEnergy, 0);
        final LifePart wallLifePart = new LifePart(this.wallPartIdentity, null, wallGridNode, wallPart, 0);
        this.lifeService.addWallLifePart(wallLifePart);
    }
}
