package com.rmnivnv.telegramcontest.model;

import java.util.List;

public class GraphDataY extends GraphData {
    private List<Long> points;

    public GraphDataY(String color, String name, ChartType chartType, List<Long> points) {
        super(color, name, chartType);
        this.points = points;
    }

    public List<Long> getPoints() {
        return points;
    }
}
