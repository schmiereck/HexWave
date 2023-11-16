package de.schmiereck.hexWave2.service.hexGrid;

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
        // Normalize probability:
        this.partCount = 0;

        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                gridNode.getPartList(this.actCellArrPos).stream().forEach(sourcePart -> {
                    final int newSourceProbability;
                    //final int sourceProbability = sourcePart.getProbability() * sourcePart.getCount();
                    final int sourceProbability = sourcePart.getProbability();

                    // Reduce field probability für every Field-Part.
                    if ((sourceProbability > 0) &&
                            (sourcePart.getParticle().getPartType() == Particle.PartType.Field)) {
                        newSourceProbability = sourceProbability - 1;
                    } else {
                        newSourceProbability = sourceProbability;
                    }

                    sourcePart.setProbability(newSourceProbability);
                    //sourcePart.setCount(1);

                    sourcePart.rotationDir = HexMathUtils.calcNextMoveDir(sourcePart.rotationDir);
                    ProbabilityService.calcNext(sourcePart.probabilityVector);
                });

                // Group Parts of the same Particle with the same ProbabilityVector.
                final Map<ParticleProbability, List<Part>> uniquePartMap
                        = gridNode.getPartList(this.actCellArrPos).stream().
                        collect(Collectors.groupingBy(part -> new ParticleProbability(part.getParticle(), part.probabilityVector)));
                final List<Part> mergedPartList = uniquePartMap.values().stream()
                        .map(group -> group.stream().reduce((aPart, bPart) -> {
                            aPart.setProbability(aPart.getProbability() + bPart.getProbability());
                            return aPart;
                        }).get())
                        .collect(Collectors.toList());
                gridNode.getPartList(this.actCellArrPos).clear();
                gridNode.getPartList(this.actCellArrPos).addAll(mergedPartList);
                this.partCount += mergedPartList.size();
            }
        }

        //--------------------------------------------------------------------------------------------------------------
        // Transfer rotation:
        //transferRotation();

        //--------------------------------------------------------------------------------------------------------------
        // Transfer probability distribution:
        // Distribute Probability to the possible DirProbabilities depending on probabilityVector.
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                gridNode.getPartList(this.actCellArrPos).stream().forEach(sourcePart -> {
                    final int sourceProbability = sourcePart.getProbability();
                    final int stepLimitSum = sourcePart.probabilityVector.stepLimitSum;

                    if (stepLimitSum > 0) {
                        int dirProbabilitySum = 0;
                        for (final Cell.Dir dir : Cell.Dir.values()) {
                            final int dirProbability;
                            if (ProbabilityService.checkDir(sourcePart.probabilityVector, dir)) {
                                final int limit = ProbabilityService.calcAbsLimitValue2(sourcePart.probabilityVector, dir);
                                dirProbability = (limit * sourceProbability) / stepLimitSum;
                            } else {
                                dirProbability = 0;
                            }
                            dirProbabilitySum += dirProbability;
                            sourcePart.probabilityVector.setDirProbability(dir, dirProbability);
                        }
                        sourcePart.setProbability(sourceProbability - dirProbabilitySum);
                    }
                });
            }
        }

        // Transfer DirProbabilities from Neighbours to Probability.
        // Depending on:
        // checkProbabilityTransfer
        //
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                for (final Cell.Dir dir : Cell.Dir.values()) {
                    final GridNode sourceGridNode = this.getNeighbourGridNode(posX, posY, dir);

                    final Cell.Dir sourceDir = DirUtils.calcOppositeDir(dir);
                    sourceGridNode.getPartList(this.actCellArrPos).stream().forEach(sourcePart -> {
                        final int sourceDirProbability = sourcePart.probabilityVector.getDirProbability(sourceDir);
                        final int transferProbability;

                        if (sourceDirProbability > 0) {
                            if (sourcePart.getParticle().getPartType() == Particle.PartType.Field) {
                                final List<Part> fieldPartList = this.searchNextParticlePartList(gridNode, sourcePart.getParticle());
                                if (!fieldPartList.isEmpty()) {
                                    final int fieldDirProbabilitySum = fieldPartList.stream().
                                            map(part -> part.probabilityVector.getDirProbability(dir)).
                                            reduce(0, Integer::sum);

                                    final int fieldDirProbabilityDiff = sourceDirProbability - fieldDirProbabilitySum;
                                    if (fieldDirProbabilityDiff > 0) {
                                        transferProbability = fieldDirProbabilityDiff;
                                    } else {
                                        transferProbability = 0;
                                    }
                                } else {
                                    transferProbability = sourceDirProbability;
                                }
                            } else {
                                transferProbability = sourceDirProbability;
                            }
                        }  else {
                            transferProbability = 0;
                        }

                        if ((transferProbability > 0) &&
                                this.checkProbabilityTransfer(sourcePart, sourceDir)) {
                            //final Optional<Part> optionalPart = this.searchNextParticlePart(gridNode, sourcePart.getParticle(), sourcePart.probabilityVector, transferProbability);
                            final Optional<Part> optionalPart = this.searchNextParticlePart(gridNode, sourcePart.getParticle(), sourcePart.probabilityVector);
                            if (optionalPart.isPresent()) {
                                final Part part = optionalPart.get();
                                //part.setCount(part.getCount() + 1);
                                part.setProbability(part.getProbability() + transferProbability);
                            } else {
                                final Part newPart = new Part(sourcePart.getParticle(),
                                        //HexMathUtils.calcNextMoveDir(sourcePart.rotationDir),
                                        //HexMathUtils.calcNextMoveDir(dir),
                                        sourcePart.rotationDir,
                                        ProbabilityService.createVector(sourcePart.probabilityVector),
                                        transferProbability);
                                gridNode.addPart(this.nextCellArrPos, newPart);
                            }
                            sourcePart.probabilityVector.setDirProbability(sourceDir, sourceDirProbability - transferProbability);
                        }
                    });
                }
            }
        }

        // Transfer Act DirProbability to Next Probability Parts.
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                gridNode.getPartList(this.actCellArrPos).stream().
                        //filter(sourcePart -> sourcePart.getParticle().getPartType() == Particle.PartType.Life).
                        forEach(sourcePart -> {
                            int sourcePartProbability = sourcePart.getProbability();
                            for (final Cell.Dir dir : Cell.Dir.values()) {
                                sourcePartProbability += sourcePart.probabilityVector.getDirProbability(dir);
                                sourcePart.probabilityVector.setDirProbability(dir, 0);
                            }
                            if (sourcePartProbability > 0) {
                                //final Optional<Part> optionalPart = this.searchNextParticlePart(gridNode, sourcePart.getParticle(), sourcePartProbability);
                                final Optional<Part> optionalPart = this.searchNextParticlePart(gridNode, sourcePart.getParticle(), sourcePart.probabilityVector);
                                if (optionalPart.isPresent()) {
                                    final Part part = optionalPart.get();
                                    //part.setProbability(part.getProbability() + sourcePart.getProbability());
                                    //part.setCount(part.getCount() + 1);
                                    part.setProbability(part.getProbability() + sourcePartProbability);
                                } else {
                                    sourcePart.setProbability(sourcePartProbability);
                                    gridNode.addPart(this.nextCellArrPos, sourcePart);
                                }
                            }
                        });
                gridNode.getPartList(this.nextCellArrPos).stream().
                        //filter(sourcePart -> sourcePart.getParticle().getPartType() == Particle.PartType.Life).
                                forEach(sourcePart -> {
                            for (final Cell.Dir dir : Cell.Dir.values()) {
                                if (sourcePart.probabilityVector.getDirProbability(dir) > 0) {
                                    throw new RuntimeException("DirProbability.");
                                    //sourcePart.probabilityVector.setDirProbability(dir, 0);
                                }
                            }
                        });
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        // Add Field for each Life-Part
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);
                    final List<Part> newFieldList = new ArrayList<>();

                    gridNode.getPartList(this.nextCellArrPos).stream().
                        filter(sourcePart -> sourcePart.getParticle().getPartType() == Particle.PartType.Particle).
                        forEach(sourcePart -> {
                            if (first) {
                            final Particle fieldParticle = sourcePart.getParticle().getFieldParticle();
                            for (final Cell.Dir dir : Cell.Dir.values()) {
                                final int apPerc = 100;
                                final int bpPerc = 100;
                                final int cpPerc = 100;
                                final int anPerc = 100;
                                final int bnPerc = 100;
                                final int cnPerc = 100;

                                final Part fieldPart = new Part(fieldParticle,
                                        dir,
                                        ProbabilityService.createVector(apPerc, bpPerc, cpPerc, anPerc, bnPerc, cnPerc),
                                        //ProbabilityService.createFieldVector(dir),

                                        //sourcePart.getProbability()
                                        //1
                                        //6*6*6
                                        6*6 +
                                        ((6*6 * sourcePart.getProbability()) / MainConfig3.InitialBallPartProbability)
                                );

                                newFieldList.add(fieldPart);
                            }
                                //first = false;
                            }
                        });
                gridNode.addPartList(this.nextCellArrPos, newFieldList);
            }
        }
        //--------------------------------------------------------------------------------------------------------------
    }
    static boolean first = true;

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
                                final int sourceProbability = sourcePart.getProbability();

                                final int transferProbability;
                                if (checkRotationTransfer(sourcePart, sourceDir)) {
                                    //transferProbability = Math.min(sourceProbability, TransProp);
                                    transferProbability = sourceProbability / 2;
                                    //transferProbability = sourceProbability / 6;
                                    //transferProbability = sourceProbability;
                                } else {
                                    transferProbability = 0;
                                }
                                if (transferProbability > 0) {
                                    //final Optional<Part> optionalPart = this.searchNextParticlePart(gridNode, sourcePart.getParticle(), transferProbability);
                                    final Optional<Part> optionalPart = this.searchNextParticlePart(gridNode, sourcePart.getParticle(), sourcePart.probabilityVector);
                                    if (optionalPart.isPresent()) {
                                        final Part part = optionalPart.get();
                                        //part.setProbability(part.getProbability() + transferProbability);
                                        //part.setCount(part.getCount() + 1);
                                        part.setProbability(part.getProbability() + transferProbability);
                                    } else {
                                        final Part newPart = new Part(sourcePart.getParticle(),
                                                sourcePart.rotationDir,
                                                //HexMathUtils.calcNextMoveDir(sourcePart.rotationDir),
                                                //HexMathUtils.calcNextMoveDir(sourceDir),
                                                //sourceDir,
                                                //dir,
                                                ProbabilityService.createVector(sourcePart.probabilityVector),
                                                transferProbability);
                                        gridNode.addPart(this.nextCellArrPos, newPart);
                                    }
                                    sourcePart.setProbability(sourceProbability - transferProbability);
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
        return ProbabilityService.checkDir(sourcePart.probabilityVector, sourceDir);
    }

    private Optional<Part> searchNextParticlePart(final GridNode gridNode, final Particle particle, final int probability) {
        final List<Part> partList = gridNode.getPartList(this.nextCellArrPos);
        return partList.stream().filter(part -> (part.getParticle() == particle) && (part.getProbability() == probability)). findFirst();
    }

    private List<Part> searchNextParticlePartList(final GridNode gridNode, final Particle particle) {
        final List<Part> partList = gridNode.getPartList(this.nextCellArrPos);

        return partList.stream().
                filter(part ->
                    (part.getParticle() == particle)).
                toList();
    }

    private Optional<Part> searchNextParticlePart(final GridNode gridNode, final Particle particle, final ProbabilityVector probabilityVector) {
        final List<Part> partList = gridNode.getPartList(this.nextCellArrPos);

        return partList.stream().
                filter(part ->
                    (part.getParticle() == particle) &&
                        ProbabilityService.compare(part.probabilityVector, probabilityVector)).
                findFirst();
    }

    private Optional<Part> searchNextParticlePart(final GridNode gridNode, final Particle particle, final ProbabilityVector probabilityVector, final int probability) {
        final List<Part> partList = gridNode.getPartList(this.nextCellArrPos);

        return partList.stream().
                filter(part ->
                    (part.getParticle() == particle) &&
                        ProbabilityService.compare(part.probabilityVector, probabilityVector) &&
                        (part.getProbability() == probability)).
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
                value += part.getProbability();
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
                value += part.getProbability();
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
