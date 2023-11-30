package de.schmiereck.hexWave2.view;

import static de.schmiereck.hexWave2.MainConfig3.InitialFieldPartProbabilityFactor;
import static de.schmiereck.hexWave2.MainConfig3.MaxPotentialProbability;

import de.schmiereck.hexWave2.MainConfig3;
import de.schmiereck.hexWave2.service.hexGrid.GridNode;
import de.schmiereck.hexWave2.service.hexGrid.HexGrid;
import de.schmiereck.hexWave2.service.hexGrid.HexGridService;
import de.schmiereck.hexWave2.service.hexGrid.Particle;
import de.schmiereck.hexWave2.service.life.LifeService;
import de.schmiereck.hexWave2.utils.MathUtils;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HexWave2Controller implements Initializable
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

    private GridModel gridModel = new GridModel();

    private AnimationTimer animationTimer;

    @FXML
    public void initialize() {
    }

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.LifeEnvironment);

        //MainConfig3.initConfig(MainConfig3.ConfigEnum.StaticBall);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.StaticBallWithField);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.StaticBallWithPotential);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.StaticBallWithPotentialAndField);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.MovingBallToRight);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.MovingBallToRightTop5);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.MovingBallToRightTop15);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.MovingBallToRightTop45);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.MovingBallWithField);
        MainConfig3.initConfig(MainConfig3.ConfigEnum.MovingBallWithPotential);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.MovingBallWithPotentialAndField);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.InteractingBallsWithFieldsNP);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.InteractingBallsWithFieldsNN);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.InteractingBallsWithPotentialAndFieldsNN);
        //MainConfig3.initConfig(MainConfig3.ConfigEnum.InteractingBallsWithPotentialAndFieldsNP);

        //MainConfig3.initConfig(MainConfig3.ConfigEnum.CollideBallsWithPotentialAndFieldsNN);

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
        final int maxAreaDistance = 3;

        //this.hexGridService.initialize(2, 1);
        this.hexGridService.initialize(MainConfig3.HexGridXSize, MainConfig3.HexGridYSize, maxAreaDistance);
        this.lifeService.initialize();


        if (MainConfig3.UseWalls)
            this.lifeService.initializeWalls();
        if (MainConfig3.UseExtraWalls)
            this.lifeService.initializeExtraWalls();
        if (MainConfig3.useBall) {
            for (int pos = 0; pos < MainConfig3.BallStartXPos.length; pos++) {
                this.lifeService.initializeBall(MainConfig3.BallStartXPos[pos], MainConfig3.BallStartYPos[pos], MainConfig3.BallStartVelocity[pos],
                        MainConfig3.useBallPush,
                        MainConfig3.BallPartSubTypeArr[pos], MainConfig3.BallFieldSubTypeArr[pos]);
            }
        }

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

        final ObjectProperty<Point2D> dragLastMouseCoordinates = new SimpleObjectProperty<>();
        final ObjectProperty<Boolean> dragOperation = new SimpleObjectProperty<>();
        dragOperation.set(Boolean.FALSE);

        this.mainPane.setOnMouseClicked(event -> {
            if (dragOperation.get() == Boolean.FALSE) {
                final Point2D sceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                final Point2D anchorPaneCoords = mainGroup.sceneToLocal(sceneCoords);

                final Rectangle rect = new Rectangle(10.0D, 10.0D, Color.DARKSLATEGRAY);
                rect.setX(anchorPaneCoords.getX());
                rect.setY(anchorPaneCoords.getY());
                mainGroup.getChildren().add(rect);
                event.consume();
            } else {
                dragOperation.set(Boolean.FALSE);
            }
        });

        this.initZoomPane2(this.mainPane, mainGroup, dragLastMouseCoordinates, dragOperation);

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
            //final List<LifePart> lifePartList = this.lifeService.getLifePartList();
            //final GenomDocument genomDocument = new GenomDocument();
            //genomDocument.genomList = lifePartList.stream().map(lifePart -> lifePart.getBrain().getGenom()).collect(Collectors.toList());


            var objectMapper = new ObjectMapper();

            //final SimpleModule module = new SimpleModule();
            //module.addDeserializer(Genom.class, new GenomDeserializer());

            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY);
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
            /*
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
            */
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
            //final GenomDocument genomDocument;

            var objectMapper = new ObjectMapper();
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
            //objectMapper.enable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES);
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY);
            //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
            /*
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
            */

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
        this.updateView();
    }

    @FXML
    public void onStartRunButtonClick(final ActionEvent actionEvent) {
        if (Objects.isNull(this.animationTimer)) {
            this.animationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    HexWave2Controller.this.runLife();
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
        this.lifeService.runLife();

        this.updateView();
    }

    public void updateView() {
        this.counterText.setText(String.format("Step: %d (Parts: %,d)",
                this.hexGridService.retrieveStepCount(),
                this.lifeService.retrievePartCount()));
        final HexGrid hexGrid = this.hexGridService.getHexGrid();

        for (int posY = 0; posY < this.gridModel.getNodeCountY(); posY++) {
            for (int posX = 0; posX < this.gridModel.getNodeCountX(); posX++) {
                final GridCellModel gridCellModel = this.gridModel.getGridCellModel(posX, posY);

                this.drawNothing(gridCellModel);

                final GridNode gridNode = this.hexGridService.getGridNode(posX, posY);
                final double partValue = this.hexGridService.retrieveActGridNodePartValue(posX, posY);
                final double nFieldValue = this.hexGridService.retrieveActGridNodeFieldValue(posX, posY, Particle.PartSubType.FieldN);
                final double pFieldValue = this.hexGridService.retrieveActGridNodeFieldValue(posX, posY, Particle.PartSubType.FieldP);

                //final double part1FieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                //        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Part1));
                //final double part2FieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                //        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Part2));
                //final double part3FieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                //        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Part3));

                //final double partPushFieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                //        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.PartPush));
                //final double partPullFieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                //        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.PartPull));
                //final double comFieldValue = this.hexGridService.retrieveActGridNodePartFieldValue(posX, posY,
                //        this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.Com));

                //final double extraValue = this.hexGridService.retrieveActGridNodeExtraValue(posX, posY);
                //showCircleShape(gridCellModel.getShape2(), extraValue, Color.WHITE, Color.WHITE);

                if (pFieldValue > 0.0D && nFieldValue > 0.0D) {
                    showCircleShape(gridCellModel.getShape2(), (pFieldValue + nFieldValue) * FieldFactor, Color.TRANSPARENT, Color.LIGHTGREEN);//Color.ORANGE, Color.RED);
                } else {
                    showCircleShape(gridCellModel.getShape3(), pFieldValue * FieldFactor, Color.TRANSPARENT, Color.LIGHTCORAL);//Color.ORANGE, Color.RED);
                    showCircleShape(gridCellModel.getShape4(), nFieldValue * FieldFactor, Color.TRANSPARENT, Color.LIGHTBLUE);//Color.AQUA, Color.BLUE);, Color.LIGHTBLUE
                }
                showCircleShape(gridCellModel.getShape5(), partValue * ParticleFactor, Color.TRANSPARENT, Color.ANTIQUEWHITE);//Color.YELLOW, Color.ANTIQUEWHITE);
            }
        }
    }

    //final static double FieldFactor = 55.0D;
    final static double FieldFactor = 1.0D / ((MaxPotentialProbability * 24.0D) / InitialFieldPartProbabilityFactor);

    //final static double RadiusFactor = 0.1D;
    final static double ParticleFactor = 1.0D / (MaxPotentialProbability / (24.0D * 6.0D));
    //final static double RadiusFactor = 0.5D;

    private static void showCircleShape(final Circle gridNodeCircle2, final double fieldValue, final Color pColor, final Color nColor) {
        if (fieldValue > 0.0D) {
            gridNodeCircle2.setRadius(Math.min(fieldValue, 12.0D));
            gridNodeCircle2.setVisible(true);
            gridNodeCircle2.setStroke(pColor.interpolate(nColor, MathUtils.sigmoid(fieldValue + 0.1D)));
        } else {
            if (fieldValue < 0.0D) {
                gridNodeCircle2.setRadius(Math.min(-fieldValue, 12.0D));
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
    private void initZoomPane2(final ScrollPane scrollPane, final Group group, final ObjectProperty<Point2D> dragLastMouseCoordinates, final ObjectProperty<Boolean> dragOperation) {
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
            public void changed(final ObservableValue<? extends Bounds> observable,
                                final Bounds oldValue, final Bounds newValue) {
                zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
            }
        });

        //scrollPane.setPrefViewportWidth(256);
        //scrollPane.setPrefViewportHeight(256);

        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(final ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                final double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA
                        : 1 / SCALE_DELTA;

                // amount of scrolling in each direction in scrollContent coordinate units
                final Point2D scrollOffset = figureScrollOffset(scrollContent, scrollPane);

                group.setScaleX(group.getScaleX() * scaleFactor);
                group.setScaleY(group.getScaleY() * scaleFactor);

                // move viewport so that old center remains in the center after the scaling
                repositionScroller(scrollContent, scrollPane, scaleFactor, scrollOffset);

            }
        });

        // Panning via drag....
        //final ObjectProperty<Point2D> dragLastMouseCoordinates = new SimpleObjectProperty<Point2D>();

        scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                dragLastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
                event.consume();
            }
        });

        scrollContent.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                //dragLastMouseCoordinates.set(null);
                event.consume();
            }
        });

        scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                dragOperation.setValue(Boolean.TRUE);
                final Point2D lastMousePoint = dragLastMouseCoordinates.get();
                if (Objects.nonNull(lastMousePoint)) {
                    final double deltaX = event.getX() - lastMousePoint.getX();
                    final double extraWidth = scrollContent.getLayoutBounds().getWidth() - scrollPane.getViewportBounds().getWidth();
                    final double deltaH = deltaX * (scrollPane.getHmax() - scrollPane.getHmin()) / extraWidth;
                    final double desiredH = scrollPane.getHvalue() - deltaH;
                    scrollPane.setHvalue(Math.max(0, Math.min(scrollPane.getHmax(), desiredH)));

                    final double deltaY = event.getY() - lastMousePoint.getY();
                    final double extraHeight = scrollContent.getLayoutBounds().getHeight() - scrollPane.getViewportBounds().getHeight();
                    final double deltaV = deltaY * (scrollPane.getHmax() - scrollPane.getHmin()) / extraHeight;
                    final double desiredV = scrollPane.getVvalue() - deltaV;
                    scrollPane.setVvalue(Math.max(0, Math.min(scrollPane.getVmax(), desiredV)));
                    event.consume();
                }
            }
        });
    }

    private Point2D figureScrollOffset(final Node scrollContent, final ScrollPane scroller) {
        final double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        final double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
        final double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        final double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        final double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
        final double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    private void repositionScroller(final Node scrollContent, final ScrollPane scroller, final double scaleFactor, final Point2D scrollOffset) {
        final double scrollXOffset = scrollOffset.getX();
        final double scrollYOffset = scrollOffset.getY();
        final double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
            final double halfWidth = scroller.getViewportBounds().getWidth() / 2;
            final double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
        final double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
            final double halfHeight = scroller.getViewportBounds().getHeight() / 2;
            final double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
    }
}