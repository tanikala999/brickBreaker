package brickbreaker;

public class MainBall {
    private int ballX = 120;
    private int ballY = 350;
    private int ballXdir;
    private int ballYdir;

    public MainBall(int ballX, int ballY, int ballXdir, int ballYdir) {
        this.ballX = ballX;
        this.ballY = ballY;
        this.ballXdir = ballXdir;
        this.ballYdir = ballYdir;
    }

    public void setBallX(int ballX) {
        this.ballX = ballX;
    }
    public void setBallY(int ballY) {
        this.ballY = ballY;
    }
    public void setBallXdir(int ballXdir) {
        this.ballXdir = ballXdir;
    }
    public void setBallYdir(int ballYdir) {
        this.ballYdir = ballYdir;
    }

    public int getBallX() {
        return ballX;
    }
    public int getBallY() {
        return ballY;
    }
    public int getBallXdir() {
        return ballXdir;
    }
    public int getBallYdir() {
        return ballYdir;
    }
}
