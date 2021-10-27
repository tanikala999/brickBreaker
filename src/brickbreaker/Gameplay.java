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
    private Timer timer;
    private int delay = 8;
    private int playerX = 310;
    private MainBall mainBall = new MainBall(120, 350, 0, 0);
    private boolean speedMultiplier = false;
    private boolean bonusCaught = false;
    private MapGenerator map;
    boolean keyLeft = false;
    boolean keyRight = false;
    boolean gameStarted = false;
    int level = 1;

    //Bonus stuff
    private int bonusX = (int) (Math.random() * (682 - 10 + 1) + 10);
    private int bonusY = (int) (Math.random() * (-200 - (-500) + 1) + (-500));
    private BonusBall[] bonusBalls = new BonusBall[3];
    private boolean resetBonus = false;

    public Gameplay(int row, int col) {
        map = new MapGenerator(row, col, level);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
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
        g.fillOval(mainBall.getBallX(), mainBall.getBallY(), 20, 20);
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

        //Ball speed increase-decrease text
        if (!speedMultiplier && play) {
            g.setColor(Color.white);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Up Arrow to increase the speed of the ball", 30, 30);
        }
        if (speedMultiplier && play) {
            g.setColor(Color.white);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Down Arrow to increase the speed of the ball", 30, 30);
        }
        
        //when game just opened
        if (!gameStarted) {
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Press Enter to start ", 240, 325);
        }
        //Winning-Losing
        if (map.totalBricks <= 0) {
            gameDone(g, false);
            level = 2;
        } else if (mainBall.getBallY() > 570) {
            gameDone(g, true);
            level = 1;
        }

        g.dispose();
    }

    //Reset all the ball positions and bricks
    public void gameDone(Graphics g, boolean isLost) {
        play = false;
        speedMultiplier = false;
        mainBall.setBallXdir(0);
        mainBall.setBallYdir(0);
        bonusCaught = false;
        bonusX = (int) (Math.random() * (682 - 10 + 1) + 10);
        bonusY = (int) (Math.random() * (-200 - (-500) + 1) + (-500));
        resetBonus = true;
        g.setColor(Color.RED);
        g.setFont(new Font("serif", Font.BOLD, 30));
        if (isLost)
            g.drawString("Game over, score: " + score, 215, 300);
        else
            g.drawString("You won!", 280, 300);
        g.setFont(new Font("serif", Font.BOLD, 20));
        if (level != 1)
            g.drawString("Press Enter to start the next level" , 200, 350);
        else
            g.drawString("Press Enter to restart ", 250, 350);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        timer.start();

        //Main ball set directions
        if (play) {
            while (mainBall.getBallXdir() == 0 || mainBall.getBallYdir() == 0) {
                mainBall.setBallXdir((int) (Math.random() * (2 - (-2) + 1) + (-2)));
                mainBall.setBallYdir((int) (Math.random() * (2 - (-2) + 1) + (-2)));
            }
            //Intersection with a paddle
            Rectangle paddleRect = new Rectangle(playerX, 550, 90, 8);
            Rectangle ballRect = new Rectangle(mainBall.getBallX(),mainBall.getBallY(), 20, 20);
            if (ballRect.intersects(paddleRect)) {
                if (mainBall.getBallX() + 19 <= paddleRect.x || mainBall.getBallX() + 1 >= paddleRect.x + paddleRect.width) {
                    mainBall.setBallXdir(-mainBall.getBallXdir());
                } else if (mainBall.getBallX() == paddleRect.x || mainBall.getBallX() == paddleRect.x + paddleRect.width) {
                    mainBall.setBallXdir(-mainBall.getBallXdir());
                    mainBall.setBallYdir(-mainBall.getBallYdir());
                } else {
                    mainBall.setBallYdir(-mainBall.getBallYdir());
                }
            }

            //Brick drawing and intersection with balls
            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;

                        Rectangle brickRect = new Rectangle(brickX, brickY, map.brickWidth, map.brickHeight);

                        //when the ball hits a brick
                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            map.totalBricks--;
                            score += 5;
                            if (mainBall.getBallX() + 19 <= brickRect.x || mainBall.getBallX() + 1 >= brickRect.x + brickRect.width) {
                                mainBall.setBallXdir(-mainBall.getBallXdir());
                            } else if (mainBall.getBallX() + 20 == brickRect.x || mainBall.getBallX() == brickRect.x + brickRect.width) {
                                mainBall.setBallXdir(-mainBall.getBallXdir());
                                mainBall.setBallYdir(-mainBall.getBallYdir());
                            } else {
                                mainBall.setBallYdir(-mainBall.getBallYdir());
                            }
                            break A;
                        }

                        //when the bonus ball hits a brick
                        if(bonusCaught) {
                            for(BonusBall ball : bonusBalls) {
                                if (new Rectangle(ball.bonusBallX, ball.bonusBallY, 10, 10).intersects(brickRect)) {
                                    map.setBrickValue(0, i, j);
                                    map.totalBricks--;
                                    score += 5;

                                    //TODO: Fix a collision with bricks
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

            //Main ball moving
            mainBall.setBallX(mainBall.getBallX() + mainBall.getBallXdir());
            mainBall.setBallY(mainBall.getBallY() + mainBall.getBallYdir());
            if (mainBall.getBallX() < 0)
                mainBall.setBallXdir(-mainBall.getBallXdir());
            if (mainBall.getBallY() < 0)
                mainBall.setBallYdir(-mainBall.getBallYdir());
            if (mainBall.getBallX() > 670)
                mainBall.setBallXdir(-mainBall.getBallXdir());

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
                mainBall.setBallX(120);
                mainBall.setBallY(350);
                do {
                    mainBall.setBallXdir((int) (Math.random() * (2 - (-2) + 1) + (-2)));
                    mainBall.setBallYdir((int) (Math.random() * (0 - (-2) + 1) + (-2)));
                } while (mainBall.getBallXdir() == 0 || mainBall.getBallYdir() == 0);
                playerX = 310;
                int row = 3;
                int col = 7;
                if (level != 1) {
                    map = new MapGenerator(row + level - 1, col + level - 1, level);
                } else {
                    map = new MapGenerator(row, col, level);
                    score = 0;
                }
                repaint();
            }
        }

        //Increase the speed of the main ball
        if (keyCode == KeyEvent.VK_UP) {
            if (play && !speedMultiplier) {
                speedMultiplier = true;
                mainBall.setBallXdir(mainBall.getBallXdir() * 2);
                mainBall.setBallYdir(mainBall.getBallYdir() * 2);
            }
        }
        //Decrease the speed of the mail ball
        if (keyCode == KeyEvent.VK_DOWN) {
            if (play && speedMultiplier) {
                speedMultiplier = false;
                mainBall.setBallXdir(mainBall.getBallXdir() / 2);
                mainBall.setBallYdir(mainBall.getBallYdir() / 2);
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
                playerX += 10;
            }
        }
        if(keyLeft) {
            if(playerX > 0) {
                playerX -= 10;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) { }
}

