package com.example.hexi.canvastest.model;

import java.util.List;

/**
 * Created by hexi on 15/8/28.
 */
public class Line {

    List<LineEntry> points;

    public Line(List<LineEntry> points) {
        this.points = points;
    }

    public List<LineEntry> getPoints() {
        return points;
    }

    public int size() {
        return points.size();
    }
}
