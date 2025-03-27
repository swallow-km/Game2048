package org.example;

import lombok.Getter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
    private Model model;
    @Getter
    private View view;

    private static final int WINNING_TILE = 2048;

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this);
    }

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    public int getScore() {
        return model.score;
    }

    public void resetGame() {
        model.score = 0;
        model.maxTile = 0;
        view.isGameWon = false;
        view.isGameLost = false;
        model.resetGameTiles();
    }

    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
            resetGame();
        }
        if (!model.canMove()) {
            view.isGameLost = true;
        }
        if (!view.isGameWon && !view.isGameLost) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> model.left();
                case KeyEvent.VK_RIGHT -> model.right();
                case KeyEvent.VK_UP -> model.up();
                case KeyEvent.VK_DOWN -> model.down();
                case KeyEvent.VK_Z -> model.rollback();
                case KeyEvent.VK_R -> model.randomMove();
                case KeyEvent.VK_A -> model.autoMove();
            }
        }
        if (model.maxTile == WINNING_TILE) {
            view.isGameWon = true;
        }
        view.repaint();
    }
}
