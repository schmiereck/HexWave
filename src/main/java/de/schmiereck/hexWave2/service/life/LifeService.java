package de.schmiereck.hexWave2.service.life;

import static de.schmiereck.hexWave2.MainConfig3.MaxImpulseProb;
import static de.schmiereck.hexWave2.MainConfig3.MaxImpulsePercent;

import de.schmiereck.hexWave2.MainConfig3;
import de.schmiereck.hexWave2.math.ProbabilityService;
import de.schmiereck.hexWave2.math.ProbabilityVector;
import de.schmiereck.hexWave2.service.hexGrid.Cell;
import de.schmiereck.hexWave2.service.hexGrid.GridNode;
import de.schmiereck.hexWave2.service.hexGrid.HexGridService;
import de.schmiereck.hexWave2.service.hexGrid.Part;
import de.schmiereck.hexWave2.service.hexGrid.Particle;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifeService {


    @Autowired
    private HexGridService hexGridService;

    private final Random rnd = new Random();

    public LifeService() {
    }

    public void initialize() {
    }

    /**
     *     bn  cp
     *      \ /
     *  an---A---ap
     *      / \
     *     cn  bp
     *
     */
    public void initializeBall(final int ballXPos, final int ballYPos, final MainConfig3.StartVelocity ballStartVelocity, final boolean useBallPush,
                               final Particle.PartSubType partSubType, final Particle.PartSubType fieldSubType) {
        final GridNode gridNode = this.hexGridService.getGridNode(ballXPos, ballYPos);

        final int potentialProbability = MainConfig3.InitialBallPartPotentialProbability;

        final ProbabilityVector impulseProbabilityVector =
            switch (ballStartVelocity) {
                case Static -> ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 0, 0, 0, 0, 0, 0);
                case StaticPotential -> ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 6, 6, 6, 6, 6, 6);
                case MovingRight -> ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 25, 0, 0, 0, 0, 0);
                case MovingRightTop5 -> ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 23, 0, 2, 0, 0, 0);
                case MovingRightTop15 -> ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 20, 0, 5, 0, 0, 0);
                case MovingRightTop45 -> ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 12, 0, 12, 0, 0, 0);
                case MovingRightWithPotential -> ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 45, 6, 6, 6, 6, 6);
                case MovingLeft -> ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 0, 0, 0, 25, 0, 0);
                case MovingLeftWithPotential -> ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 6, 6, 6, 45, 6, 6);
            };
        final Particle fieldParticle;
        if (fieldSubType == Particle.PartSubType.Nothing) {
            fieldParticle = null;
        } else {
            fieldParticle = new Particle(Particle.PartType.Field, fieldSubType, null);
        }
        final Particle ballParticle = new Particle(Particle.PartType.Particle, partSubType, fieldParticle);
        final Part ballPart = new Part(ballParticle,
                Cell.Dir.AP,
                impulseProbabilityVector,
                potentialProbability);
                //ProbabilityService.createVector(, potentialProbability));
                //ProbabilityService.createVector(, potentialProbability));
                //ProbabilityService.createVector(, potentialProbability));
                //ProbabilityService.createVector(55, 10, 20, 5, 5, 5, potentialProbability));
                //ProbabilityService.createVector(40, 15, 30, 5, 5, 5, potentialProbability));
                //ProbabilityService.createVector(60, 15, 15, 5, 5, 5, potentialProbability));
                //ProbabilityService.createVector(60, 0, 40, 0, 0, 0, potentialProbability));

        //ballPart.getHexParticle().getVelocityHexVector().a = ballStartVelocityA;

        this.addPart(gridNode, ballPart);
    }

    public void addPart(final GridNode gridNode, final Part part) {
        this.hexGridService.addActPart(gridNode, part);
    }

    public void runLife() {
        this.calcNext();
    }

    public void calcNext() {
        this.hexGridService.calcNext();
    }

    public long retrievePartCount() {
        return this.hexGridService.calcPartCount();
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
        final int probability = MainConfig3.InitialWallPartProbability;
        final Particle fieldParticle = new Particle(Particle.PartType.Field, Particle.PartSubType.FieldWall, null);
        final Particle wallParticle = new Particle(Particle.PartType.Wall, Particle.PartSubType.ParticleWall, fieldParticle);
        final Part wallPart = new Part(wallParticle,
                Cell.Dir.AP,
                ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, 0, 0, 0, 0, 0, 0),
                probability);
        this.addPart(wallGridNode, wallPart);
    }

}
