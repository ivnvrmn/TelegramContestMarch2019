package com.rmnivnv.telegramcontest.model;

import java.util.List;

public class GraphDataX extends GraphData {
    private List<String> dates;

    public GraphDataX(String color, String name, ChartType chartType, List<String> dates) {
        super(color, name, chartType);
        this.dates = dates;
    }

    public List<String> getDates() {
        return dates;
    }
}
