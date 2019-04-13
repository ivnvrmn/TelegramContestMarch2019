package com.rmnivnv.telegramcontest.model;

public abstract class Column {
    private String name;

    Column(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
