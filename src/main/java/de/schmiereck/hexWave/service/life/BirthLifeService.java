package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.brain.BrainService;
import de.schmiereck.hexWave.service.genom.Genom;
import de.schmiereck.hexWave.service.genom.GenomService;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;

import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BirthLifeService {
    @Autowired
    private HexGridService hexGridService;

    @Autowired
    private GenomService genomService;

    @Autowired
    private BrainService brainService;

    private final Random rnd = new Random();

    @NotNull
    public LifePart createChildLifePart(final LifePart parentLifePart, final double mutationRate, final double energy) {
        final Brain parentLifePartBrain = parentLifePart.getBrain();
        final Genom parentGenom = parentLifePartBrain.getGenom();
        final Genom newGenom = this.genomService.createMutatedGenom(parentGenom, mutationRate);

        final Brain childBrain = this.brainService.createBrain(newGenom);
        final PartIdentity childPartIdentity = this.calcChildPartIdentity(parentLifePart.partIdentity);
        final LifePart childLifePart = this.createLifePartByBrain(childPartIdentity, childBrain, energy);
        return childLifePart;
    }

    public LifePart createLifePartByBrain(final PartIdentity partIdentity, final Brain brain, final double energy) {
        final GridNode gridNode = this.hexGridService.searchRandomEmptyGridNode(false);

        final Part part = new Part(Part.PartType.Life, energy, 8);

        this.hexGridService.addPart(gridNode, part);

        return new LifePart(partIdentity, brain, gridNode, part);
    }

    private PartIdentity calcChildPartIdentity(final PartIdentity parentPartIdentity) {
        return new PartIdentity(this.calcChildPartIdentityValue(parentPartIdentity.partIdentity[0]),
                this.calcChildPartIdentityValue(parentPartIdentity.partIdentity[1]),
                this.calcChildPartIdentityValue(parentPartIdentity.partIdentity[2]));
    }

    private double calcChildPartIdentityValue(final double parentPartIdentityValue) {
        return parentPartIdentityValue + (this.rnd.nextDouble() * 0.1D) - 0.05D;
    }

    public PartIdentity createPartIdentity() {
        return new PartIdentity(this.calcChildPartIdentityValue(),
                this.calcChildPartIdentityValue(),
                this.calcChildPartIdentityValue());
    }

    private double calcChildPartIdentityValue() {
        return (this.rnd.nextDouble() * 2.0D) - 1.0D;
    }

}
