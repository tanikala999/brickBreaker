package brickbreaker;

public class BonusBall {
    int bonusBallX;
    int bonusBallY;
    int bonusBallXDir;
    int bonusBallYDir;

    public BonusBall(int paddleX, int paddleY, int dirX, int dirY) {
        bonusBallX = paddleX;
        bonusBallY = paddleY;
        bonusBallXDir = dirX;
        bonusBallYDir = dirY;
    }
}