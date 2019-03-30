package com.rmnivnv.telegramcontest.model;

import java.util.List;
import java.util.Map;

public class ChartData {
    private List<Column> columns;
    private Map<String, ChartType> types;
    private Map<String, String> names;
    private Map<String, String> colors;

    public ChartData(List<Column> columns, Map<String, ChartType> types, Map<String, String> names,
                     Map<String, String> colors) {
        this.columns = columns;
        this.types = types;
        this.names = names;
        this.colors = colors;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Map<String, ChartType> getTypes() {
        return types;
    }

    public Map<String, String> getNames() {
        return names;
    }

    public Map<String, String> getColors() {
        return colors;
    }
}
