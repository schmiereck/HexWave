package de.schmiereck.hexWave;

import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.HexGrid;
import de.schmiereck.hexWave.view.GridModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    //@Value("classpath:/de/schmiereck/hexWave/hex2D-view.fxml")
    @Value("classpath:/hex2D-view.fxml")
    private Resource chartResource;
    private String applicationTitle;
    private ApplicationContext applicationContext;

    @Autowired
    private HexGridService hexGridService;

    public StageInitializer(@Value("${spring.application.ui.title}") final String applicationTitle,
                            final ApplicationContext applicationContext) {
        this.applicationTitle = applicationTitle;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(final StageReadyEvent event) {
        final Stage stage = event.getStage();
        try {
            //FXMLLoader fxmlLoader = new FXMLLoader(Hex2DApplication.class.getResource("hex2D-view.fxml"));
            FXMLLoader fxmlLoader = new FXMLLoader(this.chartResource.getURL());
            fxmlLoader.setControllerFactory(aClass -> this.applicationContext.getBean(aClass));

            final Parent parent = fxmlLoader.load();

            final HexGrid hexGrid = this.hexGridService.getHexGrid();

            //final double initialSceneWidth = 640; //hexGrid.getNodeCountX() * GridModel.StepX;
            //final double initialSceneHeight = 460; //(hexGrid.getNodeCountY()) * GridModel.StepY;
            final double initialSceneWidth = hexGrid.getNodeCountX() * GridModel.StepX;
            final double initialSceneHeight = hexGrid.getNodeCountY() * GridModel.StepY;

            final Scene scene = new Scene(parent, initialSceneWidth, initialSceneHeight);

            stage.setTitle(this.applicationTitle);
            stage.setScene(scene);


            //final BorderPane borderPane = (BorderPane) scene.lookup("#mainBoderPane");

            final Pane mainPane = (Pane) scene.lookup("#mainPane");
            //mainPane.setPrefSize(240, 200);

            //final Circle circle = new Circle(50, Color.BLUE);
            //circle.relocate(20, 20);

            //final Rectangle rectangle = new Rectangle(100, 100, Color.RED);
            //rectangle.relocate(70, 70);

            //canvas.getChildren().addAll(circle, rectangle);

            //borderPane.setCenter(mainPane);

            //scene.setRoot(borderPane);
            //scene.setRoot(borderPane);

            stage.show();

            final Window window = scene.getWindow();

            final double decorationWidth = scene.getWidth() - initialSceneWidth;
            final double decorationHeight = scene.getHeight() - initialSceneHeight;

            final double titleHeight = window.getHeight() - scene.getHeight();

            final HBox mainButtonBar = (HBox) scene.lookup("#mainButtonBar");
            final double mainButtonBarHeight = mainButtonBar.getHeight();

            stage.setWidth(initialSceneWidth + decorationWidth);
            stage.setHeight(initialSceneHeight + decorationHeight + mainButtonBarHeight + titleHeight);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
