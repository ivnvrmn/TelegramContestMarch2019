package com.rmnivnv.telegramcontest.model;

import java.util.List;

public class Column {
    private String name;
    private List<Long> data;

    public Column(String name, List<Long> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public List<Long> getData() {
        return data;
    }
}
