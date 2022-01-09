package src.client.main;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;



public class Main extends Application {
    double yOffset;
    double xOffset;
    @Override
    public void start(Stage primaryStage) throws Exception {


        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("arcticsu");
        Scene scene = new Scene(root, 420, 800, Color.TRANSPARENT);
       ToolBar toolBar = (ToolBar) root.getChildrenUnmodifiable().get(0);
        
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
        Animation animation = appearance(primaryStage.opacityProperty());
        animation.play();


        toolBar.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = primaryStage.getX() - event.getScreenX();
                    yOffset = primaryStage.getY() - event.getScreenY();
                }
        });
        toolBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() + xOffset);
                    primaryStage.setY(event.getScreenY() + yOffset);
                }
        });
    }

    private Animation appearance(DoubleProperty property) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(property, 0)),
                new KeyFrame(Duration.millis(800), new KeyValue(property, 1))
        );
        return timeline;
    }
}

