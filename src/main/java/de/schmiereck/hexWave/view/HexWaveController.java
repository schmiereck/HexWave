package de.schmiereck.hexWave.view;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.service.genom.Genom;
import de.schmiereck.hexWave.service.hexGrid.FieldType;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.service.life.FieldTypeService;
import de.schmiereck.hexWave.service.life.LifePart;
import de.schmiereck.hexWave.service.life.LifeService;
import de.schmiereck.hexWave.service.hexGrid.HexGrid;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.utils.MathUtils;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HexWaveController implements Initializable
{
    @FXML
    private Label counterText;

    @FXML
    private BorderPane mainBoderPane;

    @FXML
    private Pane mainPane;

    @Autowired
    private HexGridService hexGridService;

    @Autowired
    private LifeService lifeService;

    @Autowired
    private FieldTypeService fieldTypeService;

    private GridModel gridModel = new GridModel();

    private AnimationTimer animationTimer;

    @FXML
    public void initialize() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        MainConfig.initConfig(MainConfig.ConfigEnum.LifeEnvironment);
        //MainConfig.initConfig(MainConfig.ConfigEnum.JumpingBall);
        //MainConfig.initConfig(MainConfig.ConfigEnum.BouncingBall);
        //MainConfig.initConfig(MainConfig.ConfigEnum.ShowFields);
        //MainConfig.initConfig(MainConfig.ConfigEnum.OnlySun);
        //MainConfig.initConfig(MainConfig.ConfigEnum.NoMoves);

        this.mainPane.setStyle("-fx-background-color: black;");

        final int maxAreaDistance = this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Part1).getMaxAreaDistance();

        //this.hexGridService.initialize(2, 1);
        this.hexGridService.initialize(10, 3, maxAreaDistance);
        this.lifeService.initializeWalls();
        this.lifeService.initialize(MainConfig.useLifeParts ? MainConfig.lifePartsCount : 0);
        if (MainConfig.useBall) this.lifeService.initializeBall(MainConfig.config == MainConfig.ConfigEnum.BouncingBall);
        if (MainConfig.useShowFields) this.lifeService.initializeShowFields();

        final HexGrid hexGrid = this.hexGridService.getHexGrid();

        this.gridModel.init(hexGrid.getNodeCountX(), hexGrid.getNodeCountY());

        for (int posY = 0; posY < this.gridModel.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.gridModel.getNodeCountX(); posX++) {
                final GridCellModel gridCellModel = this.gridModel.getGridCellModel(posX, posY);
                final Circle gridNodeCircle = new Circle(1.0D, Color.DARKGRAY);
                gridNodeCircle.setCenterX(gridCellModel.getScreenPosX());
                gridNodeCircle.setCenterY(gridCellModel.getScreenPosY());
                //gridNodeCircle.relocate(gridNode.getScreenPosX(), gridNode.getScreenPosY());
                this.mainPane.getChildren().add(gridNodeCircle);

                this.gridModel.setShape(posX, posY, gridNodeCircle);
            }
        }

        for (int posY = 0; posY < this.gridModel.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.gridModel.getNodeCountX(); posX++) {
                final GridCellModel gridCellModel = this.gridModel.getGridCellModel(posX, posY);
                this.gridModel.setShape2(posX, posY, this.createCircleShape(gridCellModel));
                this.gridModel.setShape3(posX, posY, this.createCircleShape(gridCellModel));
                this.gridModel.setShape4(posX, posY, this.createCircleShape(gridCellModel));
                this.gridModel.setShape5(posX, posY, this.createCircleShape(gridCellModel));
            }
        }

        this.updateView();
    }

    @NotNull
    private Circle createCircleShape(GridCellModel gridCellModel) {
        final Circle gridNodeCircle2 = new Circle(4.0D, Color.TRANSPARENT);
        gridNodeCircle2.setCenterX(gridCellModel.getScreenPosX());
        gridNodeCircle2.setCenterY(gridCellModel.getScreenPosY());
        gridNodeCircle2.setStroke(Color.RED);
        gridNodeCircle2.setStrokeWidth(1.0D);
        gridNodeCircle2.setVisible(false);
        //gridNodeCircle.relocate(gridNode.getScreenPosX(), gridNode.getScreenPosY());
        this.mainPane.getChildren().add(gridNodeCircle2);
        return gridNodeCircle2;
    }

    @FXML
    protected void onLoadButtonClick() {
        final List<Genom> genomList;
        final File file = new File("demo1.hex.json");

        var objectMapper = new ObjectMapper();
        try {
            genomList = objectMapper.readValue(file, List.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        this.updateView();
    }

    @FXML
    protected void onSaveButtonClick() {
        final List<LifePart> lifePartList = this.lifeService.getLifePartList();
        final List<Genom> genomList = lifePartList.stream().map(lifePart -> lifePart.getBrain().getGenom()).collect(Collectors.toList());

        final File file = new File("demo1.hex.json");

        var objectMapper = new ObjectMapper();
        try {
            // mapper.registerModule(new JavaTimeModule());
            objectMapper.writeValue(file, genomList);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onNextButtonClick() {
        this.hexGridService.calcNext();

        this.updateView();
    }

    @FXML
    protected void onNextAndMoveButtonClick() {
        this.runLife();
    }

    @FXML
    public void onNextGenerationButtonClick(final ActionEvent actionEvent) {
        this.lifeService.calcGenPoolWinners();
        this.updateView();
    }

    @FXML
    public void onStartRunButtonClick(final ActionEvent actionEvent) {
        if (Objects.isNull(this.animationTimer)) {
            this.animationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    runLife();
                }
            };
            this.animationTimer.start();
        }
    }

    @FXML
    public void onStopRunButtonClick(final ActionEvent actionEvent) {
        if (Objects.nonNull(this.animationTimer)) {
            this.animationTimer.stop();
            this.animationTimer = null;
        }
    }

    private void runLife() {
        // Sensor Inputs.
        this.lifeService.runSensorInputs();

        // Calc Brain.
        this.lifeService.runBrain();

        // Output Results.

        if (MainConfig.useSunshine) this.lifeService.addSunshine();
        this.lifeService.runOutputActionResults();

        this.lifeService.calcAcceleration();

        this.lifeService.runMoveOrCollisions();

        this.lifeService.calcNext();

        this.updateView();
    }

    public void updateView() {
        this.counterText.setText(String.format("Step: %d (Parts: %,d)", this.hexGridService.retrieveStepCount(), this.lifeService.retrievePartCount()));

        for (int posY = 0; posY < this.gridModel.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.gridModel.getNodeCountX(); posX++) {
                final GridCellModel gridCellModel = this.gridModel.getGridCellModel(posX, posY);

                this.drawNothing(gridCellModel);

                //final double part1FieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                //        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Part1));
                //final double part2FieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                //        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Part2));
                //final double part3FieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                //        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Part3));
                final double partPushFieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.PartPush));
                final double partPullFieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.PartPull));
                final double comFieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Com));

                //final double extraValue = this.hexGridService.retrieveActGridNodeExtraValue(posX, posY);
                //showCircleShape(gridCellModel.getShape2(), extraValue, Color.WHITE, Color.WHITE);

                showCircleShape(gridCellModel.getShape3(), partPushFieldValue, Color.RED, Color.ORANGE);
                showCircleShape(gridCellModel.getShape4(), partPullFieldValue, Color.BLUE, Color.AQUA);
                showCircleShape(gridCellModel.getShape5(), comFieldValue, Color.TURQUOISE, Color.CORAL);
            }
        }

        this.lifeService.getWallPartList().stream().forEach(lifePart -> {
            drawLifePart(lifePart);
        });
        this.lifeService.getSunPartList().stream().forEach(lifePart -> {
            drawLifePart(lifePart);
        });
        this.lifeService.getLifePartList().stream().forEach(lifePart -> {
            drawLifePart(lifePart);
        });
    }

    private void drawLifePart(final LifePart lifePart) {
        final GridNode gridNode = lifePart.getGridNode();
        final Part part = lifePart.getPart();
        final GridCellModel gridCellModel = this.gridModel.getGridCellModel(gridNode.getPosX(), gridNode.getPosY());

        switch (part.getPartType()) {
            case Nothing -> this.drawNothing(gridCellModel);
            case Life -> this.drawLife(gridCellModel, lifePart);
            case Wall -> this.drawWall(gridCellModel);
            case Sun -> this.drawSun(gridCellModel);
        }
    }

    private static void showCircleShape(final Circle gridNodeCircle2, final double fieldValue, final Color pColor, final Color nColor) {
        if (fieldValue > 0.0D) {
            gridNodeCircle2.setRadius(Math.min(fieldValue * 2.0D, 12.0D));
            gridNodeCircle2.setVisible(true);
            gridNodeCircle2.setStroke(pColor.interpolate(nColor, MathUtils.sigmoid(fieldValue)));
        } else {
            if (fieldValue < 0.0D) {
                gridNodeCircle2.setRadius(Math.min(-fieldValue * 2.0D, 12.0D));
                gridNodeCircle2.setVisible(true);
                gridNodeCircle2.setStroke(nColor.interpolate(pColor, MathUtils.sigmoid(-fieldValue)));
            } else {

                gridNodeCircle2.setVisible(false);
            }
        }
    }

    private void drawNothing(final GridCellModel gridCellModel) {
        final Circle gridNodeCircle = gridCellModel.getShape();

        gridNodeCircle.setRadius(1.0D);
        gridNodeCircle.setFill(Color.DARKGRAY);
    }

    private void drawLife(final GridCellModel gridCellModel, final LifePart lifePart) {
        final Circle gridNodeCircle = gridCellModel.getShape();
        final Part part = lifePart.getPart();

        gridNodeCircle.setRadius(4.0D + (MathUtils.sigmoid(part.getEnergy() * 5.0D)));
        gridNodeCircle.setFill(Color.color(
                Math.abs(MathUtils.sigmoid(lifePart.partIdentity.partIdentity[0])),
                Math.abs(MathUtils.sigmoid(lifePart.partIdentity.partIdentity[1])),
                Math.abs(MathUtils.sigmoid(lifePart.partIdentity.partIdentity[2]))
                ));
    }

    private void drawWall(final GridCellModel gridCellModel) {
        final Circle gridNodeCircle = gridCellModel.getShape();

        gridNodeCircle.setRadius(8.0D);
        gridNodeCircle.setFill(Color.GRAY);
    }

    private void drawSun(final GridCellModel gridCellModel) {
        final Circle gridNodeCircle = gridCellModel.getShape();

        gridNodeCircle.setRadius(2.0D);
        gridNodeCircle.setFill(Color.YELLOW);
    }

}