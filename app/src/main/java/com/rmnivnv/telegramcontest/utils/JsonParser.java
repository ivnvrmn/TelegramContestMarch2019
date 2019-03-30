package com.rmnivnv.telegramcontest.utils;

import com.rmnivnv.telegramcontest.model.ChartData;

import java.util.List;

public interface JsonParser {

    List<ChartData> getChartsFromJsonFile();
}
