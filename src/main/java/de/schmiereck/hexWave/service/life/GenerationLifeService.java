package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.brain.BrainService;
import de.schmiereck.hexWave.service.genom.Genom;
import de.schmiereck.hexWave.service.genom.GenomService;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenerationLifeService {
    @Autowired
    private HexGridService hexGridService;

    @Autowired
    private GenomService genomService;

    @Autowired
    private BrainService brainService;

    @Autowired
    private BirthLifeService birthLifeService;

    private final Random rnd = new Random();

    public void initializeLifePartList(final List<LifePart> lifePartList, final int lifePartCount) {
        final Genom lifePartGenom = this.genomService.createInitialGenom();

        for (int lifePartPos = 0; lifePartPos < lifePartCount; lifePartPos++) {
            final Brain brain = this.brainService.createBrain(lifePartGenom);
            final PartIdentity partIdentity = this.birthLifeService.createPartIdentity();

            lifePartList.add(this.birthLifeService.createLifePartByBrain(partIdentity, brain, this.rnd.nextDouble(MainConfig.InitialLifePartEnergy / 2.0D, MainConfig.InitialLifePartEnergy)));
        }
    }
    
    public void initializeByGenomList(List<LifePart> lifePartList, List<Genom> genomList) {
        lifePartList.stream().forEach(lifePart -> this.hexGridService.removePart(lifePart.getGridNode(), lifePart.getPart()));
        lifePartList.clear();
        
        for (int genomPos = 0; genomPos < genomList.size(); genomPos++) {
            final Genom lifePartGenom = genomList.get(0);
            final Brain brain = this.brainService.createBrain(lifePartGenom);
            final PartIdentity partIdentity = this.birthLifeService.createPartIdentity();

            lifePartList.add(this.birthLifeService.createLifePartByBrain(partIdentity, brain, this.rnd.nextDouble(MainConfig.InitialLifePartEnergy / 2.0D, MainConfig.InitialLifePartEnergy)));
        }
    }

    public List<LifePart> calcGenPoolWinners(final List<LifePart> lifePartList, final int lifePartCount) {
        lifePartList.stream().forEach(lifePart -> this.hexGridService.removePart(lifePart.getGridNode(), lifePart.getPart()));

        final int maxPosX = this.hexGridService.getNodeCountX() / 2;
        final List<LifePart> winnerLifePartList = lifePartList.stream().filter(lifePart -> lifePart.getGridNode().getPosX() > maxPosX).collect(Collectors.toList());
        final List<LifePart> youngLifePartList = winnerLifePartList.subList(winnerLifePartList.size() / 2, winnerLifePartList.size());

        if (youngLifePartList.isEmpty()) {
            youngLifePartList.add(lifePartList.get(0));
        }

        youngLifePartList.stream().forEach(lifePart -> {
            final GridNode gridNode = this.hexGridService.searchRandomEmptyGridNode(false);
            this.hexGridService.addPart(gridNode, lifePart.getPart());
            lifePart.setGridNode(gridNode);
        });

        final List<LifePart> childLifePartList = new ArrayList<>();

        for (int pos = 0; pos < (lifePartCount - youngLifePartList.size()); pos++) {
            final LifePart youngLifePart = youngLifePartList.get(pos % youngLifePartList.size());
            final LifePart childLifePart = this.birthLifeService.createChildLifePart(youngLifePart,
                    MainConfig.PoolChildMutationRate,
                    this.rnd.nextDouble(MainConfig.InitialLifePartEnergy / 2.0D, MainConfig.InitialLifePartEnergy));

            childLifePartList.add(childLifePart);
        }

        youngLifePartList.addAll(childLifePartList);

        return youngLifePartList;
    }

    void runBirth(final List<LifePart> lifePartList, final int lifePartCount, final int minLifePartCount, final boolean onlyHighEnergy) {
        if (lifePartList.size() < minLifePartCount) {
            while (lifePartList.size() < lifePartCount) {
                final LifePart childLifePart;
                if (lifePartList.size() == 0) {
                    final Genom genom = this.genomService.createInitialGenom();
                    final Brain brain = this.brainService.createBrain(genom);
                    final PartIdentity partIdentity = this.birthLifeService.createPartIdentity();
                    childLifePart = this.birthLifeService.createLifePartByBrain(partIdentity, brain, MainConfig.InitialLifePartEnergy);
                } else {
                    final LifePart parentLifePart;
                    if (onlyHighEnergy) {
                        final List<LifePart> parentLifePartList = lifePartList.stream().filter(searchParentLifePart -> searchParentLifePart.getPart().getEnergy() > MainConfig.InitialLifePartEnergy).collect(Collectors.toList());
                        if (parentLifePartList.isEmpty()) {
                            parentLifePart = lifePartList.get(this.rnd.nextInt(lifePartList.size()));
                        } else {
                            parentLifePart = parentLifePartList.get(this.rnd.nextInt(parentLifePartList.size()));
                        }
                    } else {
                        parentLifePart = lifePartList.get(this.rnd.nextInt(lifePartList.size()));
                    }
                    childLifePart = this.birthLifeService.createChildLifePart(parentLifePart,
                            MainConfig.PoolChildMutationRate,
                            this.rnd.nextDouble(MainConfig.InitialLifePartEnergy / 2.0D, MainConfig.InitialLifePartEnergy));
                }
                if (Objects.nonNull(childLifePart)) {
                    lifePartList.add(childLifePart);
                }
            }
        }
    }

    public boolean runDeath(final LifePart lifePart) {
        final boolean removed;
        final Part part = lifePart.getPart();

        if (part.getEnergy() <= 0.0D) {
            final GridNode gridNode = lifePart.getGridNode();
            this.hexGridService.removePart(gridNode, part);
            removed = true;
        } else {
            removed = false;
        }
        return removed;
    }

}
