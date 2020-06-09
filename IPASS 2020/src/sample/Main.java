package sample;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Circle cir1 = new Circle();
        cir1.setFill(Color.BLUE);
        cir1.setRadius(50);
        cir1.relocate(50, 50);
        cir1.setLayoutX(50);
        cir1.setLayoutY(50);

        TranslateTransition Transition = new TranslateTransition();
        Transition.setDuration(Duration.seconds(3));
        Transition.setToX(900);
        Transition.setToY(900);
        Transition.setAutoReverse(true);
        Transition.setCycleCount(Animation.INDEFINITE);
        Transition.setNode(cir1);
        Transition.play();

        Pane root = new Pane();

        root.getChildren().add(cir1);

        primaryStage.setTitle("Flocking sim van Lars Schipper ");
        primaryStage.setScene(new Scene(root, 1000, 1000));
        primaryStage.show();

    }



    public static void main(String[] args) {
        launch(args);
    }
}
