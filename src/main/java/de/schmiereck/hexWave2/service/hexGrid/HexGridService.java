package de.schmiereck.hexWave2.service.hexGrid;

import static de.schmiereck.hexWave2.MainConfig3.FieldPotentialCutoffValue;
import static de.schmiereck.hexWave2.MainConfig3.MaxImpulseProb;
import static de.schmiereck.hexWave2.MainConfig3.MaxImpulsePercent;

import de.schmiereck.hexWave2.MainConfig3;
import de.schmiereck.hexWave2.math.ProbabilityService;
import de.schmiereck.hexWave2.math.ProbabilityVector;
import de.schmiereck.hexWave2.utils.DirUtils;
import de.schmiereck.hexWave2.utils.HexMathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * <pre><code>
 *              dir               X    Y
 *              np                0     0
 * left-right:  ap    ABCDEFG     1     0
 * left-down:   bp    AFDBGEC     1     1
 * left-up:     cp    ACEGBDF     1    -1
 * right-left:  an    GFEDCBA    -1     0
 * right-up:    bn    CEGBDFA     0    -1
 * right-down:  cn    FDBGECA     0     1
 *
 *     bn  cp    A
 *      \ /
 *  an---A---ap     A
 *      / \
 *     cn  bp    A
 *
 *  A    bn  cp    A
 *        \ /
 *    an---A---ap    A
 *        / \
 *  A    cn  bp    A
 *
 * 100 -> 20, 50, 30 =>
 *
 *                     0,8
 *                4 -> 2
 *                     1,2
 *         20 -> 10
 *                6
 *
 *               10
 *  100 -> 50 -> 25
 *               15
 *
 *                6
 *         30 -> 15
 *                9
 *
 * </code></pre>
 */
@Component
public class HexGridService {

    private static final int[][][] DirOffsetArr = {
            {
                    //!np {0, 0},    // NP
                    {1, 0},    // AP
                    {0, 1},    // BP
                    {0, -1},    // CP
                    {-1, 0},    // AN
                    {-1, -1},    // BN
                    {-1, 1}     // CN
            },
            {
                    //!np {0, 0},    // NP
                    {1, 0},    // AP
                    {1, 1},    // BP
                    {1, -1},    // CP
                    {-1, 0},    // AN
                    {0, -1},    // BN
                    {0, 1}     // CN
            }
    };

    private HexGrid hexGrid;

    private int stepCount = 0;
    private int maxAreaDistance;
    private int actCellArrPos = 0;
    private int nextCellArrPos = 1;
    private long partCount = 0;

    private final Random rnd = new Random();

    public HexGridService() {
    }

