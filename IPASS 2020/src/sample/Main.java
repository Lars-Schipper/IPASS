package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {


    int aantalBoids = 50;
    double boidRadius = 10d;
    double boidMinDistance = boidRadius * 2d + 5;
    double initialBaseVelocity = 1d;
    double velocitiyLimit = 3d;
    double movemnetToCenter = 0.01;

    public List<Boid> boids;

    static Random rnd = new Random();

    double sceneWidth = 1024;
    double sceneHeight = 768;

    Pane playfield;

    Rectangle rectangle;

    @Override
    public void start(Stage primaryStage) throws Exception{

        BorderPane root = new BorderPane();

        playfield = new Pane();
        playfield.setPrefSize(sceneWidth, sceneHeight);

        //Text infoText = new Text("drag the rectangle and have the flock follow it");
        //root.setTop(infoText);

        root.setCenter(playfield);

        Scene scene = new Scene(root, sceneWidth, sceneHeight, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.show();

        createBoids();

        playfield.getChildren().addAll(boids);

        double w = 20;
        double h = 20;
        rectangle = new Rectangle(w, h);
        rectangle.relocate(sceneWidth / 2 - w/ 2, sceneHeight / 4 - h/2);
        playfield.getChildren().add(rectangle);

        MouseGestures mg = new MouseGestures();
        mg.makeDraggable(rectangle);




    }

    private void createBoids(){
        boids = new ArrayList<>();

        double marginX = sceneWidth / 4;
        double marginY = sceneHeight / 4;

        for(int i = 0; i < aantalBoids; i++){
            double x = rnd.nextDouble() * (sceneWidth - marginX * 2) + marginX;
            double y = rnd.nextDouble() * (sceneHeight - marginY * 2) + marginY;

            double v = Math.random() * 4 + initialBaseVelocity;

            Boid boid = new Boid(i, x, y, v);

            boids.add(boid);
        }
    }

    public class Boid extends Circle {

        int id;

        Point2D position;
        Point2D velocity;

        double v;

        // random color
        Color color = Color.BLACK;

        public Boid(int id, double x, double y, double v) {

            this.id = id;
            this.v = v;

            position = new Point2D( x, y);
            velocity = new Point2D( v, v);

            setRadius( boidRadius);

            setStroke(color);
            setFill(color.deriveColor(1, 1, 1, 0.2));

        }

//        public void move() {
//
//            Point2D v1 = rule1(this);
//            Point2D v2 = rule2(this);
//            Point2D v3 = rule3(this);
//            Point2D v4 = tendToPlace(this);
//
//            velocity = velocity
//                    .add(v1)
//                    .add(v2)
//                    .add(v3)
//                    .add(v4)
//            ;
//
//            limitVelocity();
//
//            position = position.add(velocity);
//
//            constrainPosition();
//        }

//        private void limitVelocity() {
//
//            double vlim = velocityLimit;
//
//            if( velocity.magnitude() > vlim) {
//                velocity = (velocity.multiply(1d/velocity.magnitude())).multiply( vlim);
//            }
//
//        }


        // limit position to screen dimensions
        public void constrainPosition() {

            double xMin = boidRadius;
            double xMax = sceneWidth - boidRadius;
            double yMin = boidRadius;
            double yMax = sceneHeight - boidRadius;

            double x = position.getX();
            double y = position.getY();
            double vx = velocity.getX();
            double vy = velocity.getY();

            if( x < xMin) {
                x = xMin;
                vx = v;
            }
            else if( x > xMax) {
                x = xMax;
                vx = -v;
            }

            if( y < yMin) {
                y = yMin;
                vy = v;
            }
            else if( y > yMax) {
                y = yMax;
                vy = -v;
            }

            // TODO: modification would be less performance consuming => find out how to modify the vector directly or create own Poin2D class
            position = new Point2D( x, y);
            velocity = new Point2D( vx, vy);

        }


        public void updateUI() {

            setCenterX(position.getX());
            setCenterY(position.getY());
        }
    }

    public static class MouseGestures {

        class DragContext {
            double x;
            double y;
        }

        DragContext dragContext = new DragContext();

        public void makeDraggable( Node node) {
            node.setOnMousePressed( onMousePressedEventHandler);
            node.setOnMouseDragged( onMouseDraggedEventHandler);
            node.setOnMouseReleased( onMouseReleasedEventHandler);
        }

        EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if( event.getSource() instanceof Circle) {

                    Circle circle = ((Circle) (event.getSource()));

                    dragContext.x = circle.getCenterX() - event.getSceneX();
                    dragContext.y = circle.getCenterY() - event.getSceneY();

                } else {

                    Node node = ((Node) (event.getSource()));

                    dragContext.x = node.getTranslateX() - event.getSceneX();
                    dragContext.y = node.getTranslateY() - event.getSceneY();

                }
            }
        };

        EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if( event.getSource() instanceof Circle) {

                    Circle circle = ((Circle) (event.getSource()));

                    circle.setCenterX( dragContext.x + event.getSceneX());
                    circle.setCenterY( dragContext.y + event.getSceneY());

                } else {

                    Node node = ((Node) (event.getSource()));

                    node.setTranslateX( dragContext.x + event.getSceneX());
                    node.setTranslateY( dragContext.y + event.getSceneY());

                }

            }
        };

        EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

            }
        };

    }

    public static void main(String[] args) {
        launch(args);
    }
}


