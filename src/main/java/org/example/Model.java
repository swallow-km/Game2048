package org.example;

import lombok.Getter;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    @Getter
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
    int score;
    int maxTile;

    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = false;

    public Model() {
        resetGameTiles();
    }

    void resetGameTiles() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH ; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            Tile tile = emptyTiles.get((int)(Math.random() * emptyTiles.size()));
            tile.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()) {
                    emptyTiles.add(gameTiles[i][j]);
                }
            }
        }
        return emptyTiles;
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean isChaged = false;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].isEmpty()) {
                for (int j = i + 1; j < tiles.length; j++) {
                    if (!tiles[j].isEmpty()) {
                        tiles[i].value = tiles[j].value;
                        tiles[j].value = 0;
                        isChaged = true;
                        break;
                    }
                }
            }
        }
        return isChaged;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean isChanged = false;
        for (int i = 1; i < tiles.length; i++) {
            if (!tiles[i].isEmpty() && tiles[i].value == tiles[i - 1].value) {
                tiles[i - 1].value = tiles[i].value << 1;
                isChanged = true;
                score += tiles[i - 1].value;
                if (tiles[i - 1].value > maxTile) {
                    maxTile = tiles[i - 1].value;
                }
                tiles[i].value = 0;
                i++;
            }
        }

        compressTiles(tiles);

        return isChanged;
    }

    public void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }

        if (isChanged) {
            addTile();
            isSaveNeeded = true;
        }
    }

    public void right() {
        saveState(gameTiles);
        rotate();
        rotate();
        left();
        rotate();
        rotate();
    }

    public void up() {
        saveState(gameTiles);
        rotate();
        rotate();
        rotate();
        left();
        rotate();
    }

    public void down() {
        saveState(gameTiles);
        rotate();
        left();
        rotate();
        rotate();
        rotate();
    }

    private void rotate() {
        Tile[][] tempTiles = new Tile[FIELD_WIDTH][];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            tempTiles[i] = Arrays.copyOf(gameTiles[i], FIELD_WIDTH);
        }

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j <FIELD_WIDTH ; j++) {
                gameTiles[i][j] = tempTiles[FIELD_WIDTH - 1 - j][i];
            }
        }
    }

    public boolean canMove() {
        if (getEmptyTiles().isEmpty()) {
            for (int i = 0; i < FIELD_WIDTH - 1; i++) {
                for (int j = 0; j < FIELD_WIDTH - 1; j++) {
                    if (gameTiles[i][j].value == gameTiles[i][j + 1].value || gameTiles[i][j].value == gameTiles[i+1][j].value) {
                        return true;
                    }
                }
            }
        }
        else {
            return true;
        }
        return false;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] tempTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tempTiles[i][j] = new Tile(gameTiles[i][j].value);
            }
        }
        previousStates.push(tempTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void  randomMove() {
        switch ((int)(Math.random() * 4)) {
            case 0 -> left();
            case 1 -> up();
            case 2 -> right();
            case 3 -> down();
        }
    }

    private boolean hasBoardChanged() {
        boolean isChanged = false;
        Tile[][] prevTiles = previousStates.peek();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value != prevTiles[i][j].value) {
                    isChanged = true;
                    break;
                }
            }
        }
        return isChanged;
    }

    private MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency = null;
        move.move();
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        else {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        }

        rollback();

        return moveEfficiency;
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.add(getMoveEfficiency(this::left));
        queue.add(getMoveEfficiency(this::right));
        queue.add(getMoveEfficiency(this::up));
        queue.add(getMoveEfficiency(this::down));

        queue.poll().getMove().move();
    }
}