    public void initialize(final int sizeX, final int sizeY, final int maxAreaDistance) {
        this.maxAreaDistance = maxAreaDistance;

        this.hexGrid = new HexGrid(sizeX, sizeY, this.getMaxAreaDistance());

        if (MainConfig3.useGridNodeAreaRef)
            for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
                for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                    final GridNode gridNode = this.getGridNode(posX, posY);

                    for (final Cell.Dir dir : Cell.Dir.values()) {
                        final Cell.Dir leftDir = DirUtils.calcDirByRotOffset(dir, -2);
                        final Cell.Dir rightDir = DirUtils.calcDirByRotOffset(dir, 2);

                        // Iterator all Areas.
                        for (int areaDistance = 0; areaDistance < GridNodeArea.calcGridNodeArrSizeForMaxAreaDistance(this.getMaxAreaDistance()); areaDistance++) {
                            //final int startAreaDistancePos = 0;
                            final int startAreaDistancePos = areaDistance;
                            for (int areaDistancePos = startAreaDistancePos; areaDistancePos <= (areaDistance); areaDistancePos++) {
                                final GridNode neighbourareaDistanceGridNode = this.getNeighbourGridNode(gridNode, dir, areaDistancePos);

                                // Set Middle Node.
                                gridNode.setGridNodeAreaArr(dir, areaDistance, areaDistancePos, 0, neighbourareaDistanceGridNode,
                                        calcGridNodeAreaRefValue(areaDistance, areaDistancePos, 0, this.getMaxAreaDistance()));

                                // Set Left&Right Nodes.
                                for (int gridNodePos = 1; gridNodePos <= areaDistancePos; gridNodePos++) {
                                    final GridNode leftNeighbourGridNode = this.getNeighbourGridNode(neighbourareaDistanceGridNode, leftDir, gridNodePos);
                                    final GridNode rightNeighbourGridNode = this.getNeighbourGridNode(neighbourareaDistanceGridNode, rightDir, gridNodePos);

                                    // 1: (1, 2), 2: (3, 4), 3: (5, 6), ...
                                    gridNode.setGridNodeAreaArr(dir, areaDistance, areaDistancePos, gridNodePos * 2 - 1, leftNeighbourGridNode,
                                            calcGridNodeAreaRefValue(areaDistance, areaDistancePos, gridNodePos, this.getMaxAreaDistance()));
                                    gridNode.setGridNodeAreaArr(dir, areaDistance, areaDistancePos, gridNodePos * 2, rightNeighbourGridNode,
                                            calcGridNodeAreaRefValue(areaDistance, areaDistancePos, gridNodePos, this.getMaxAreaDistance()));
                                }
                            }
                        }
                    }
                }
        }
    }

    public static double calcGridNodeAreaRefValue(final int areaDistance, final int areaDistancePos, final int gridNodePos, final int maxAreaDistance) {
        final int areaNodeCount = GridNodeArea.calcGridNodeSizeForAreaDistance(areaDistance);

        final double areaDistanceValue = (((maxAreaDistance + 1) - areaDistance) / (double)maxAreaDistance);

        //final double distanceValue = (areaDistancePos / (double)MAX_AREA_DISTANCE);
        //final double distanceValue = (areaDistancePos / (double)areaNodeCount);
        final double distanceValue = ((areaDistancePos + 1) / (double)areaDistance);

        //final double posValue = (MAX_AREA_DISTANCE / ((gridNodePos / 2.0D) + 1.0D)) / MAX_AREA_DISTANCE;
        //final double posValue = ((MAX_AREA_DISTANCE - (gridNodePos / 2))) / (double)MAX_AREA_DISTANCE;
        final double posValue = (((areaDistancePos + 1) - (gridNodePos))) / (double)(areaDistancePos);
        //final double posValue = (((areaNodeCount / 2) - (gridNodePos / 2))) / (double)(areaNodeCount / 2);

        //final double value = 1.0D;
        //final double value = distanceValue * posValue;
        //final double value = areaDistanceValue;
        final double value = areaDistanceValue * distanceValue * posValue;
        //System.out.printf("value:%f\n", value);
        return value;
    }

    public GridNode getGridNode(int posX, int posY) {
        return this.hexGrid.getGridNode(posX, posY);
    }

    public void calcNext() {
        this.calcGrid();

        this.calcNextCellArrPos();
        this.clearNextGrid();

        this.stepCount++;
    }

    private record ParticleProbability(Particle particle, ProbabilityVector probabilityVector) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParticleProbability that = (ParticleProbability) o;
            return Objects.equals(this.particle, that.particle) &&
                    Objects.equals(this.probabilityVector, that.probabilityVector);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.particle, this.probabilityVector);
        }
    }

    private void calcGrid() {
        //--------------------------------------------------------------------------------------------------------------
        // Precalculation of Act-Grid
        // (ex.: Merge similar Part of same Particle, Probability to DirProbability, ...)
        this.precalcActGrid();

        // Add Field for each Life-Part
        this.addFieldsToParticleParts(this.actCellArrPos);

        this.calcActParticleImpulse();

        this.calcActGridNext();

        // Transfer probability distribution:
        // Distribute Probability to the possible DirProbabilities depending on probabilityVector.
        this.transferActProbabilityToActDirProbabilities();

        //--------------------------------------------------------------------------------------------------------------
        // Transfer rotation:
        if (MainConfig3.useRotation)
            transferRotation();

        //--------------------------------------------------------------------------------------------------------------
        // Transfer probability distribution from Act- to Next-Grid:
        // Only read from Act and write to Next!

        this.transferFromActToNext();

        // Add Field for each Life-Part
        //this.addFieldsToParticleParts(this.nextCellArrPos);

        //--------------------------------------------------------------------------------------------------------------
    }

    private void calcActParticleImpulse() {
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);
                final int finPosX = posX;
                final int finPosY = posY;

                gridNode.getPartList(this.actCellArrPos).stream().
                    filter(part -> (part.getParticle().getPartType() == Particle.PartType.Particle) &&
                            (Objects.nonNull(part.getParticle().getFieldParticle()))).
                            forEach(part -> {
                                final Particle fieldParticle = part.getParticle().getFieldParticle();
                                final int[] actNeighbourFieldPotentialProbabilitySumArr = new int[Cell.Dir.values().length];
                                for (final Cell.Dir dir : Cell.Dir.values()) {
                                    final GridNode neighbourGridNode = this.getNeighbourGridNode(finPosX, finPosY, dir);
                                    //this.searchActParticlePartListByParticle(neighbourGridNode, fieldParticle);
                                    actNeighbourFieldPotentialProbabilitySumArr[dir.ordinal()] =
                                            //this.calcActTargetFieldProbabilitySumByTypes(neighbourGridNode, fieldParticle.getPartType(), fieldParticle.getPartSubType());
                                            this.calcActTargetFieldPotentialProbabilitySumByTypesAndOtherParticles(neighbourGridNode, part.getParticle().getFieldParticle(), fieldParticle.getPartType(), fieldParticle.getPartSubType())
                                            -
                                            this.calcActTargetFieldPotentialProbabilitySumByOppositeTypesAndOtherParticles(neighbourGridNode, part.getParticle().getFieldParticle(), fieldParticle.getPartType(), fieldParticle.getPartSubType());
                                }
                                final int aDiff = actNeighbourFieldPotentialProbabilitySumArr[Cell.Dir.AP.ordinal()] - actNeighbourFieldPotentialProbabilitySumArr[Cell.Dir.AN.ordinal()];
                                final int bDiff = actNeighbourFieldPotentialProbabilitySumArr[Cell.Dir.BP.ordinal()] - actNeighbourFieldPotentialProbabilitySumArr[Cell.Dir.BN.ordinal()];
                                final int cDiff = actNeighbourFieldPotentialProbabilitySumArr[Cell.Dir.CP.ordinal()] - actNeighbourFieldPotentialProbabilitySumArr[Cell.Dir.CN.ordinal()];
                                if (aDiff != 0 || bDiff != 0 || cDiff != 0) {
                                    //final int f = -MainConfig3.MaxImpulseProb / 2;//-1;//-MainConfig3.MaxProb;//-32;
                                    final int f = -MainConfig3.MaxPotentialProbability;
                                    //TODO ProbabilityService.calcMoveVector(part.probabilityVector, aDiff / f, bDiff / f, cDiff / f);
                                    ProbabilityService.calcMoveVector(part.impulseProbabilityVector, aDiff / f, bDiff / f, cDiff / f);
                                }
                            });
            }
        }
    }

    private void transferActProbabilityToActDirProbabilities() {
        //--------------------------------------------------------------------------------------------------------------
        // Transfer probability distribution:
        // Distribute Probability to the possible DirProbabilities depending on probabilityVector.
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                gridNode.getPartList(this.actCellArrPos).stream().forEach(sourcePart -> {
                    this.transferPotentialProbabilityToDirPotentialProbabilities(sourcePart);
                });
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    private void addFieldsToParticleParts(final int cellArrPos) {
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);
                final List<Part> newFieldList = new ArrayList<>();

                gridNode.getPartList(cellArrPos).stream().
                        filter(sourcePart -> (sourcePart.getParticle().getPartType() == Particle.PartType.Particle) &&
                                (Objects.nonNull(sourcePart.getParticle().getFieldParticle()))).
                        forEach(sourcePart -> {
                            final int sourcePotentialProbability = sourcePart.getPotentialProbability();
                            if (sourcePotentialProbability > 0) {
                                final Particle fieldParticle = sourcePart.getParticle().getFieldParticle();
                                for (final Cell.Dir dir : Cell.Dir.values()) {
                                    final int apPerc = MaxImpulsePercent;
                                    final int bpPerc = MaxImpulsePercent;
                                    final int cpPerc = MaxImpulsePercent;
                                    final int anPerc = MaxImpulsePercent;
                                    final int bnPerc = MaxImpulsePercent;
                                    final int cnPerc = MaxImpulsePercent;

                                    final Part fieldPart = new Part(fieldParticle,
                                            dir,
                                            ProbabilityService.createVector(MaxImpulsePercent, MaxImpulseProb, apPerc, bpPerc, cpPerc, anPerc, bnPerc, cnPerc),
                                            //ProbabilityService.createFieldVector(dir),

                                            sourcePotentialProbability * MainConfig3.InitialFieldPartProbabilityFactor
                                            //1
                                            //6*6*6
                                            //((6*6*6 * sourcePart.getProbability()) / MainConfig3.InitialBallPartProbability)
                                            //6*6*2 + ((6*6 * sourcePart.getProbability()) / MainConfig3.InitialBallPartProbability)
                                            //6*6 + ((6*6 * sourcePart.getProbability()) / MainConfig3.InitialBallPartProbability)
                                            //((6 * sourcePart.getProbability()) / MainConfig3.InitialBallPartProbability)
                                    );

                                    newFieldList.add(fieldPart);
                                }
                            }
                        });
                gridNode.addPartList(cellArrPos, newFieldList);
            }
        }
    }

    /**
     * Transfer probability distribution from Act- to Next-Grid:
     * Only read from Act and write to Next!
     */
    private void transferFromActToNext() {
        //--------------------------------------------------------------------------------------------------------------
        // Transfer DirProbabilities from Neighbours to Probability.
        // Depending on:
        // - calcTransferProbabilityForField()
        // - checkProbabilityTransfer()
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode targetGridNode = this.getGridNode(posX, posY);

                for (final Cell.Dir dir : Cell.Dir.values()) {
                    final GridNode sourceGridNode = this.getNeighbourGridNode(posX, posY, dir);

                    final Cell.Dir sourceDir = DirUtils.calcOppositeDir(dir);
                    sourceGridNode.getPartList(this.actCellArrPos).stream().
                            forEach(sourcePart -> {
                        final int transferDirProbability;
                        final int actSourceDirPotentialProbability = sourcePart.getPotentialProbabilityByDir(sourceDir);

                        if (actSourceDirPotentialProbability > 0) {
                            // TODO double checked, remove this.
                            // Source-Part is ready for transfer?
                            if (ProbabilityService.checkDir(sourcePart.impulseProbabilityVector, sourceDir)) {
                                //if (actSourceDirPotentialProbability > 0) {
                                //if (sourcePart.getParticle().getPartType() == Particle.PartType.Field) {
                                //    transferDirProbability = this.calcActTransferPotentialProbabilityForFieldPart(targetGridNode, sourcePart, actSourceDirPotentialProbability, dir);
                                //} else {
                                    // Particle:
                                    transferDirProbability = actSourceDirPotentialProbability;
                                //}
                                //}  else {
                                //    transferDirProbability = 0;
                                //}

                                // Transfer Probability in this direction available?
                                if (transferDirProbability > 0) {
                                    final int leftDirProbability = actSourceDirPotentialProbability - transferDirProbability;
                                    if (leftDirProbability < 0) {
                                        throw new RuntimeException("leftDirProbability < 0");
                                    }

                                    // Source-Part is ready for transfer?
                                    //if (this.checkProbabilityTransfer(sourcePart, sourceDir)) {
                                    //final Optional<Part> optionalPart = this.searchNextParticlePart(targetGridNode, sourcePart.getParticle(), sourcePart.probabilityVector, transferDirProbability);
                                    this.transferProbabilityToNextByNewPart(sourcePart, targetGridNode, transferDirProbability);
                                    this.transferProbabilityToNextByNewPart(sourcePart, sourceGridNode, leftDirProbability);
                                    //} else {
                                    //    // No transfer, DirProbability stay in Next-SourceGridNode.
                                    //    this.transferProbabilityToNextByNewPart(sourcePart, sourceGridNode, actSourceDirPotentialProbability);
                                    //}
                                } else {
                                    // No transfer, DirProbability stay in Next-SourceGridNode.
                                    this.transferProbabilityToNextByNewPart(sourcePart, sourceGridNode, actSourceDirPotentialProbability);
                                }
                            } else {
                                // No transfer, DirProbability stay in Next-SourceGridNode.
                                this.transferProbabilityToNextByNewPart(sourcePart, sourceGridNode, actSourceDirPotentialProbability);
                            }
                        }
                    });
                }
            }
        }

        //--------------------------------------------------------------------------------------------------------------
        // Transfer left Act DirProbability to Next Probability Parts.
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                gridNode.getPartList(this.actCellArrPos).stream().
                        //filter(sourcePart -> sourcePart.getParticle().getPartType() == Particle.PartType.Life).
                        forEach(sourcePart -> {
                            final int sourcePartProbability;

                            //sourcePartProbability = PartService.calcProbabilitySumAndResetDirProbability(sourcePart);
                            sourcePartProbability = sourcePart.getPotentialProbability();

                            if (sourcePartProbability > 0) {
                                PartService.calcResetDirPotentialProbability(sourcePart);
                                this.transferProbabilityToNextByCopyPart(gridNode, sourcePart, sourcePartProbability);
                            }
                        });
                gridNode.getPartList(this.nextCellArrPos).stream().
                        //filter(sourcePart -> sourcePart.getParticle().getPartType() == Particle.PartType.Life).
                                forEach(sourcePart -> {
                            for (final Cell.Dir dir : Cell.Dir.values()) {
                                if (sourcePart.getPotentialProbabilityByDir(dir) > 0) {
                                    throw new RuntimeException("DirProbability not 0.");
                                    //sourcePart.probabilityVector.setDirProbability(dir, 0);
                                }
                            }
                        });
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }
    private void precalcActGrid() {
        //--------------------------------------------------------------------------------------------------------------
        // Normalize probability:
        this.partCount = 0;

        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                gridNode.getPartList(this.actCellArrPos).stream().
                        forEach(sourcePart -> {
                            final int newSourcePotentialProbability;
                            //final int sourcePotentialProbability = sourcePart.getProbability() * sourcePart.getCount();
                            final int sourcePotentialProbability = sourcePart.getPotentialProbability();

                            // Reduce field probability für every Field-Part.
                            if (sourcePart.getParticle().getPartType() == Particle.PartType.Field) {
                                if (sourcePotentialProbability >= FieldPotentialCutoffValue) {
                                    newSourcePotentialProbability = sourcePotentialProbability - FieldPotentialCutoffValue;
                                } else {
                                    newSourcePotentialProbability = 0;
                                }
                            } else {
                                newSourcePotentialProbability = sourcePotentialProbability;
                            }

                            sourcePart.setPotentialProbability(newSourcePotentialProbability);
                            //sourcePart.setCount(1);

                            //sourcePart.rotationDir = HexMathUtils.calcNextMoveDir(sourcePart.rotationDir);
                            //ProbabilityService.calcNext(sourcePart.probabilityVector);
                        });

                // Group Parts of the same Particle with the same ProbabilityVector.
                final Map<ParticleProbability, List<Part>> uniquePartMap
                        = gridNode.getPartList(this.actCellArrPos).stream().
                        collect(Collectors.groupingBy(part -> new ParticleProbability(part.getParticle(), part.impulseProbabilityVector)));
                final List<Part> mergedPartList = uniquePartMap.values().stream()
                        .map(group -> group.stream().reduce((aPart, bPart) -> {
                            aPart.setPotentialProbability(aPart.getPotentialProbability() + bPart.getPotentialProbability());
                            ProbabilityService.combineCntArr(aPart.impulseProbabilityVector, bPart.impulseProbabilityVector);
                            return aPart;
                        }).get())
                        .collect(Collectors.toList());
                gridNode.getPartList(this.actCellArrPos).clear();
                gridNode.getPartList(this.actCellArrPos).addAll(mergedPartList);
                this.partCount += mergedPartList.size();
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    private void calcActGridNext() {
        //--------------------------------------------------------------------------------------------------------------
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                gridNode.getPartList(this.actCellArrPos).stream().
                        forEach(sourcePart -> {
                            sourcePart.rotationDir = HexMathUtils.calcNextMoveDir(sourcePart.rotationDir);
                            ProbabilityService.calcNext(sourcePart.impulseProbabilityVector, MaxImpulseProb);
                        });
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    /**
     * Distribute Probability to the possible DirProbabilities depending on probabilityVector.
     */
    private void transferPotentialProbabilityToDirPotentialProbabilities(final Part sourcePart) {
        final int sourcePotentialProbability = sourcePart.getPotentialProbability();
        //final int stepLimitSum = sourcePart.impulseProbabilityVector.stepLimitSum;
        //final int limitSum = sourcePart.impulseProbabilityVector.limitSum;
        final int stepLimitCnt = sourcePart.impulseProbabilityVector.limitCnt;

        //if (stepLimitSum > 0) {
        //if (limitSum > 0) {
        if (stepLimitCnt > 0) {
            final int extraPotentialProbability = (sourcePotentialProbability % stepLimitCnt);
            final int transferDirPotentialProbability = (sourcePotentialProbability / stepLimitCnt);

            Cell.Dir lowestLastExtraDir = null;
            if (extraPotentialProbability > 0) {
                final int extraDirPos = Objects.isNull(sourcePart.lastExtraDir) ? 0 : sourcePart.lastExtraDir.ordinal();
                int lowestLastExtraPotentialProbability = Integer.MAX_VALUE;//sourcePotentialProbability;
                boolean foundEqual = false;
                for (int dirPos = 0; dirPos < Cell.Dir.values().length; dirPos++) {
                    final Cell.Dir dir = Cell.Dir.values()[(dirPos + extraDirPos) % Cell.Dir.values().length];

                    if (ProbabilityService.checkDir(sourcePart.impulseProbabilityVector, dir)) {

                        final int dirLastExtraPotentialProbability = sourcePart.impulseProbabilityVector.dirLastExtraPotentialProbabilityArr[dirPos];
                        if ((dirLastExtraPotentialProbability == lowestLastExtraPotentialProbability) &&
                                (dir != sourcePart.lastExtraDir) && (foundEqual == false)) {
                            lowestLastExtraPotentialProbability = dirLastExtraPotentialProbability;
                            lowestLastExtraDir = dir;
                            foundEqual = true;
                        } else {
                        if (dirLastExtraPotentialProbability <= lowestLastExtraPotentialProbability) {
                            lowestLastExtraPotentialProbability = dirLastExtraPotentialProbability;
                            lowestLastExtraDir = dir;
                            foundEqual = true;
                        }
                        }
                    }
                }
                if (Objects.isNull(lowestLastExtraDir)) throw new RuntimeException("no lowestLastExtraDir foundEqual.");
                for (int dirPos = 0; dirPos < Cell.Dir.values().length; dirPos++) {
                    sourcePart.impulseProbabilityVector.dirLastExtraPotentialProbabilityArr[dirPos] -= lowestLastExtraPotentialProbability;
                }
            }
            int dirPotentialProbabilitySum = 0;
            //for (final Cell.Dir dir : Cell.Dir.values()) {
            for (int dirPos = 0; dirPos < Cell.Dir.values().length; dirPos++) {
                final Cell.Dir dir = Cell.Dir.values()[dirPos];
                if (ProbabilityService.checkDir(sourcePart.impulseProbabilityVector, dir)) {
                    final int limit = ProbabilityService.calcProbabilityValue(sourcePart.impulseProbabilityVector, dir, MaxImpulseProb);
                    //final int dirPotentialProbability = (limit * sourcePotentialProbability) / stepLimitSum;
                    //final int dirPotentialProbability = (limit * sourcePotentialProbability) / limitSum;
                    final int dirPotentialProbability;

                    if ((extraPotentialProbability > 0) && (lowestLastExtraDir == dir)) {
                        dirPotentialProbability = transferDirPotentialProbability + extraPotentialProbability;
                        sourcePart.lastExtraDir = dir;
                        sourcePart.impulseProbabilityVector.dirLastExtraPotentialProbabilityArr[dirPos] += extraPotentialProbability;
                    } else {
                        dirPotentialProbability = transferDirPotentialProbability;
                    }

                    dirPotentialProbabilitySum += dirPotentialProbability;
                    sourcePart.setPotentialProbabilityForDir(dir, dirPotentialProbability);
                }
            }
            sourcePart.setPotentialProbability(sourcePotentialProbability - dirPotentialProbabilitySum);
            if (sourcePart.getPotentialProbability() != 0) {
                throw new RuntimeException("PotentialProbability not completely transfered to dirPotentialProbability");
            }
            // TODO add left prob to nest lastExtraDir.
        }
    }

    /**
     * Distribute Probability to the possible DirProbabilities depending on probabilityVector.
     */
    private void transferPotentialProbabilityToDirPotentialProbabilities2(final Part sourcePart) {
        final int sourcePotentialProbability = sourcePart.getPotentialProbability();
        //final int stepLimitSum = sourcePart.impulseProbabilityVector.stepLimitSum;
        //final int limitSum = sourcePart.impulseProbabilityVector.limitSum;
        final int stepLimitCnt = sourcePart.impulseProbabilityVector.limitCnt;

        //if (stepLimitSum > 0) {
        //if (limitSum > 0) {
        if (stepLimitCnt > 0) {
            int extraPotentialProbability = (sourcePotentialProbability % stepLimitCnt);
            final int extraDirPos = Objects.isNull(sourcePart.lastExtraDir) ? 0 : sourcePart.lastExtraDir.ordinal();
            int dirPotentialProbabilitySum = 0;
            //for (final Cell.Dir dir : Cell.Dir.values()) {
            for (int dirPos = 0; dirPos < Cell.Dir.values().length; dirPos++) {
                final Cell.Dir dir = Cell.Dir.values()[(dirPos + extraDirPos) % Cell.Dir.values().length];
                if (ProbabilityService.checkDir(sourcePart.impulseProbabilityVector, dir)) {
                    final int limit = ProbabilityService.calcProbabilityValue(sourcePart.impulseProbabilityVector, dir, MaxImpulseProb);
                    //final int dirPotentialProbability = (limit * sourcePotentialProbability) / stepLimitSum;
                    //final int dirPotentialProbability = (limit * sourcePotentialProbability) / limitSum;
                    final int dirPotentialProbability;

                    if ((extraPotentialProbability > 0) &&
                            (Objects.isNull(sourcePart.lastExtraDir) || (sourcePart.lastExtraDir != dir))) {
                        dirPotentialProbability = (sourcePotentialProbability / stepLimitCnt) + extraPotentialProbability;
                        extraPotentialProbability = 0;
                        sourcePart.lastExtraDir = dir;
                    } else {
                        dirPotentialProbability = (sourcePotentialProbability / stepLimitCnt);
                    }

                    dirPotentialProbabilitySum += dirPotentialProbability;
                    sourcePart.setPotentialProbabilityForDir(dir, dirPotentialProbability);
                }
            }
            sourcePart.setPotentialProbability(sourcePotentialProbability - dirPotentialProbabilitySum);
            if (sourcePart.getPotentialProbability() != 0) throw new RuntimeException("PotentialProbability not completely transfered to dirPotentialProbability");
            // TODO add left prob to nest lastExtraDir.
        }
    }

    private void transferProbabilityToNextByNewPart(final Part sourcePart, final GridNode targetGridNode, final int transferProbability) {
        if (transferProbability > 0) {
            final Optional<Part> optionalNextPart = this.searchNextParticlePart(targetGridNode, sourcePart.getParticle(), sourcePart.impulseProbabilityVector);
            if (optionalNextPart.isPresent()) {
                final Part nextPart = optionalNextPart.get();
                //nextPart.setCount(nextPart.getCount() + 1);
                nextPart.setPotentialProbability(nextPart.getPotentialProbability() + transferProbability);
            } else {
                final Part newNextPart = new Part(sourcePart.getParticle(),
                        //HexMathUtils.calcNextMoveDir(sourcePart.rotationDir),
                        //HexMathUtils.calcNextMoveDir(dir),
                        sourcePart.rotationDir,
                        sourcePart.lastExtraDir,
                        ProbabilityService.createVector(sourcePart.impulseProbabilityVector),
                        transferProbability);
                targetGridNode.addPart(this.nextCellArrPos, newNextPart);
            }
        }
    }

    private void transferProbabilityToNextByCopyPart(final GridNode gridNode, final Part sourcePart, final int sourcePartProbability) {
        //final Optional<Part> optionalPart = this.searchNextParticlePart(gridNode, sourcePart.getParticle(), sourcePartProbability);
        final Optional<Part> optionalPart = this.searchNextParticlePart(gridNode, sourcePart.getParticle(), sourcePart.impulseProbabilityVector);
        if (optionalPart.isPresent()) {
            final Part part = optionalPart.get();
            //part.setProbability(part.getProbability() + sourcePart.getProbability());
            //part.setCount(part.getCount() + 1);
            part.setPotentialProbability(part.getPotentialProbability() + sourcePartProbability);
        } else {
            sourcePart.setPotentialProbability(sourcePartProbability);
            gridNode.addPart(this.nextCellArrPos, sourcePart);
        }
    }

    /**
     * Calculate how much of the Potential of the  Source-Field-Part will be transferred to the Target-Node.
     *
     * a) Potential of the same Field of the same Particle:
     *    A existing Field in the Target-Node (in the transfer-direction?)
     *    is a "pressure" against the flow from source to target.
     *    Only the difference between source to target is transferred.
     * b) Potential of the same Field-Type of other Particles:
     *    ...
     * c) Potential of the opposite Field-Type of other Particles:
     *    ...
     */
    private int calcActTransferPotentialProbabilityForFieldPart(final GridNode targetGridNode, final Part sourceFieldPart, final int sourceDirProbability, final Cell.Dir dir) {
        final int transferPotentialProbability;

        final Particle sourceFieldParticle = sourceFieldPart.getParticle();
        final Particle.PartType sourcePartType = sourceFieldParticle.getPartType();
        final Particle.PartSubType sourcePartSubType = sourceFieldParticle.getPartSubType();

        // a) Potential of the same Field of the same Particle in the Target-Node:
        // Potential in the direction of ???
        //final int targetParticleFieldPotentialProbabilityDirSum = this.calcActTargetFieldPotentialProbabilityDirSumByParticle(targetGridNode,
        //        sourceFieldParticle, sourcePartType, sourcePartSubType, dir);
        // Potential independent of the direction.
        //final int targetParticleFieldPotentialProbabilityDirSum = this.calcActTargetFieldPotentialProbabilitySumByTypesAndParticle(targetGridNode,
        //        sourceFieldParticle, sourcePartType, sourcePartSubType);
        // Do not feel the own field of the particle.
        final int targetParticleFieldPotentialProbabilityDirSum = 0;

        // b) Potential of the same Field-Type of other Particles in the Target-Node:
        //final int targetSameFieldPotentialProbabilitySum = this.calcActTargetSameFieldProbabilitySum(targetGridNode, sourceFieldPart);
        //final int targetSameFieldPotentialProbabilitySum = this.calcActTargetSameFieldProbabilitySumOfOtherParticles(targetGridNode, sourceFieldPart);
        final int targetSameFieldPotentialProbabilitySum = this.calcActTargetFieldPotentialProbabilitySumByTypesAndOtherParticles(targetGridNode,
                sourceFieldParticle, sourcePartType, sourcePartSubType);

        // c) Potential of the opposite Field-Type of other Particles in the Target-Node:
        //final int targetOppositeFieldPotentialProbabilitySum = this.calcTargetOppositeFieldDirProbabilitySum(targetGridNode, sourceFieldPart, dir);
        final int targetOppositeFieldPotentialProbabilitySum = this.calcActTargetFieldProbabilitySumByOppositeTypes(targetGridNode,
                sourcePartType, sourcePartSubType);

        // Add the fields in the target and
        // reduce the Particle-Field, if there is an Opposite-Field.
        final int targetFieldPotentialProbabilitySum =
                Math.max(0, (targetParticleFieldPotentialProbabilityDirSum + targetSameFieldPotentialProbabilitySum) -
                        targetOppositeFieldPotentialProbabilitySum);

        // Target has a Field of the Source-Particle?
        if (targetFieldPotentialProbabilitySum > 0) {
            final int fieldPotentialProbabilityDiff;
            // Source-Field bigger than Target-Field?
            if (sourceDirProbability > targetFieldPotentialProbabilitySum) {
                // Transfer only the difference from Source to Target.
                fieldPotentialProbabilityDiff = sourceDirProbability - targetFieldPotentialProbabilitySum;
            } else {
                // Transfer only the available Field from Source to Target.
                fieldPotentialProbabilityDiff = sourceDirProbability;
            }
            if (fieldPotentialProbabilityDiff > 0) {
                transferPotentialProbability = fieldPotentialProbabilityDiff;
            } else {
                transferPotentialProbability = 0;
            }
        } else {
            // No Field of Source-Particle: Transfer all Probability.
            transferPotentialProbability = sourceDirProbability;
        }
        return transferPotentialProbability;
    }

    private int calcActTargetFieldPotentialProbabilityDirSumByParticle(final GridNode targetGridNode,
                                                                       final Particle sourceFieldParticle, final Particle.PartType sourcePartType, final Particle.PartSubType sourcePartSubType,
                                                                       final Cell.Dir dir) {
        final List<Part> targetFieldPartList = this.searchActParticlePartListByParticleAndTypes(targetGridNode, sourceFieldParticle, sourcePartType, sourcePartSubType);
        final int targetFieldPotentialProbabilityDirSum;
        if (!targetFieldPartList.isEmpty()) {
            targetFieldPotentialProbabilityDirSum = targetFieldPartList.stream().
                    map(targetFieldPart -> targetFieldPart.getPotentialProbabilityByDir(dir)).
                    reduce(0, Integer::sum);
        } else {
            targetFieldPotentialProbabilityDirSum = 0;
        }
        return targetFieldPotentialProbabilityDirSum;
    }

    private int calcActTargetOppositeFieldDirProbabilitySum(final GridNode targetGridNode, final Part sourcePart, final Cell.Dir dir) {
        final Particle.PartType partType = sourcePart.getParticle().getPartType();
        final Particle.PartSubType oppositeSubType = PartService.calcOppositeSubType(sourcePart.getParticle());
        final List<Part> targetOppositeFieldPartList = this.searchActParticlePartListByTypeAndSubType(targetGridNode, partType, oppositeSubType);
        final int targetOppositeFieldDirPotentialProbabilitySum;
        if (!targetOppositeFieldPartList.isEmpty()) {
            targetOppositeFieldDirPotentialProbabilitySum = targetOppositeFieldPartList.stream().
                    map(targetFieldPart -> targetFieldPart.getPotentialProbabilityByDir(dir)).
                    reduce(0, Integer::sum);
        } else {
            targetOppositeFieldDirPotentialProbabilitySum = 0;
        }
        return targetOppositeFieldDirPotentialProbabilitySum;
    }

    private int calcActTargetFieldProbabilitySumByOppositeTypes(final GridNode targetGridNode, final Particle.PartType partType, final Particle.PartSubType partSubType) {
        final Particle.PartSubType oppositeSubType = PartService.calcOppositeSubType(partType, partSubType);
        return this.calcActTargetFieldPotentialProbabilitySumByTypes(targetGridNode, partType, oppositeSubType);
    }

    private int calcActTargetFieldPotentialProbabilitySumByOppositeTypesAndOtherParticles(final GridNode targetGridNode, final Particle excludeParticle, final Particle.PartType partType, final Particle.PartSubType partSubType) {
        final Particle.PartSubType oppositeSubType = PartService.calcOppositeSubType(partType, partSubType);
        return this.calcActTargetFieldPotentialProbabilitySumByTypesAndOtherParticles(targetGridNode, excludeParticle, partType, oppositeSubType);
    }

    private int calcActTargetFieldPotentialProbabilitySumByTypes(final GridNode targetGridNode,
                                                                 final Particle.PartType partType, final Particle.PartSubType sameSubType) {
        final List<Part> targetFieldPartList = this.searchActParticlePartListByTypeAndSubType(targetGridNode, partType, sameSubType);
        final int targetFieldPotentialProbabilitySum;
        if (!targetFieldPartList.isEmpty()) {
            targetFieldPotentialProbabilitySum = targetFieldPartList.stream().
                    map(targetFieldPart -> PartService.calcPotentialProbabilitySum(targetFieldPart)).
                    reduce(0, Integer::sum);
        } else {
            targetFieldPotentialProbabilitySum = 0;
        }
        return targetFieldPotentialProbabilitySum;
    }

    private int calcActTargetFieldPotentialProbabilitySumByTypesAndOtherParticles(final GridNode targetGridNode,
                                                                                  final Particle excludeParticle, final Particle.PartType partType, final Particle.PartSubType sameSubType) {
        final List<Part> targetFieldPartList = this.searchActParticlePartListByTypeAndSubType(targetGridNode, partType, sameSubType);
        final int targetFieldPotentialProbabilitySum;
        if (!targetFieldPartList.isEmpty()) {
            targetFieldPotentialProbabilitySum = targetFieldPartList.stream().
                    filter(part -> part.getParticle() != excludeParticle).
                    map(targetFieldPart -> PartService.calcPotentialProbabilitySum(targetFieldPart)).
                    reduce(0, Integer::sum);
        } else {
            targetFieldPotentialProbabilitySum = 0;
        }
        return targetFieldPotentialProbabilitySum;
    }

    private int calcActTargetFieldPotentialProbabilitySumByTypesAndParticle(final GridNode targetGridNode,
                                                                                  final Particle particle, final Particle.PartType partType, final Particle.PartSubType sameSubType) {
        final List<Part> targetFieldPartList = this.searchActParticlePartListByTypeAndSubType(targetGridNode, partType, sameSubType);
        final int targetFieldPotentialProbabilitySum;
        if (!targetFieldPartList.isEmpty()) {
            targetFieldPotentialProbabilitySum = targetFieldPartList.stream().
                    filter(part -> part.getParticle() == particle).
                    map(targetFieldPart -> PartService.calcPotentialProbabilitySum(targetFieldPart)).
                    reduce(0, Integer::sum);
        } else {
            targetFieldPotentialProbabilitySum = 0;
        }
        return targetFieldPotentialProbabilitySum;
    }

    private void transferRotation() {
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                //final Cell.Dir dir = Cell.Dir.CP; {
                //for (final Cell.Dir dir : new Cell.Dir[] { Cell.Dir.BP, Cell.Dir.BN }) {
                for (final Cell.Dir dir : Cell.Dir.values()) {
                    final GridNode sourceGridNode = this.getNeighbourGridNode(posX, posY, dir);

                    final Cell.Dir sourceDir = DirUtils.calcOppositeDir(dir);
                    sourceGridNode.getPartList(this.actCellArrPos).stream().
                            filter(sourcePart -> sourcePart.getParticle().getPartType() == Particle.PartType.Particle).
                            forEach(sourcePart -> {
                                final int sourceProbability = sourcePart.getPotentialProbability();

                                final int transferProbability;
                                if (this.checkRotationTransfer(sourcePart, sourceDir)) {
                                    //transferProbability = Math.min(sourceProbability, TransProp);
                                    transferProbability = sourceProbability / 2;
                                    //transferProbability = sourceProbability / 6;
                                    //transferProbability = sourceProbability;
                                } else {
                                    transferProbability = 0;
                                }
                                if (transferProbability > 0) {
                                    //final Optional<Part> optionalPart = this.searchNextParticlePart(gridNode, sourcePart.getParticle(), transferProbability);
                                    transferProbabilityToNextByNewPart(sourcePart, gridNode, transferProbability);
                                    sourcePart.setPotentialProbability(sourceProbability - transferProbability);
                                }
                            });
                }
            }
        }
    }

    private boolean checkRotationTransfer(final Part sourcePart, final Cell.Dir sourceDir) {
        return sourceDir == sourcePart.rotationDir;
    }

    private boolean checkProbabilityTransfer(final Part sourcePart, final Cell.Dir sourceDir) {
        //return sourceDir == sourcePart.rotationDir;
        return ProbabilityService.checkDir(sourcePart.impulseProbabilityVector, sourceDir);
    }

    private List<Part> searchActParticlePartListByParticle(final GridNode gridNode, final Particle particle) {
        return GridNodeService.searchParticlePartListByParticle(gridNode, this.actCellArrPos, particle);
    }

    private List<Part> searchActParticlePartListByParticleAndTypes(final GridNode gridNode, final Particle particle,
                                                                   final Particle.PartType partType, final Particle.PartSubType partSubType) {
        return GridNodeService.searchParticlePartListByParticleAndTypes(gridNode, this.actCellArrPos, particle, partType, partSubType);
    }

    private List<Part> searchActParticlePartListByTypeAndSubType(final GridNode gridNode, final Particle.PartType partType, final Particle.PartSubType partSubType) {
        return GridNodeService.searchParticlePartListByTypeAndSubType(gridNode, this.actCellArrPos, partType, partSubType);
    }

    private List<Part> searchNextParticlePartListByTypeAndSubType(final GridNode gridNode, final Particle.PartType partType, final Particle.PartSubType partSubType) {
        return GridNodeService.searchParticlePartListByTypeAndSubType(gridNode, this.nextCellArrPos, partType, partSubType);
    }

    private Optional<Part> searchNextParticlePart(final GridNode gridNode, final Particle particle, final ProbabilityVector probabilityVector) {
        return GridNodeService.searchParticlePart(gridNode, this.nextCellArrPos, particle, probabilityVector);
    }

    private Optional<Part> searchNextParticlePart(final GridNode gridNode, final Particle particle, final ProbabilityVector probabilityVector, final int probability) {
        final List<Part> partList = gridNode.getPartList(this.nextCellArrPos);

        return partList.stream().
                filter(part ->
                    (part.getParticle() == particle) &&
                        ProbabilityService.compare(part.impulseProbabilityVector, probabilityVector) &&
                        (part.getPotentialProbability() == probability)).
                findFirst();
    }

    private Cell.Dir incDir(final Cell.Dir dir) {
        return Cell.Dir.values()[(dir.ordinal() + 1) % Cell.Dir.values().length];
    }

    private Cell.Dir addDir(final Cell.Dir aDir, final Cell.Dir bDir) {
        return Cell.Dir.values()[(aDir.ordinal() + bDir.ordinal()) % Cell.Dir.values().length];
    }

    private GridNode getNeighbourGridNode(final GridNode gridNode, final Cell.Dir dir, final int distance) {
        return getNeighbourGridNode(gridNode.getPosX(), gridNode.getPosY(), dir, distance);
    }

    private GridNode getNeighbourGridNode(final int posX, final int posY, final Cell.Dir dir, final int distance) {
        GridNode retGridNode = this.getGridNode(posX, posY);

        for (int distancePos = 0; distancePos < distance; distancePos++) {
            retGridNode = this.getNeighbourGridNode(retGridNode, dir);
        }
        return retGridNode;
    }

    public GridNode getNeighbourGridNode(final GridNode gridNode, final Cell.Dir dir) {
        return this.getNeighbourGridNode(gridNode.getPosX(), gridNode.getPosY(), dir);
    }

    public Optional<GridNode> getEmptyNeighbourGridNode(final GridNode gridNode, final Cell.Dir dir) {
        final GridNode neighbourGridNode = this.getNeighbourGridNode(gridNode.getPosX(), gridNode.getPosY(), dir);
        if (neighbourGridNode.getPartList(this.actCellArrPos).isEmpty()) {
            return Optional.of(neighbourGridNode);
        } else {
            return Optional.empty();
        }
    }

    private GridNode getNeighbourGridNode(final int posX, final int posY, final Cell.Dir dir) {
        final int rowNo = posY % 2;
        final int[] offsetArr = DirOffsetArr[rowNo][dir.ordinal()];
        final GridNode sourceGridNode =
                this.getGridNode(posX + offsetArr[0], posY + offsetArr[1]);
        return sourceGridNode;
    }

    private void calcNextCellArrPos() {
        if (this.actCellArrPos == 0) {
            this.actCellArrPos = 1;
            this.nextCellArrPos = 0;
        } else {
            this.actCellArrPos = 0;
            this.nextCellArrPos = 1;
        }
    }

    private void clearNextGrid() {
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);
                gridNode.getPartList(this.nextCellArrPos).clear();
            }
        }
    }

    public HexGrid getHexGrid() {
        return this.hexGrid;
    }

    public GridNode retrieveGridNode(final int posX, final int posY) {
        return this.getGridNode(posX, posY);
    }

    public int retrieveStepCount() {
        return this.stepCount;
    }

    public GridNodeArea retrieveActGridNodeArea(final int posX, final int posY, final Cell.Dir dir, final int areaDistance) {
        return this.getGridNode(posX, posY).getGridNodeArea(dir, areaDistance);
    }

    public double retrieveActGridNodePartValue(final int posX, final int posY) {
        double value = 0.0D;
        final GridNode gridNode = this.getGridNode(posX, posY);

        for (final Part part : gridNode.getPartList(this.actCellArrPos)) {
            //value += 1.0D;
            if (part.getParticle().getPartType() == Particle.PartType.Particle) {
                value += part.getPotentialProbability();
            }
        }
        return value;
    }

    public double retrieveActGridNodeFieldValue(final int posX, final int posY, final Particle.PartSubType fieldSubType) {
        double value = 0.0D;
        final GridNode gridNode = this.getGridNode(posX, posY);

        for (final Part part : gridNode.getPartList(this.actCellArrPos)) {
            //value += 1.0D;
            if ((part.getParticle().getPartType() == Particle.PartType.Field) &&
                (part.getParticle().getPartSubType() == fieldSubType)) {
                value += part.getPotentialProbability();
            }
        }
        return value;
    }

    public double retrieveActGridNodeExtraValue(final int posX, final int posY) {
        double value = 0.0D;
        final GridNode gridNode = this.getGridNode(posX, posY);

        return this.getActPartList(gridNode).size();
    }

    public int getNodeCountX() {
        return this.hexGrid.getNodeCountX();
    }

    public int getNodeCountY() {
        return this.hexGrid.getNodeCountY();
    }

    public void addActPart(final GridNode gridNode, final Part part) {
        gridNode.addPart(this.actCellArrPos, part);
    }

    public void addNextPart(final GridNode gridNode, final Part part) {
        gridNode.addPart(this.nextCellArrPos, part);
    }

    public GridNode searchRandomEmptyGridNode(final boolean onTop) {
        GridNode gridNode = null;
        int searchCnt = this.getNodeCountX();
        do {
            final int posX = this.rnd.nextInt(this.getNodeCountX() - 2) + 1;
            final int posY;
            if (onTop) {
                posY = 0;
            } else {
                posY = this.rnd.nextInt((this.getNodeCountY() - 2) / 4) + 1 + ((this.getNodeCountY() / 4) * 3);
            }
            gridNode = this.getGridNode(posX, posY);
            searchCnt--;
        }
        while ((!gridNode.getPartList(this.actCellArrPos).isEmpty()) && (searchCnt > 0));

        return gridNode;
    }

    public int getMaxAreaDistance() {
        return this.maxAreaDistance;
    }

    public List<Part> getActPartList(final GridNode gridNode) {
        return gridNode.getPartList(this.actCellArrPos);
    }

    public long calcPartCount() {
        return this.partCount;
    }
}
