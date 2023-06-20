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

    private int cellArrPos = 0;
    private int stepCount = 0;

    public enum FieldTypeEnum {
        Part(0),
        PartPull(1),
        PartPush(2),
        Sun(3);

        public int no;

        FieldTypeEnum(final int no) {
            this.no = no;
        }
    }
    private FieldType[] fieldTypeArr = new FieldType[FieldTypeEnum.values().length];
    private final int maxAreaDistance;

    private final Random rnd = new Random();

    public HexGridService() {
        this.fieldTypeArr[FieldTypeEnum.Part.no] = new FieldType(5, true);    // Part
        this.fieldTypeArr[FieldTypeEnum.PartPull.no] = new FieldType(3, false);
        this.fieldTypeArr[FieldTypeEnum.PartPush.no] = new FieldType(5, false);
        this.fieldTypeArr[FieldTypeEnum.Sun.no] = new FieldType(0, true);    // Sun
        this.maxAreaDistance = this.fieldTypeArr[FieldTypeEnum.Part.no].getMaxAreaDistance();
    }

    public FieldType getFieldType(final FieldTypeEnum fieldTypeEnum) {
        return this.fieldTypeArr[fieldTypeEnum.no];
    }

    public int getMaxAreaDistance() {
        return this.maxAreaDistance;
    }

    public void initialize(final int sizeX, final int sizeY) {
        this.hexGrid = new HexGrid(sizeX, sizeY, this.getMaxAreaDistance());

        // Left-/ Right-Walls.
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            final GridNode leftGridNode = this.getGridNode(0, posY);
            leftGridNode.addPart(0, new Part(Part.PartType.Wall, this.getFieldType(FieldTypeEnum.Part), MainConfig.InitialWallPartEnergy, false, 0));

            final GridNode rightGridNode = this.getGridNode(this.hexGrid.getNodeCountX() - 1, posY);
            rightGridNode.addPart(0, new Part(Part.PartType.Wall, this.getFieldType(FieldTypeEnum.Part), MainConfig.InitialWallPartEnergy, false, 0));
        }
        // Bottom-Wall.
        for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
            final GridNode leftGridNode = this.getGridNode(posX, this.hexGrid.getNodeCountY() - 1);
            leftGridNode.addPart(0, new Part(Part.PartType.Wall, this.getFieldType(FieldTypeEnum.Part), MainConfig.InitialWallPartEnergy, false, 0));
        }

        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);

                for (final Cell.Dir dir : Cell.Dir.values()) {
                    final Cell.Dir leftDir = DirUtils.calcDirByRotOffset(dir, -2);
                    final Cell.Dir rightDir = DirUtils.calcDirByRotOffset(dir, 2);

                    // Iterator all Areas.
                    for (int areaDistance = 0; areaDistance < getMaxAreaDistance(); areaDistance++) {
                        //final GridNode neighbourGridNode = this.getNeighbourGridNode(posX, posY, dir, areaDistance);
                        final GridNode neighbourGridNode = this.getGridNode(posX, posY);

                        //final int startAreaDistancePos = 0;
                        final int startAreaDistancePos = areaDistance + 1;
                        for (int areaDistancePos = startAreaDistancePos; areaDistancePos <= (areaDistance + 1); areaDistancePos++) {
                            //DirUtils.calcOppositeDir(dir)
                            final GridNode neighbourareaDistanceGridNode = this.getNeighbourGridNode(neighbourGridNode, dir, areaDistancePos);
                            gridNode.setGridNodeAreaArr(dir, areaDistance, areaDistancePos, 0, neighbourareaDistanceGridNode, this.getMaxAreaDistance());

                            for (int gridNodePos = 1; gridNodePos <= areaDistancePos; gridNodePos++) {
                                final GridNode leftNeighbourGridNode = this.getNeighbourGridNode(neighbourareaDistanceGridNode, leftDir, gridNodePos);
                                final GridNode rightNeighbourGridNode = this.getNeighbourGridNode(neighbourareaDistanceGridNode, rightDir, gridNodePos);

                                // 1: (1, 2), 2: (3, 4), 3: (5, 6), ...
                                gridNode.setGridNodeAreaArr(dir, areaDistance, areaDistancePos, gridNodePos * 2 - 1, leftNeighbourGridNode, this.getMaxAreaDistance());
                                gridNode.setGridNodeAreaArr(dir, areaDistance, areaDistancePos, gridNodePos * 2, rightNeighbourGridNode, this.getMaxAreaDistance());
                            }
                        }
                    }
                }
            }
        }
    }

    public GridNode getGridNode(int posX, int posY) {
        return this.hexGrid.getGridNode(posX, posY);
    }

    public GridNode movePart(final GridNode gridNode, final Part part, final Cell.Dir dir) {
        gridNode.removePart(this.getActCellArrPos(), part);
        final GridNode newGridNode = this.getNeighbourGridNode(gridNode, dir);
        newGridNode.addPart(this.getActCellArrPos(), part);
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
                    sourceGridNode.getPartList(this.getActCellArrPos()).stream().forEach(sourcePart -> {
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

                gridNode.getPartList(this.getActCellArrPos()).stream().forEach(part -> {
                    sendPartField(gridNode, part);

                    // TODO Wenn Pull-Field output gesetzt, dann Feld aussenden.

                    if (MainConfig.useWallPushField)// && finalPosX == 32)
                    if (part.getPartType() == Part.PartType.Wall) {
                        final PartField pushPartField = new PartField(part, this.fieldTypeArr[FieldTypeEnum.PartPush.no]);
                        {
                            final GridNodeArea lastGridNodeArea = gridNode.getGridNodeArea(Cell.Dir.BN, 0);
                            lastGridNodeArea.addPartField(pushPartField);
                        }
                        {
                            final GridNodeArea lastGridNodeArea = gridNode.getGridNodeArea(Cell.Dir.CP, 0);
                            lastGridNodeArea.addPartField(pushPartField);
                        }
                    }
                });
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
                                                gridNode.getPartList(this.getActCellArrPos()).stream().
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
                                   final GridNodeArea gridNodeArea, final PartField partField, final Part part) {
        final Cell.Dir dir = gridNodeArea.getDir();
        //final Cell.Dir oppositeDir = DirUtils.calcOppositeDir();
        final GridNodeArea lastGridNodeArea = gridNode.getGridNodeArea(dir, 0);
        lastGridNodeArea.addAntiPartField(new PartField(partField, partField.getFieldType(),
                partField.getParentAreaDistance() + gridNodeArea.getAreaDistance()));
    }

    private static void populatePartFieldToHigherAreas(final GridNode gridNode, final FieldCalcType fieldCalcType, final int maxAreaDistance) {
        for (final Cell.Dir dir : Cell.Dir.values()) {
            final GridNodeArea lastGridNodeArea = gridNode.getGridNodeArea(dir, maxAreaDistance - 1);

            // Clear the highest Part-Field-List and use it as a new lowest Part-Field-List.

            List<PartField> lastPartFieldList = lastGridNodeArea.getPartFieldList();
            lastPartFieldList.clear();

            // Populate the Part-Field to higher areas (and higher distances).

            for (int areaDistance = 0; areaDistance < maxAreaDistance; areaDistance++) {
                final GridNodeArea gridNodeArea = gridNode.getGridNodeArea(dir, areaDistance);
                final int finalAreaDistance = areaDistance;

                lastPartFieldList = lastPartFieldList.stream().
                        filter(partField -> (partField.getParentAreaDistance() + finalAreaDistance) < partField.getFieldType().getMaxAreaDistance()).
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
            final GridNodeArea lastGridNodeArea = gridNode.getGridNodeArea(dir, maxAreaDistance - 1);

            // Clear the highest Part-Field-List and use it as a new lowest Part-Field-List.

            List<PartField> lastPartFieldList = lastGridNodeArea.getAntiPartFieldList();
            lastPartFieldList.clear();

            // Populate the Part-Field to higher areas (and higher distances).

            for (int areaDistance = 0; areaDistance < maxAreaDistance; areaDistance++) {
                final GridNodeArea gridNodeArea = gridNode.getGridNodeArea(dir, areaDistance);
                final int finalAreaDistance = areaDistance;

                lastPartFieldList = lastPartFieldList.stream().
                        filter(partField -> (partField.getParentAreaDistance() + finalAreaDistance) < partField.getFieldType().getMaxAreaDistance()).
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
                    (lastPartField.getParentAreaDistance() + areaDistance) < lastPartField.getFieldType().getMaxAreaDistance()
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
                            baseGridNodeArea.addPartField(new PartField(partField, partField.getFieldType(), areaDistance));
                        }
                });

            // TODO complex solution: split area in one source pice and the front area into distance 1 pieces.
        });

        lastPartFieldList.clear();
        lastPartFieldList.addAll(partFieldList);
    }

    private static void sendPartField(final GridNode gridNode, final Part part) {
        final PartField partField = part.getPartField();
        if (Objects.nonNull(partField)) {
            for (final Cell.Dir dir : Cell.Dir.values()) {
                final GridNodeArea lastGridNodeArea = gridNode.getGridNodeArea(dir, 0);
                lastGridNodeArea.addPartField(partField);
            }
        }
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
                gridNode.getPartList(this.getNextCellArrPos()).clear();
            }
        }
    }

    public HexGrid getHexGrid() {
        return this.hexGrid;
    }

    public GridNode retrieveGridNode(final int posX, final int posY) {
        return this.getGridNode(posX, posY);
    }

    public int getActCellArrPos() {
        return this.cellArrPos;
    }

    private int getNextCellArrPos() {
        return this.cellArrPos == 0 ? 1 : 0;
    }

    private void calcNextCellArrPos() {
        this.cellArrPos = this.getNextCellArrPos();
    }

    public int retrieveStepCount() {
        return this.stepCount;
    }

    /** fiona
     *  0   1   2   3   4   5   6   7       8       9       10      11      12      13
     *  1   3   9   27  81  243 729 2.187   6.561   19.683  59.049  177.147 53.441  1.594.323
     *  1   3   6   17  27  54  105 186     316     511     808     1.254   1.884   2.741
     */
    public long retrievePartCount() {
        long partCount = 0;
        for (int posY = 0; posY < this.hexGrid.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.hexGrid.getNodeCountX(); posX++) {
                final GridNode gridNode = this.getGridNode(posX, posY);
                partCount += gridNode.getPartList(this.getActCellArrPos()).size();
            }
        }
        return partCount;
    }

    public GridNodeArea retrieveActGridNodeArea(final int posX, final int posY, final Cell.Dir dir, final int areaDistance) {
        return this.getGridNode(posX, posY).getGridNodeArea(dir, areaDistance);
    }

    public double retrieveActGridNodePartValue(final int posX, final int posY) {
        double value = 0.0D;
        final GridNode gridNode = this.getGridNode(posX, posY);

        for (final Part part : gridNode.getPartList(this.getActCellArrPos())) {
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
                if (gridNodeAreaPartField.getFieldType() == filterFieldType) {
                    value += refValue / gridNodeAreaPartField.getParentAreaDistance();
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

    public int getNodeCountX() {
        return this.hexGrid.getNodeCountX();
    }

    public int getNodeCountY() {
        return this.hexGrid.getNodeCountY();
    }

    public void removePart(final GridNode gridNode, final Part part) {
        gridNode.removePart(this.getActCellArrPos(), part);
    }

    public void addPart(final GridNode gridNode, final Part part) {
        gridNode.addPart(this.getActCellArrPos(), part);
    }

    public GridNode searchRandomEmptyGridNode(final boolean onTop) {
        GridNode gridNode;

        do {
            final int posX = this.rnd.nextInt(this.getNodeCountX() - 2) + 1;
            final int posY;
            if (onTop) {
                posY = 0;
            } else {
                posY = this.rnd.nextInt(this.getNodeCountY() - 2) + 1;
            }
            gridNode = this.getGridNode(posX, posY);
        }
        while (!gridNode.getPartList(0).isEmpty());

        return gridNode;
    }
}
