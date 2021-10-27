package brickbreaker;

import java.awt.*;
import java.util.Random;

public class MapGenerator {
    public int map[][];
    public int brickWidth;
    public int brickHeight;
    public int totalBricks = 0;

    public MapGenerator(int row, int col, int level) {
        map = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (level == 1) {
                    map[i][j] = 1;
                    totalBricks++;
                    brickHeight = 150/row;
                } else {
                    Random random = new Random();
                    double d = random.nextDouble();
                    if (d <= 0.25) {
                        map[i][j] = 0;
                    } else {
                        map[i][j] = 1;
                        totalBricks++;
                    }
                    brickHeight = 200/row;
                }
            }
        }
        brickWidth = 540/col;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    g.setColor(Color.WHITE);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}
