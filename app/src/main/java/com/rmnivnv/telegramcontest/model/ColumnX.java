package com.rmnivnv.telegramcontest.model;

import java.util.List;

public class ColumnX extends Column {
    private List<String> dates;

    public ColumnX(String name, List<String> dates) {
        super(name);
        this.dates = dates;
    }

    public List<String> getDates() {
        return dates;
    }
}
