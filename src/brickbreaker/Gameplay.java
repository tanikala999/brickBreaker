package brickbreaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks;
    private Timer timer;
    private int delay = 8;
    private int playerX = 310;
    private int ballX = 120;
    private int ballY = 350;
    private int ballXdir = (int) (Math.random() * (2 - (-2) + 1) + (-2));
    private int ballYdir = (int) (Math.random() * (2 - (-2) + 1) + (-2));
    private boolean bonusCaught = false;
    private MapGenerator map;
    boolean keyLeft = false;
    boolean keyRight = false;
    boolean gameStarted = false;

    //Bonus stuff
    private int bonusX = (int) (Math.random() * (682 - 10 + 1) + 10);
    private int bonusY = (int) (Math.random() * (-200 - (-500) + 1) + (-500));
    private BonusBall[] bonusBalls = new BonusBall[3];
    private boolean resetBonus = false;

    public Gameplay(int row, int col) {
        map = new MapGenerator(row, col);
        totalBricks = row * col;
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    private class BonusBall {
        int bonusBallX;
        int bonusBallY;
        int bonusBallXDir;
        int bonusBallYDir;
        int speedMultiplier = 2;

        BonusBall(int paddleX, int paddleY, int dirX, int dirY) {
            bonusBallX = paddleX;
            bonusBallY = paddleY;
            bonusBallXDir = dirX;
            bonusBallYDir = dirY;
        }
    }

    @Override
    public void paint(Graphics g) {
        //background
        g.setColor(Color.BLACK);
        g.fillRect(1,1, 692, 592);
        //drawing map
        map.draw((Graphics2D)g);
        //borders
        g.setColor(Color.CYAN);
        g.fillRect(0,0,3,592);
        g.fillRect(0,0,692, 3);
        g.fillRect(691,0,3,592);
        g.fillRect(0, 575, 692, 3);
        //scores
        g.setColor(Color.WHITE);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score: " + score, 570, 30);
        //paddle
        g.setColor(Color.CYAN);
        g.fillRect(playerX, 550, 90, 8);
        //ball
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(ballX, ballY, 20, 20);
        //bonus
        if(!resetBonus) {
            if (!bonusCaught) {
                g.setColor(Color.RED);
                g.fillOval(bonusX, bonusY, 20, 20);
            } else {
                //bonus balls drawing
                for (BonusBall ball : bonusBalls) {
                    g.setColor(Color.GREEN);
                    g.fillOval(ball.bonusBallX, ball.bonusBallY, 10, 10);
                }
            }
        }
        
        //when game just opened
        if (!gameStarted) {
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Press Enter to start ", 240, 325);
        }

        //Winning
        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            bonusCaught = false;
            bonusX = (int) (Math.random() * (682 - 10 + 1) + 10);
            bonusY = (int) (Math.random() * (-200 - (-500) + 1) + (-500));
            resetBonus = true;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You won!", 280, 300);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to restart ", 250, 350);
        }

        //Losing
        if (ballY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            bonusCaught = false;
            bonusX = (int) (Math.random() * (682 - 10 + 1) + 10);
            bonusY = (int) (Math.random() * (-200 - (-500) + 1) + (-500));
            resetBonus = true;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game over, score: " + score, 215, 300);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to restart ", 250, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();

        //Main ball set directions
        if (play) {
            while (ballXdir == 0 || ballYdir == 0) {
                ballXdir = (int) (Math.random() * (2 - (-2) + 1) + (-2));
                ballYdir = (int) (Math.random() * (2 - (-2) + 1) + (-2));
            }

            //Intersection with a paddle
            Rectangle paddleRect = new Rectangle(playerX, 550, 90, 8);
            Rectangle ballRect = new Rectangle(ballX, ballY, 20, 20);
            if (ballRect.intersects(paddleRect)) {
                if (ballX + 19 <= paddleRect.x || ballX + 1 >= paddleRect.x + paddleRect.width) {
                    ballXdir = -ballXdir;
                } else if (ballX + 20 == paddleRect.x || ballX == paddleRect.x + paddleRect.width) {
                    ballXdir = -ballXdir;
                    ballYdir = -ballYdir;
                } else {
                    ballYdir = -ballYdir;
                }
            }

            //Brick drawing and intersection with balls
            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);

                        //when the ball hits a brick
                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;
                            //TODO: change the ball speed after destroying one brick
                            if (ballX + 19 <= brickRect.x || ballX + 1 >= brickRect.x + brickRect.width) {
                                ballXdir = -ballXdir;
                            } else if (ballX + 20 == brickRect.x || ballX == brickRect.x + brickRect.width) {
                                ballXdir = -ballXdir;
                                ballYdir = -ballYdir;
                            } else {
                                ballYdir = -ballYdir;
                            }
                            break A;
                        }

                        //when the bonus ball hits a brick
                        if(bonusCaught) {
                            for(BonusBall ball : bonusBalls) {
                                if (new Rectangle(ball.bonusBallX, ball.bonusBallY, 10, 10).intersects(brickRect)) {
                                    map.setBrickValue(0, i, j);
                                    totalBricks--;
                                    score += 5;

                                    //TODO: Change a collision with bricks
                                    if (ball.bonusBallX + 9 <= brickRect.x || ball.bonusBallX + 1 >= brickRect.x + brickRect.width) {
                                        ball.bonusBallXDir = -ball.bonusBallXDir;
                                    } else if (ball.bonusBallX + 10 == brickRect.x || ball.bonusBallX == brickRect.x + brickRect.width) {
                                        ball.bonusBallXDir = -ball.bonusBallXDir;
                                        ball.bonusBallYDir = -ball.bonusBallYDir;
                                    } else {
                                        ball.bonusBallYDir = -ball.bonusBallYDir;
                                    }
                                    break A;
                                }
                            }
                        }
                    }
                }
            }

            //TODO: MAKE BALL MOVE HERE
            //Main ball moving
            ballX += ballXdir;
            ballY += ballYdir;
            if (ballX < 0)
                ballXdir = -ballXdir;
            if (ballY < 0)
                ballYdir = -ballYdir;
            if (ballX > 670)
                ballXdir = -ballXdir;

            //bonus balls logic (small green balls)
            if(bonusCaught) {
                //TODO: MAKE THIS A FUNCTION
                for(BonusBall ball : bonusBalls) {
                    //bonus balls paddle collision
                    if (new Rectangle(ball.bonusBallX, ball.bonusBallY, 10, 10).intersects
                            (new Rectangle(playerX, 550, 90, 8))) {
                        ball.bonusBallYDir = -ball.bonusBallYDir;
                    }
                    ball.bonusBallX += ball.bonusBallXDir;
                    ball.bonusBallY += ball.bonusBallYDir;
                    //Bonus balls-walls collision
                    if (ball.bonusBallX < 0)
                        ball.bonusBallXDir = -ball.bonusBallXDir;
                    if (ball.bonusBallY < 0)
                        ball.bonusBallYDir = -ball.bonusBallYDir;
                    if (ball.bonusBallX > 670)
                        ball.bonusBallXDir = -ball.bonusBallXDir;
                }

            //Intersection of the bonus (red) ball and paddle
            } else if (new Rectangle(playerX, 550, 90, 8).intersects
                    (new Rectangle(bonusX , bonusY, 20, 20))) {
                bonusCaught = true;
                //reset bonusBall direction
                bonusX = bonusY = 0;
                //set bonus balls to the center of the paddle, with random trajectories
                bonusBalls[0] = new BonusBall(playerX + 25, 530,
                        (int)Math.random() * (-2 - (-3) + 1) + (-3), (int)Math.random() * (-1 - (-4) + 1) + (-4));
                bonusBalls[1] = new BonusBall(playerX + 45, 520,
                        (int)Math.random() * (1 - (-1) + 1) + (-1) ,(int)Math.random() * (2 - (-2) + 1) + (-2));
                bonusBalls[2] = new BonusBall(playerX + 65, 530,
                        (int)Math.random() * (3 - (2) + 1) + (2), (int)Math.random() * (-1 - (-4) + 1) + (-4));
            } else {
                //Before red ball gets caught, it moves down
                bonusY += 1;
            }
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if(keyCode == KeyEvent.VK_RIGHT) {
            keyRight = true;
        }
        if(keyCode == KeyEvent.VK_LEFT) {
            keyLeft = true;
        }
        if (keyCode == KeyEvent.VK_ENTER) {
            //Start the game
            if (!play) {
                gameStarted = true;
                play = true;
                resetBonus = false;
                ballX = 120;
                ballY = 350;
                do {
                    ballXdir = (int) (Math.random() * (2 - (-2) + 1) + (-2));
                    ballYdir = (int) (Math.random() * (0 - (-2) + 1) + (-2));
                } while (ballXdir == 0 || ballYdir == 0);
                playerX = 310;
                score = 0;
                totalBricks = 21;
                map = new MapGenerator(3, 7);
                repaint();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if(keyCode == KeyEvent.VK_RIGHT)
            keyRight = false;
        if(keyCode == KeyEvent.VK_LEFT)
            keyLeft = false;
    }

    //Called every time screen is repainted, allows for smooth movement of paddle
    public void move() {
        if(keyRight) {
            if (playerX < 600) {
                //play = true;
                playerX += 10;
            }
        }

        if(keyLeft) {
            if(playerX > 0) {
                //play = true;
                playerX -= 10;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) { }

}

