package de.schmiereck.hexWave.view;

import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.service.life.LifeService;
import de.schmiereck.hexWave.service.hexGrid.HexGrid;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private GridModel gridModel = new GridModel();

    private AnimationTimer animationTimer;

    public static boolean useSunshine = false;
    public static boolean useLifeParts = false;
    public static boolean useBall = true;

    @FXML
    public void initialize() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        this.mainPane.setStyle("-fx-background-color: black;");

        //this.hexGridService.initialize(2, 1);
        this.hexGridService.initialize(10, 3);
        this.lifeService.initialize(useLifeParts ? 130 : 0);
        if (useBall) this.lifeService.initializeBall();

        final HexGrid hexGrid = this.hexGridService.getHexGrid();

        this.gridModel.init(hexGrid.getNodeCountX(), hexGrid.getNodeCountY());

        for (int posY = 0; posY < this.gridModel.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.gridModel.getNodeCountX(); posX++) {
                final GridCellModel gridCellModel = this.gridModel.getGridCellModel(posX, posY);
                final Circle gridNodeCircle2 = new Circle(4.0D, Color.TRANSPARENT);
                gridNodeCircle2.setCenterX(gridCellModel.getScreenPosX());
                gridNodeCircle2.setCenterY(gridCellModel.getScreenPosY());
                gridNodeCircle2.setStroke(Color.RED);
                gridNodeCircle2.setStrokeWidth(2.0D);
                gridNodeCircle2.setVisible(false);
                //gridNodeCircle.relocate(gridNode.getScreenPosX(), gridNode.getScreenPosY());
                this.mainPane.getChildren().add(gridNodeCircle2);

                this.gridModel.setShape2(posX, posY, gridNodeCircle2);
            }
        }

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

        this.updateView();
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
    public void onNextGenerationButtonClick(ActionEvent actionEvent) {
        this.lifeService.calcGenPoolWinners();
        this.updateView();
    }

    @FXML
    public void onStartRunButtonClick(ActionEvent actionEvent) {
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

    private void runLife() {
        // Sensor Inputs.
        this.lifeService.runSensorInputs();

        // Calc Brain.
        this.lifeService.runBrain();

        // Output Results.

        if (useSunshine) this.lifeService.addSunshine();
        this.lifeService.runOutputActionResults();
        this.lifeService.runCollisions();

        this.lifeService.calcNext();

        this.updateView();
    }

    @FXML
    public void onStopRunButtonClick(ActionEvent actionEvent) {
        if (Objects.nonNull(this.animationTimer)) {
            this.animationTimer.stop();
            this.animationTimer = null;
        }
    }

    public void updateView() {
        this.counterText.setText(String.format("Step: %d (Part-Steps: %,d)", this.hexGridService.retrieveStepCount(), this.hexGridService.retrievePartCount()));

        for (int posY = 0; posY < this.gridModel.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.gridModel.getNodeCountX(); posX++) {
                final GridCellModel gridCellModel = this.gridModel.getGridCellModel(posX, posY);

                final GridNode gridNode = this.hexGridService.retrieveGridNode(posX, posY);
                final List<Part> gridNodePartList = gridNode.getPartList(0);

                final Optional<Part> optionalPart = gridNodePartList.stream().findFirst();
                if (optionalPart.isPresent()) {
                    final Part part = optionalPart.get();
                    switch (part.getPartType()) {
                        case Nothing -> this.drawNothing(gridCellModel);
                        case Life -> this.drawLife(gridCellModel);
                        case Wall -> this.drawWall(gridCellModel);
                        case Sun -> this.drawSun(gridCellModel);
                    }
                } else {
                    this.drawNothing(gridCellModel);
                }

                final double partFieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                        this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.Part));
                final double partPushFieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                        this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.PartPush));
                final double partPullFieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                        this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.PartPull));

                final double fieldValue = partPushFieldValue;

                final Circle gridNodeCircle2 = gridCellModel.getShape2();

                if (fieldValue > 0.0D) {
                    gridNodeCircle2.setRadius(Math.min(fieldValue * 4.0D, 12.0D));
                    gridNodeCircle2.setVisible(true);
                    gridNodeCircle2.setStroke(Color.RED);
                } else {
                    if (fieldValue < 0.0D) {
                        gridNodeCircle2.setRadius(Math.min(-fieldValue * 4.0D, 12.0D));
                        gridNodeCircle2.setVisible(true);
                        gridNodeCircle2.setStroke(Color.BLUE);
                    } else {

                        gridNodeCircle2.setVisible(false);
                    }
                }
            }
        }
    }

    private void drawNothing(GridCellModel gridCellModel) {
        final Circle gridNodeCircle = gridCellModel.getShape();

        gridNodeCircle.setRadius(1.0D);
        gridNodeCircle.setFill(Color.DARKGRAY);
    }

    private void drawLife(GridCellModel gridCellModel) {
        final Circle gridNodeCircle = gridCellModel.getShape();

        gridNodeCircle.setRadius(9.0D);
        gridNodeCircle.setFill(Color.WHITE);
    }

    private void drawWall(GridCellModel gridCellModel) {
        final Circle gridNodeCircle = gridCellModel.getShape();

        gridNodeCircle.setRadius(8.0D);
        gridNodeCircle.setFill(Color.GRAY);
    }

    private void drawSun(GridCellModel gridCellModel) {
        final Circle gridNodeCircle = gridCellModel.getShape();

        gridNodeCircle.setRadius(2.0D);
        gridNodeCircle.setFill(Color.YELLOW);
    }

}