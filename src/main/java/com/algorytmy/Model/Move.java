package com.algorytmy.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Data
@AllArgsConstructor
public class Move {
    private Integer x1;
    private Integer y1;
    private Integer x2;
    private Integer y2;
}
