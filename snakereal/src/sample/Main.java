package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    //Variablen
    private static Random random = new Random();
    private static int speed = 5;
    private static List<Body> snake = new ArrayList<>();
    private static Dir direction = Dir.left; //Startrichtung geht nach links
    private static boolean gameOver = false;

    //4 verschiedene Richtungen, in welche sich die Schlange bewegen kann
    public enum Dir{
        left, right, up, down
    }

    //Anhand dieser 3 Variablen kann die grösse des Spielfeldes angegeben werden
    static int width = 40;
    static int height = 40;
    static int bodysize = 15;

    //food
    static int foodX = random.nextInt(width);
    static int foodY =random.nextInt(height);


    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        Canvas canvas = new Canvas(width * bodysize, height * bodysize);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        /*Man kann den AnimationTimer mit einem while-Loop vergleichen, welcher eine Methode handle hat,
        welche bei jedem Durchgnag ausgeführt wird. Die Methode enthällt zusätzlich noch die Variable now,
        welche die Anzahl Milisekunden seit dem 01.01.1970 angibt*/
        new AnimationTimer() {
            long lastTick = 0;

            public void handle(long now) {

                /*Am Anfang ist die Variable lastTick auf 0 gesetzt und deshalb muss die Funktion tick das erste Mal manuell
                ausgeführt werden*/
                if (lastTick == 0) {
                    lastTick = now;
                    tick(graphicsContext);
                    return;
                }

                /*Sorgt dafür, dass screen nur in gewissen Zeitabständen refreshed wird. Je höher die Variable Speed, desto
                höher die refreshrates und desto schneller bewegt sich die Schlange*/
                if (now - lastTick > 1000000000 / speed) {
                    lastTick = now;
                    tick(graphicsContext);
                }

            }
        }.start();


        Scene scene = new Scene(root, width * bodysize, height * bodysize);

        //Code wird ausgeführt, wenn eine Taste gedrückt wird
        //Schlange kann sowohl mit WASD als auch mit den Pfeiltasten kontrolliert werden
        //Die Schlange kann nicht in die entgegengesetzte Richtung laufen
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if ((keyEvent.getCode() == KeyCode.W || keyEvent.getCode() == KeyCode.UP) && direction != Dir.down){
                    direction = Dir.up;
            }
            if ((keyEvent.getCode() == KeyCode.D || keyEvent.getCode() == KeyCode.RIGHT) && direction != Dir.left){
                direction = Dir.right;
            }
            if ((keyEvent.getCode() == KeyCode.S || keyEvent.getCode() == KeyCode.DOWN) && direction != Dir.up){
                direction = Dir.down;
            }
            if ((keyEvent.getCode() == KeyCode.A || keyEvent.getCode() == KeyCode.LEFT) && direction != Dir.right){
                direction = Dir.left;
            }
            if (keyEvent.getCode() == KeyCode.ESCAPE){
                System.exit(0);
            }
        });

        //Am Anfang des Spiels werden manuell 3 Schlangen-Elemente kreiert
        snake.add(new Body(width / 2, height / 2));
        snake.add(new Body(width / 2, height / 2));
        snake.add(new Body(width / 2, height / 2));
        snake.add(new Body(width / 2, height / 2));



        primaryStage.setScene(scene);
        primaryStage.setTitle("SNAKE GAME");
        primaryStage.show();

    }


    //tick

    public static void tick(GraphicsContext graphicsContext) {

        //Game over text, wenn die Schlange in die Wand läuft
        if(gameOver){
            //exit screen
            //pause Animation
            graphicsContext.setFill(Color.PALEVIOLETRED);
            graphicsContext.fillRect(width * bodysize / 2 - 170, height * bodysize / 2-48, 300, 60);
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.setFont(new Font("", 50));
            graphicsContext.fillText("GAME OVER", width * bodysize / 2 - 160, height * bodysize / 2);
            return;

        }

        //Alle Schlangen-Elemente rücken (von hintern her) auf die Position des vorherigen Schlangen-Elements nach
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }


        //Der Kopf der Schlange bewegt sich in die entsprechende Richtung
        switch (direction){
            case up:
                snake.get(0).y--;
                if(snake.get(0).y<=0){
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if(snake.get(0).y>=height){
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if(snake.get(0).x<=0){
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if(snake.get(0).x>=width){
                    gameOver = true;
                }
                break;
        }
        //Funktion checkFood wird aufgerufen
        //und es wird geprüft, ob sich der Schlangenkopf mit einem anderen Schlangen-Element kreuzt
        checkFood();

        bitesTail();


        //Karierter Hintergrund
        //Um eine Verschiebung der verschiedenfarbigen Raster zu bekommen müssen zwei Loops eigeführt werden
        //Dabei wird immer in Zweierschritte iteriert

        for (int i = 0; i<height;i+=2) {
            for (int j = 0; j<width;j++) {
                if (j%2 == 0) {
                    graphicsContext.setFill(Color.LIGHTGRAY);
                }else {
                    graphicsContext.setFill(Color.DARKGRAY);
                }
                graphicsContext.fillRect(i * bodysize, j * bodysize, bodysize, bodysize);
            }
        }
        for (int i = 1; i<height;i+=2) {
            for (int j = 0; j<width;j++) {
                if (j%2 != 0) {
                    graphicsContext.setFill(Color.LIGHTGRAY);
                }else {
                    graphicsContext.setFill(Color.DARKGRAY);
                }
                graphicsContext.fillRect(i * bodysize, j * bodysize, bodysize, bodysize);
            }
        }


        /*score, speed muss minus 5, da die Geschwindigkeit am Anfang 5 war. Wenn man etwas isst,
         wird die Gschwindigkeit eins grösser, daher kann man so auch  Punkte zählen
         */
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.setFont(new Font("",30));
        graphicsContext.fillText("Score: "+(speed-5), 8, 30);

        //Grösse und Farbe vom Essen wird festgelegt und Essen wird generiert
        graphicsContext.setFill(Color.BLUEVIOLET);
        graphicsContext.fillOval(foodX *bodysize, foodY * bodysize, bodysize, bodysize);

        //Jedes Körper-Element der Schlange wird gefärbt. Danach ein kleineres Feld hineingezeichnet, um einen Schatten zu simulieren
        for (Body body : snake) {
            graphicsContext.setFill(Color.LIGHTGREEN);
            graphicsContext.fillRect(body.x * bodysize, body.y * bodysize, bodysize - 1, bodysize - 1);
            graphicsContext.setFill(Color.GREEN);
            graphicsContext.fillRect(body.x * bodysize, body.y * bodysize, bodysize - 2, bodysize - 2);
        }
        //Kopf erhält eine andere Farbe, Kopf ist immer an der Position snake.get(0).x/y.
        graphicsContext.setFill(Color.AQUAMARINE);
        graphicsContext.fillRect(snake.get(0).x * bodysize, snake.get(0).y * bodysize,bodysize - 1, bodysize - 1);
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.fillRect(snake.get(0).x * bodysize, snake.get(0).y * bodysize,bodysize - 2, bodysize - 2);
    }


    //Checks whether snake head is on position of food
        private static void checkFood() {
            if (snake.get(0).x == foodX && snake.get(0).y == foodY) {
                //grow snake
                snake.add(new Body(snake.get(snake.size() - 1).x, snake.get(snake.size() - 1).y));
                /*move food randomly, schaut ob das neue Essen in der Schlange regeneriert wird,
                falls dies so ist, wird die Variable Start aufgerufen und Essen wird neu an einem anderen Ort regeneriert*/
                start:
                while (true) {
                    foodX = random.nextInt(width);
                    foodY = random.nextInt(height);

                    for (Body body : snake) {
                        if (body.x == foodX && body.y == foodY) {
                            continue start;
                        }
                    }
                    speed++;
                    break;
                }
            }
        }

    private static void bitesTail(){
        int x = snake.get(0).x, y = snake.get(0).y;

        for (int i = 1; i < snake.size(); i++)    {
            if (x == snake.get(i).x && y == snake.get(i).y) {
                gameOver = true;
                return;
            }
        }


    }


    public static void main(String[] args) {

        launch(args);
    }
}
