package de.schmiereck.hexWave.view;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.service.genom.GenomDocument;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.service.life.FieldTypeService;
import de.schmiereck.hexWave.service.life.LifePart;
import de.schmiereck.hexWave.service.life.LifeService;
import de.schmiereck.hexWave.service.hexGrid.HexGrid;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.utils.MathUtils;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
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
    private ScrollPane mainPane;

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
        //MainConfig.initConfig(MainConfig.ConfigEnum.BlockedBall);
        //MainConfig.initConfig(MainConfig.ConfigEnum.MachineBalls);
        //MainConfig.initConfig(MainConfig.ConfigEnum.CrashBalls);
        //MainConfig.initConfig(MainConfig.ConfigEnum.ShowFields);
        //MainConfig.initConfig(MainConfig.ConfigEnum.OnlySun);
        //MainConfig.initConfig(MainConfig.ConfigEnum.NoMoves);

        this.mainPane.setStyle("-fx-background: black;");
/*
        this.mainPane.setOnScroll(new EventHandler<ScrollEvent>() {
            final double SCALE_DELTA = 1.1;
            @Override public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                double scaleFactor =
                        (event.getDeltaY() > 0)
                                ? SCALE_DELTA
                                : 1/SCALE_DELTA;

                mainPane.setScaleX(mainPane.getScaleX() * scaleFactor);
                mainPane.setScaleY(mainPane.getScaleY() * scaleFactor);
            }
        });
*/
        final int maxAreaDistance = this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Part1).getMaxAreaDistance();

        //this.hexGridService.initialize(2, 1);
        this.hexGridService.initialize(MainConfig.HexGridXSize, MainConfig.HexGridYSize, maxAreaDistance);
        this.lifeService.initializeWalls();
        this.lifeService.initializeExtraWalls();
        this.lifeService.initialize(MainConfig.useLifeParts ? MainConfig.LifePartsCount : 0);
        if (MainConfig.useBall) {
            for (int pos = 0; pos < MainConfig.BallStartXPos.length; pos++) {
                this.lifeService.initializeBall(MainConfig.BallStartXPos[pos], MainConfig.BallStartYPos[pos], MainConfig.BallStartVelocityA[pos]);
            }
        }
        if (MainConfig.useShowFields) this.lifeService.initializeShowFields();

        final HexGrid hexGrid = this.hexGridService.getHexGrid();

        this.gridModel.init(hexGrid.getNodeCountX(), hexGrid.getNodeCountY());

        final Group mainGroup = new Group();

        for (int posY = 0; posY < this.gridModel.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.gridModel.getNodeCountX(); posX++) {
                final GridCellModel gridCellModel = this.gridModel.getGridCellModel(posX, posY);
                final Circle gridNodeCircle = new Circle(1.0D, Color.DARKSLATEGRAY);
                gridNodeCircle.setCenterX(gridCellModel.getScreenPosX());
                gridNodeCircle.setCenterY(gridCellModel.getScreenPosY());
                //gridNodeCircle.relocate(gridNode.getScreenPosX(), gridNode.getScreenPosY());
                mainGroup.getChildren().add(gridNodeCircle);

                this.gridModel.setShape(posX, posY, gridNodeCircle);
            }
        }

        for (int posY = 0; posY < this.gridModel.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.gridModel.getNodeCountX(); posX++) {
                final GridCellModel gridCellModel = this.gridModel.getGridCellModel(posX, posY);
                this.gridModel.setShape2(posX, posY, this.createCircleShape(mainGroup, gridCellModel));
                this.gridModel.setShape3(posX, posY, this.createCircleShape(mainGroup, gridCellModel));
                this.gridModel.setShape4(posX, posY, this.createCircleShape(mainGroup, gridCellModel));
                this.gridModel.setShape5(posX, posY, this.createCircleShape(mainGroup, gridCellModel));
            }
        }

        //final Parent zoomPane = this.createZoomPane(mainGroup);

        //VBox.setVgrow(zoomPane, Priority.ALWAYS);
        //VBox.setVgrow(this.mainPane, Priority.ALWAYS);

        //this.mainPane.getChildren().add(zoomPane);

        //this.mainPane.setContent(mainGroup);
        this.initZoomPane2(this.mainPane, mainGroup);

        this.updateView();
    }

    @NotNull
    private Circle createCircleShape(final Group mainGroup, final GridCellModel gridCellModel) {
        final Circle gridNodeCircle2 = new Circle(4.0D, Color.TRANSPARENT);
        gridNodeCircle2.setCenterX(gridCellModel.getScreenPosX());
        gridNodeCircle2.setCenterY(gridCellModel.getScreenPosY());
        gridNodeCircle2.setStroke(Color.RED);
        gridNodeCircle2.setStrokeWidth(1.0D);
        gridNodeCircle2.setVisible(false);
        //gridNodeCircle.relocate(gridNode.getScreenPosX(), gridNode.getScreenPosY());
        mainGroup.getChildren().add(gridNodeCircle2);
        return gridNodeCircle2;
    }

    private final FileChooser fileChooser = new FileChooser();
    private File lastFile = new File("demo1.hex.json");

    @FXML
    protected void onSaveButtonClick() {
        final Window window = this.mainPane.getScene().getWindow();

        this.fileChooser.setInitialDirectory(this.lastFile.getAbsoluteFile().getParentFile());
        this.fileChooser.setInitialFileName(this.lastFile.getAbsoluteFile().getName());

        final File file = this.fileChooser.showSaveDialog(window);
        if (file != null) {
            this.lastFile = file;
            final List<LifePart> lifePartList = this.lifeService.getLifePartList();
            final GenomDocument genomDocument = new GenomDocument();
            genomDocument.genomList = lifePartList.stream().map(lifePart -> lifePart.getBrain().getGenom()).collect(Collectors.toList());


            var objectMapper = new ObjectMapper();

            //final SimpleModule module = new SimpleModule();
            //module.addDeserializer(Genom.class, new GenomDeserializer());

            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY);
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
            try {
                // mapper.registerModule(new JavaTimeModule());
                objectMapper.writeValue(file, genomDocument);
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonGenerationException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    protected void onLoadButtonClick() {
        final Window window = this.mainPane.getScene().getWindow();

        this.fileChooser.setInitialDirectory(this.lastFile.getAbsoluteFile().getParentFile());
        this.fileChooser.setInitialFileName(this.lastFile.getAbsoluteFile().getName());

        final File file = this.fileChooser.showOpenDialog(window);
        if (file != null) {
            this.lastFile = file;
            final GenomDocument genomDocument;

            var objectMapper = new ObjectMapper();
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
            //objectMapper.enable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES);
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY);
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
            try {
                genomDocument = objectMapper.readValue(file, new TypeReference<GenomDocument>() {
                });
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonParseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.lifeService.initializeByGenomList(genomDocument.genomList);

            this.updateView();
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
                    HexWaveController.this.runLife();
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

                showCircleShape(gridCellModel.getShape3(), partPushFieldValue, Color.TRANSPARENT, Color.RED);//Color.ORANGE, Color.RED);
                showCircleShape(gridCellModel.getShape4(), partPullFieldValue, Color.TRANSPARENT, Color.LIGHTBLUE);//Color.AQUA, Color.BLUE);
                showCircleShape(gridCellModel.getShape5(), comFieldValue, Color.TRANSPARENT, Color.ANTIQUEWHITE);//Color.YELLOW, Color.ANTIQUEWHITE);
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
            gridNodeCircle2.setStroke(pColor.interpolate(nColor, MathUtils.sigmoid(fieldValue + 0.1D)));
        } else {
            if (fieldValue < 0.0D) {
                gridNodeCircle2.setRadius(Math.min(-fieldValue * 2.0D, 12.0D));
                gridNodeCircle2.setVisible(true);
                gridNodeCircle2.setStroke(nColor.interpolate(pColor, MathUtils.sigmoid(-fieldValue + 0.1D)));
            } else {

                gridNodeCircle2.setVisible(false);
            }
        }
    }

    private void drawNothing(final GridCellModel gridCellModel) {
        final Circle gridNodeCircle = gridCellModel.getShape();

        gridNodeCircle.setRadius(1.0D);
        gridNodeCircle.setFill(Color.DARKSLATEGRAY);
    }

    private void drawLife(final GridCellModel gridCellModel, final LifePart lifePart) {
        final Circle gridNodeCircle = gridCellModel.getShape();
        final Part part = lifePart.getPart();

        gridNodeCircle.setRadius(2.0D + Math.min(part.getEnergy() / 5.0D, 8.0D));
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

    /**
     * https://stackoverflow.com/questions/16680295/javafx-correct-scaling
     */
    private void initZoomPane2(final ScrollPane scrollPane, final Group group) {
        final double SCALE_DELTA = 1.1;

        final StackPane zoomPane = new StackPane();
        zoomPane.getChildren().add(group);

        final Group scrollContent = new Group(zoomPane);

        scrollPane.setContent(scrollContent);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);

        scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable,
                                Bounds oldValue, Bounds newValue) {
                zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
            }
        });

        //scrollPane.setPrefViewportWidth(256);
        //scrollPane.setPrefViewportHeight(256);

        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA
                        : 1 / SCALE_DELTA;

                // amount of scrolling in each direction in scrollContent coordinate units
                Point2D scrollOffset = figureScrollOffset(scrollContent, scrollPane);

                group.setScaleX(group.getScaleX() * scaleFactor);
                group.setScaleY(group.getScaleY() * scaleFactor);

                // move viewport so that old center remains in the center after the scaling
                repositionScroller(scrollContent, scrollPane, scaleFactor, scrollOffset);

            }
        });

        // Panning via drag....
        final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
        scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
            }
        });

        scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double deltaX = event.getX() - lastMouseCoordinates.get().getX();
                double extraWidth = scrollContent.getLayoutBounds().getWidth() - scrollPane.getViewportBounds().getWidth();
                double deltaH = deltaX * (scrollPane.getHmax() - scrollPane.getHmin()) / extraWidth;
                double desiredH = scrollPane.getHvalue() - deltaH;
                scrollPane.setHvalue(Math.max(0, Math.min(scrollPane.getHmax(), desiredH)));

                double deltaY = event.getY() - lastMouseCoordinates.get().getY();
                double extraHeight = scrollContent.getLayoutBounds().getHeight() - scrollPane.getViewportBounds().getHeight();
                double deltaV = deltaY * (scrollPane.getHmax() - scrollPane.getHmin()) / extraHeight;
                double desiredV = scrollPane.getVvalue() - deltaV;
                scrollPane.setVvalue(Math.max(0, Math.min(scrollPane.getVmax(), desiredV)));
            }
        });
    }

    private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    private void repositionScroller(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
            double halfWidth = scroller.getViewportBounds().getWidth() / 2;
            double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
            double halfHeight = scroller.getViewportBounds().getHeight() / 2;
            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
    }
}