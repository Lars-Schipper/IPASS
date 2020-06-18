package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {

    int numBoids = 200;
    double boidRadius = 5d;
    double boidMinDistance = boidRadius * 2d + 3;
    double boidMaxDistance = boidRadius * 2d + 80;
    double initialBaseVelocity = 1d;
    double velocityLimit = 5d;
    double movementToCenter = 0.0001;

    List<Boid> boids;

    static Random rnd = new Random();

    double sceneWidth = 1524;
    double sceneHeight = 768;

    Pane playfield;

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        playfield = new Pane();
        playfield.setPrefSize(sceneWidth, sceneHeight);

        root.setCenter(playfield);

        Scene scene = new Scene(root, sceneWidth, sceneHeight, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.show();

        // create boids
        createBoids();

        // add boids to scene
        playfield.getChildren().addAll(boids);

        // animation loop
        AnimationTimer loop = new AnimationTimer() {

            @Override
            public void handle(long now) {

                boids.forEach(Boid::move);
                boids.forEach(Boid::updateUI);

            }
        };

        loop.start();
    }

    private void createBoids() {

        boids = new ArrayList<>();

        // margin from top/left/bottom/right, so we have the boids initially more in the center
        double marginX = sceneWidth / 4;
        double marginY = sceneHeight / 4;

        for (int i = 0; i < numBoids; i++) {

            // random position around the center
            double x = rnd.nextDouble() * (sceneWidth - marginX * 2) + marginX;
            double y = rnd.nextDouble() * (sceneHeight - marginY * 2) + marginY;

            // initial random velocity depending on speed
            double v = Math.random() * 4 + initialBaseVelocity;

            Boid boid = new Boid(i, x, y, v);

            boids.add(boid);

        }

    }

    // Rule 1: Boids try to fly towards the centre of mass of neighbouring boids.
    public Point2D rule1(Boid boid) {

        Point2D pcj = new Point2D(0, 0);

        for( Boid neighbor: boids)  {

            if( boid == neighbor)
                continue;

            pcj = pcj.add( neighbor.position);

        }

        if( boids.size() > 1) {
            double div = 1d / (boids.size() - 10);
            pcj = pcj.multiply( div);
        }

        pcj = (pcj.subtract(boid.position)).multiply( movementToCenter);

        return pcj;
    }

    // Rule 2: Boids try to keep a small distance away from other objects (including other boids).
    public Point2D rule2(Boid boid) {

        Point2D c = new Point2D(0, 0);

        for( Boid neighbor: boids)  {

            if( boid == neighbor)
                continue;

            double distance = (neighbor.position.subtract(boid.position)).magnitude();

            if( distance < boidMinDistance) {
                c = c.subtract(neighbor.position.subtract(boid.position));

            }

        }

        return c;
    }

    // Rule 3: Boids try to match velocity with near boids.
    public Point2D rule3(Boid boid) {

        Point2D pvj = new Point2D(0, 0);

        for( Boid neighbor: boids)  {

            if( boid == neighbor)
                continue;

            pvj = pvj.add( neighbor.velocity);

        }

        if( boids.size() > 1) {
            double div = 1d / (boids.size() - 1);
            pvj = pvj.multiply( div);
        }

        pvj = (pvj.subtract(boid.velocity)).multiply(0.0625); // 0.125 = 1/8

        return pvj;
    }

    public class Boid extends Circle {

        int id;

        Point2D position;
        Point2D velocity;

        double v;

        // random color
        Color color = randomColor();

        public Boid(int id, double x, double y, double v) {

            this.id = id;
            this.v = v;

            position = new Point2D( x, y);
            velocity = new Point2D( v, v);

            setRadius( boidRadius);

            setStroke(color);
            setFill(color.deriveColor(1, 1, 1, 0.2));

        }

        public void move() {

            Point2D v1 = rule1(this);
            Point2D v2 = rule2(this);
            Point2D v3 = rule3(this);

            velocity = velocity
                    .add(v1)
                    .add(v2)
                    .add(v3)
            ;

            limitVelocity();

            position = position.add(velocity);

            constrainPosition();
        }

        private void limitVelocity() {

            double vlim = velocityLimit;

            if( velocity.magnitude() > vlim) {
                velocity = (velocity.multiply(1d/velocity.magnitude())).multiply( vlim);
            }

        }


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
                x = xMax;
            }
            else if( x > xMax) {
                x = xMin;
            }

            if( y < yMin) {
                y = yMax;
            }
            else if( y > yMax) {
                y = yMin;
            }

            position = new Point2D( x, y);
            velocity = new Point2D( vx, vy);

        }


        public void updateUI() {

            setCenterX(position.getX());
            setCenterY(position.getY());
        }
    }

    public static Color randomColor() {
        int range = 220;
        return Color.rgb((int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range));
    }

    public static void main(String[] args) {
        launch(args);
    }

}