package de.schmiereck.hexWave.service.hexGrid;

import static de.schmiereck.hexWave.utils.DirUtils.calcOppositeDir;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.math.NumService;
import de.schmiereck.hexWave.service.life.LifeService;
import de.schmiereck.hexWave.utils.DirUtils;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
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

    enum FieldCalcType {
        Nothing,
        Crumble,
        Anti
    };
    private FieldCalcType fieldCalcType = FieldCalcType.Anti;

    private HexGrid hexGrid;

    private int stepCount = 0;
    private int maxAreaDistance;

    private final Random rnd = new Random();

    public HexGridService() {
    }

    public void initialize(final int sizeX, final int sizeY, final int maxAreaDistance) {
        this.maxAreaDistance = maxAreaDistance;

        this.hexGrid = new HexGrid(sizeX, sizeY, this.getMaxAreaDistance());

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

    public GridNode movePart(final GridNode gridNode, final Part part, final Cell.Dir dir) {
        gridNode.removePart(part);
        final GridNode newGridNode = this.getNeighbourGridNode(gridNode, dir);
        newGridNode.addPart(part);
        return newGridNode;
    }

    public void calcNext() {
        this.calcGrid();

        //this.calcNextCellArrPos();
        //this.clearNextGrid();

        this.stepCount++;
    }

    private void calcGrid() {
        /*
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);
                for (final Cell.Dir dir : Cell.Dir.values()) {
                    final GridNode sourceGridNode = this.getNeighbourGridNode(posX, posY, dir);

                    final Cell.Dir sourceDir = calcOppositeDir(dir);
                    sourceGridNode.getPartList().stream().forEach(sourcePart -> {
                    });
                }
            }
        }
        */
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);
                final int finalPosX = posX;

                //for (final FieldTypeEnum fieldTypeEnum : FieldTypeEnum.values()) {
                //    final FieldType fieldType = this.getFieldType(fieldTypeEnum);
                //}

                populatePartFieldToHigherAreas(gridNode, fieldCalcType, this.getMaxAreaDistance());
                populateAntiPartFieldToHigherAreas(gridNode, fieldCalcType, this.getMaxAreaDistance());
            }
        }

        if (fieldCalcType == FieldCalcType.Anti) {
            for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
                for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                    final GridNode gridNode = this.getGridNode(posX, posY);

                    gridNode.getGridNodeAreaRefList().stream().
                            filter(gridNodeAreaRef -> !gridNodeAreaRef.getGridNodeArea().getPartFieldList().isEmpty()).
                            forEach(gridNodeAreaRef -> {
                                final GridNodeArea gridNodeArea = gridNodeAreaRef.getGridNodeArea();
                                // Hit one of the PartFields of this Grid-Node a Part?
                                gridNodeArea.getPartFieldList().stream().
                                        forEach(partField -> {
                                            if (partField.getFieldType().getUseRefection()) {
                                                gridNode.getPartList().stream().
                                                        filter(part -> partField.getPart() != part).
                                                        forEach(part -> {
                                                            sendAntiPartField(gridNode, gridNodeAreaRef, gridNodeArea, partField, partField.getPart());
                                                        });
                                            }
                                        });
                            });
                }
            }
        }
    }

    private void sendAntiPartField(final GridNode gridNode, final GridNodeAreaRef gridNodeAreaRef,
                                   final GridNodeArea gridNodeArea, final PartField parentPartField, final Part part) {
        final Cell.Dir dir = gridNodeArea.getDir();
        //final Cell.Dir oppositeDir = DirUtils.calcOppositeDir();
        final GridNodeArea lastGridNodeArea = gridNode.getGridNodeArea(dir, 0);
        lastGridNodeArea.addAntiPartField(new PartField(parentPartField, parentPartField.getFieldType(),
                parentPartField.getParentAreaDistance() + gridNodeArea.getAreaDistance(), parentPartField.getValue()));
    }

    private static void populatePartFieldToHigherAreas(final GridNode gridNode, final FieldCalcType fieldCalcType, final int maxAreaDistance) {
        for (final Cell.Dir dir : Cell.Dir.values()) {
            final GridNodeArea lastGridNodeArea = gridNode.getGridNodeArea(dir, maxAreaDistance);

            // Clear the highest Part-Field-List and use it as a new lowest Part-Field-List.

            List<PartField> lastPartFieldList = lastGridNodeArea.getPartFieldList();
            lastPartFieldList.clear();

            // Populate the Part-Field to higher areas (and higher distances).

            for (int areaDistance = 0; areaDistance < GridNodeArea.calcGridNodeArrSizeForMaxAreaDistance(maxAreaDistance); areaDistance++) {
                final GridNodeArea gridNodeArea = gridNode.getGridNodeArea(dir, areaDistance);
                final int finalAreaDistance = areaDistance;

                lastPartFieldList = lastPartFieldList.stream().
                        filter(partField -> (partField.getParentAreaDistance() + finalAreaDistance) <= partField.getFieldType().getMaxAreaDistance()).
                        collect(Collectors.toList());

                switch (fieldCalcType) {
                    case Crumble -> lastPartFieldList = crumbleFieldsOnParts(gridNode, dir, lastPartFieldList, areaDistance, gridNodeArea);
                    //case Anti -> lastPartFieldList = antiFieldsOnParts(gridNode, dir, lastPartFieldList, areaDistance, gridNodeArea);
                }
                // Add populated Part-Field to each Grid-Node/ Part.
                //gridNodeArea.getPartList().stream().forEach(part -> {
                //});

                lastPartFieldList = gridNodeArea.setPartFieldList(lastPartFieldList);
            }
        }
    }

    private static void populateAntiPartFieldToHigherAreas(final GridNode gridNode, final FieldCalcType fieldCalcType, final int maxAreaDistance) {
        for (final Cell.Dir dir : Cell.Dir.values()) {
            final GridNodeArea lastGridNodeArea = gridNode.getGridNodeArea(dir, maxAreaDistance);

            // Clear the highest Part-Field-List and use it as a new lowest Part-Field-List.

            List<PartField> lastPartFieldList = lastGridNodeArea.getAntiPartFieldList();
            lastPartFieldList.clear();

            // Populate the Part-Field to higher areas (and higher distances).

            for (int areaDistance = 0; areaDistance < GridNodeArea.calcGridNodeArrSizeForMaxAreaDistance(maxAreaDistance); areaDistance++) {
                final GridNodeArea gridNodeArea = gridNode.getGridNodeArea(dir, areaDistance);
                final int finalAreaDistance = areaDistance;

                lastPartFieldList = lastPartFieldList.stream().
                        filter(partField -> (partField.getParentAreaDistance() + finalAreaDistance) <= partField.getFieldType().getMaxAreaDistance()).
                        collect(Collectors.toList());

                lastPartFieldList = gridNodeArea.setAntiPartFieldList(lastPartFieldList);
            }
        }
    }

    @NotNull
    private static List<PartField> antiFieldsOnParts(final GridNode gridNode, final Cell.Dir dir,
                                                     final List<PartField> lastPartFieldList, final int areaDistance,
                                                     final GridNodeArea gridNodeArea) {
        // If there is a (blocking) Part in the area,
        // this part sends an Anti-Field in the direction of the incoming Field.
        if (areaDistance > 0) {
            final List<Part> areaPartList = gridNodeArea.getPartList();
            if (!areaPartList.isEmpty()) {
                areaPartList.stream().forEach(part -> {
                    //crumbleFields(gridNode, gridNodeArea, areaDistance, lastPartFieldList, dir, part);
                });
            }
        }

        return lastPartFieldList;
    }

    @NotNull
    private static List<PartField> crumbleFieldsOnParts(final GridNode gridNode, final Cell.Dir dir,
                                                        final List<PartField> lastPartFieldList, final int areaDistance,
                                                        final GridNodeArea gridNodeArea) {
        // If there is a (blocking) Part in the area,
        // then the field crumbles into small pieces and the part blocks the field in one direction.
        if (areaDistance > 0) {
            final List<Part> areaPartList = gridNodeArea.getPartList();
            if (!areaPartList.isEmpty()) {
                areaPartList.stream().forEach(part -> {
                    crumbleFields(gridNode, gridNodeArea, areaDistance, lastPartFieldList, dir, part);
                });
            }
        }

        final List<PartField> filteredLastPartFieldList = lastPartFieldList.stream().
                filter(lastPartField ->
                    (lastPartField.getParentAreaDistance() + areaDistance) <= lastPartField.getFieldType().getMaxAreaDistance()
                ).collect(Collectors.toList());

        return filteredLastPartFieldList;
    }

    private static void crumbleFields(final GridNode sourceGridNode, final GridNodeArea gridNodeArea, final int areaDistance,
                                      final List<PartField> lastPartFieldList, final Cell.Dir dir, final Part part) {
        final List<PartField> partFieldList =
                lastPartFieldList.stream().
                filter(partField -> partField.getPart() == part).
                collect(Collectors.toList());

        lastPartFieldList.stream().
                filter(partField -> partField.getPart() != part).
                forEach(partField -> {
                    // solution: do not split the area, but send a anti field in the direction (for shadow).

                    // easy solution: split the whole area into area distance 1 pieces.
                    gridNodeArea.forEarchGridNode(baseGridNode -> {
                        final GridNodeArea baseGridNodeArea = baseGridNode.getGridNodeArea(dir, 0);
                        if (baseGridNodeArea.getPartList().isEmpty()) {
                            baseGridNodeArea.addPartField(new PartField(partField, partField.getFieldType(), areaDistance, partField.getValue()));
                        }
                });

            // TODO complex solution: split area in one source pice and the front area into distance 1 pieces.
        });

        lastPartFieldList.clear();
        lastPartFieldList.addAll(partFieldList);
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

    private GridNode getNeighbourGridNode(final int posX, final int posY, final Cell.Dir dir) {
        final int rowNo = posY % 2;
        final int[] offsetArr = DirOffsetArr[rowNo][dir.ordinal()];
        final GridNode sourceGridNode =
                this.getGridNode(posX + offsetArr[0], posY + offsetArr[1]);
        return sourceGridNode;
    }

    private void clearNextGrid() {
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);
                gridNode.getPartList().clear();
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

        for (final Part part : gridNode.getPartList()) {
            value += 1.0D;
        }
        return value;
    }

    public double retrieveActGridNodePartFieldValue(final int posX, final int posY, final FieldType filterFieldType) {
        double value = 0.0D;
        final GridNode gridNode = this.getGridNode(posX, posY);

        for (final GridNodeAreaRef gridNodeAreaRef : gridNode.getGridNodeAreaRefList()) {
            final GridNodeArea gridNodeArea = gridNodeAreaRef.getGridNodeArea();
            final double refValue = gridNodeAreaRef.getValue();
            for (final PartField gridNodeAreaPartField : gridNodeArea.getPartFieldList()) {
                final int parentAreaDistance = gridNodeAreaPartField.getParentAreaDistance() + 1;
                if (gridNodeAreaPartField.getFieldType() == filterFieldType) {
                    value += (refValue / parentAreaDistance);
                    //value += 1;//refValue / gridNodeAreaPartField.getParentAreaDistance();
                }
            }
            for (final PartField antiPartField : gridNodeArea.getAntiPartFieldList()) {
                if (antiPartField.getFieldType() == filterFieldType) {
                    value -= refValue / GridNodeArea.calcGridNodeSizeForAreaDistance(antiPartField.getParentAreaDistance());
                }
            }
        }
        return value;
    }

    public double retrieveActGridNodeExtraValue(final int posX, final int posY) {
        double value = 0.0D;
        final GridNode gridNode = this.getGridNode(posX, posY);

        return this.getPartList(gridNode).size();
    }

    public int getNodeCountX() {
        return this.hexGrid.getNodeCountX();
    }

    public int getNodeCountY() {
        return this.hexGrid.getNodeCountY();
    }

    public void removePart(final GridNode gridNode, final Part part) {
        gridNode.removePart(part);
    }

    public void addPart(final GridNode gridNode, final Part part) {
        gridNode.addPart(part);
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
        while ((!gridNode.getPartList().isEmpty()) && (searchCnt > 0));

        return gridNode;
    }

    public int getMaxAreaDistance() {
        return this.maxAreaDistance;
    }

    public List<Part> getPartList(GridNode gridNode) {
        return gridNode.getPartList();
    }

}
