package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    @Getter
    private Move move;

    @Override
    public int compareTo(MoveEfficiency moveEfficiency) {
        int emptyTilesCompared = Integer.compare(numberOfEmptyTiles, moveEfficiency.numberOfEmptyTiles);
        if (emptyTilesCompared == 0) {
            return Integer.compare(score, moveEfficiency.score);
        }
        else {
            return emptyTilesCompared;
        }
    }
}
