package com.rmnivnv.telegramcontest.model;

import java.util.List;

public class ColumnY extends Column {
    private List<Long> data;

    public ColumnY(String name, List<Long> data) {
        super(name);
        this.data = data;
    }

    public List<Long> getData() {
        return data;
    }
}
