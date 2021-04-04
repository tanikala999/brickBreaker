package brickbreaker;

import javax.swing.*;

public class Main {
    public static void main (String[] args) {
        JFrame obj = new JFrame();
        Gameplay gameplay = new Gameplay(3, 7);
        obj.setBounds(10, 10, 700, 600);
        obj.setTitle("Breaker Game");
        obj.setResizable(false);
        obj.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        obj.add(gameplay);
        obj.setVisible(true);
    }
}
