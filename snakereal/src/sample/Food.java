package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;

import static sample.Main.*;
//Diese Klasse macht das Essen für die Schlange

public class Food extends Circle { //macht eine Kreisform
    int x;
    int y;
    private final Random random = new Random(); //allows drawing of random values
    /**
     * Position und Radius. Super ruft constructor von Objekt Circle auf
     * @param width wird durch 2 geteilt
     * @param height wird durch 2 geteilt -> ist Mitte des Spielfeldes
     */
    public Food(int width, int height){
        super(width*bodysize / 2, height * bodysize / 2, 1, Color.YELLOW);
    }

    /**
     * Returns boolean whether snake is at food
     * @param x Position x
     * @param y Position y
     * @return true or false
     */
    public boolean foundAt (int x, int y) {
        //Checks whether given position of snake is the same as food position
        // this.x/y ist für Position von Essen, x y wird von foundAt aufgerufen, ist Position Schlange
        return this.x == x && this.y == y;

    }

    /**
     * t = temporary; gibt x und y neue Werte und bewegt Essen.
     */
    public void moveFoodRandom(){
        int tx = random.nextInt(width);
        int ty = random.nextInt(height);
    }
}

