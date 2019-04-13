package com.rmnivnv.telegramcontest.model;

public abstract class GraphData {
    private String color;
    private String name;
    private ChartType chartType;

    public GraphData(String color, String name, ChartType chartType) {
        this.color = color;
        this.name = name;
        this.chartType = chartType;
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
