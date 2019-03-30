package com.rmnivnv.telegramcontest.model;

import java.util.List;

public class GraphData {
    private List<Long> points;
    private String color;
    private String name;
    private ChartType chartType;

    public GraphData(List<Long> points, String color, String name, ChartType chartType) {
        this.points = points;
        this.color = color;
        this.name = name;
        this.chartType = chartType;
    }

    public List<Long> getPoints() {
        return points;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public ChartType getChartType() {
        return chartType;
    }
}
