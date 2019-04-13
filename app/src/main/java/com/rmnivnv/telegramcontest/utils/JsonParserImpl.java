package com.rmnivnv.telegramcontest.utils;

import android.content.Context;

import com.rmnivnv.telegramcontest.model.ChartData;
import com.rmnivnv.telegramcontest.model.ChartType;
import com.rmnivnv.telegramcontest.model.Column;
import com.rmnivnv.telegramcontest.model.ColumnX;
import com.rmnivnv.telegramcontest.model.ColumnY;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JsonParserImpl implements JsonParser {

    private static final String JSON_FILE = "chart_data.json";
    private static final String KEY_COLUMNS = "columns";
    private static final String KEY_TYPES = "types";
    private static final String KEY_NAMES = "names";
    private static final String KEY_COLORS = "colors";
    private static final String DATE_FORMAT_PATTERN = "MMM dd";
    private Context context;
    private Date date;
    private SimpleDateFormat dateFormat;

    public JsonParserImpl(Context context) {
        this.context = context;
        date = new Date();
        dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.US);
    }

    @Override
    public List<ChartData> getChartsFromJsonFile() {
        List<ChartData> result = new ArrayList<>();
        try {
            String json = loadJSONFromAsset();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                List<Column> columns = extractColumns(jsonObject);
                Map<String, ChartType> types = extractTypes(jsonObject);
                Map<String, String> names = extractNames(jsonObject);
                Map<String, String> colors = extractColors(jsonObject);

                result.add(new ChartData(columns, types, names, colors));
            }
        } catch (JSONException ex) {
            return null;
        }
        return result;
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open(JSON_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private List<Column> extractColumns(JSONObject jsonObject) throws JSONException{
        List<Column> columnList = new ArrayList<>();

        JSONArray columnsJsonArray = jsonObject.getJSONArray(KEY_COLUMNS);
        for (int j = 0; j < columnsJsonArray.length(); j++) {
            JSONArray columnPointsJsonArray = columnsJsonArray.getJSONArray(j);
            String name = columnPointsJsonArray.getString(0);
            columnPointsJsonArray.remove(0);
            if (name.equals("x")) {
                List<String> dates = new ArrayList<>();
                for (int k = 0; k < columnPointsJsonArray.length(); k++) {
                    date.setTime(columnPointsJsonArray.getLong(k));
                    dates.add(dateFormat.format(date));
                }
                columnList.add(new ColumnX(name, dates));
            } else {
                List<Long> points = new ArrayList<>();
                for (int k = 0; k < columnPointsJsonArray.length(); k++) {
                    points.add(columnPointsJsonArray.getLong(k));
                }
                columnList.add(new ColumnY(name, points));
            }
        }
        return columnList;
    }

    private Map<String, ChartType> extractTypes(JSONObject jsonObject) throws JSONException {
        Map<String, ChartType> types = new HashMap<>();

        JSONObject typesObject = jsonObject.getJSONObject(KEY_TYPES);
        Iterator<String> iterator = typesObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            ChartType chartType = ChartType.valueOf(typesObject.getString(key).toUpperCase());
            types.put(key, chartType);
        }
        return types;
    }

    private Map<String, String> extractNames(JSONObject jsonObject) throws JSONException {
        Map<String, String> names = new HashMap<>();

        JSONObject namesObject = jsonObject.getJSONObject(KEY_NAMES);
        Iterator<String> iterator = namesObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String name = namesObject.getString(key);
            names.put(key, name);
        }

        return names;
    }

    private Map<String, String> extractColors(JSONObject jsonObject) throws JSONException {
        Map<String, String> colors = new HashMap<>();

        JSONObject colorsObject = jsonObject.getJSONObject(KEY_COLORS);
        Iterator<String> iterator = colorsObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String color = colorsObject.getString(key);
            colors.put(key, color);
        }

        return colors;
    }
}
