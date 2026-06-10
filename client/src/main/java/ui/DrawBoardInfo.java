package ui;

import java.util.function.Predicate;

public record DrawBoardInfo(int rowNum, Predicate<Integer>rowCondition, int rowIter,
                            int colNum, Predicate<Integer> colCondition, int colIter) {
}
