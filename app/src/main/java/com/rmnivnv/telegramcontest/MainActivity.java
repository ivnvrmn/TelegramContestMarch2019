package com.rmnivnv.telegramcontest;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.rmnivnv.telegramcontest.model.ChartData;
import com.rmnivnv.telegramcontest.model.ChartType;
import com.rmnivnv.telegramcontest.model.Column;
import com.rmnivnv.telegramcontest.model.ColumnX;
import com.rmnivnv.telegramcontest.model.ColumnY;
import com.rmnivnv.telegramcontest.model.GraphData;
import com.rmnivnv.telegramcontest.model.GraphDataX;
import com.rmnivnv.telegramcontest.model.GraphDataY;
import com.rmnivnv.telegramcontest.utils.JsonParser;
import com.rmnivnv.telegramcontest.utils.JsonParserImpl;
import com.rmnivnv.telegramcontest.utils.ThemeChecker;
import com.rmnivnv.telegramcontest.utils.ThemeCheckerImpl;
import com.rmnivnv.telegramcontest.views.ChartSliderView;
import com.rmnivnv.telegramcontest.views.ChartView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private ThemeChecker themeChecker;
    private Toolbar toolbar;
    private ViewGroup mainLayout;
    private ViewGroup backgroundLayout;
    private ChartView chartView;
    private ChartSliderView chartSliderView;
    private TextView graphTitle;
    private ListView graphNamesList;
    private List<ChartData> chartList;
    private List<String> chartNames = new ArrayList<>();
    private GraphNamesAdapter graphNamesAdapter;
    private List<GraphData> mainCharts;
    private List<GraphData> nameCharts;
    private int currentGraph = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        themeChecker = new ThemeCheckerImpl(getPreferences(MODE_PRIVATE));

        initViews();
        setActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title_text_color));
        setTitle(getString(R.string.main_title));
        setTheme();

        JsonParser jsonParser = new JsonParserImpl(this);
        chartList = jsonParser.getChartsFromJsonFile();

        showNextGraph();

        graphNamesAdapter = new GraphNamesAdapter(this, nameCharts, themeChecker);
        graphNamesList.setAdapter(graphNamesAdapter);
        graphNamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (((CheckBox) view).isChecked() && mainCharts.size() == 2) {
                    chartView.showEmptyText();
                }
                if (mainCharts.size() == 1) {
                    chartView.hideEmptyText();
                }
                onGraphNameClicked((CheckBox) view, position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_prev:
                showPreviousGraph();
                graphNamesAdapter.setGraphs(nameCharts);
                return true;
            case R.id.menu_next:
                showNextGraph();
                graphNamesAdapter.setGraphs(nameCharts);
                return true;
            case R.id.menu_dark:
                switchTheme();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        mainLayout = findViewById(R.id.main_layout);
        backgroundLayout = findViewById(R.id.background_layout);
        graphNamesList = findViewById(R.id.graph_names_list);
        graphTitle = findViewById(R.id.graph_title);

        chartView = findViewById(R.id.chart_view);
        chartView.setThemeChecker(themeChecker);
        chartView.setColorsByTheme();

        chartSliderView = findViewById(R.id.chart_slider_view);
        chartSliderView.setThemeChecker(themeChecker);
        chartSliderView.setColorsByTheme();
    }

    private void setTheme() {
        if (themeChecker.isDarkTheme()) {
            enableDarkTheme();
        } else {
            enableLightTheme();
        }
    }

    private void showPreviousGraph() {
        if (currentGraph == 0) {
            currentGraph = chartList.size() - 1;
        } else {
            currentGraph--;
        }
        setUpdatedGraph();
    }

    private void setUpdatedGraph() {
        mainCharts = convertToGraphsList(chartList.get(currentGraph));
        nameCharts = convertToGraphsNamesList(chartList.get(currentGraph));
        chartView.setGraphs(mainCharts);
        chartSliderView.setGraphs(mainCharts, chartView);

        String title = getString(R.string.followers) + " #" + currentGraph;
        graphTitle.setText(title);
    }

    private void showNextGraph() {
        if (currentGraph == chartList.size() - 1) {
            currentGraph = 0;
        } else {
            currentGraph++;
        }
        setUpdatedGraph();
    }

    private void onGraphNameClicked(CheckBox checkBox, int position) {
        setChecked(checkBox);

        GraphData clickedGraph = nameCharts.get(position);
        if (isOnMainView(clickedGraph)) {
            removeFromMainCharts(clickedGraph);
            chartView.setGraphToDelete((GraphDataY) clickedGraph);
        } else {
            chartView.addGraph((GraphDataY) clickedGraph);
            mainCharts.add(clickedGraph);
        }

        chartView.update();
        chartSliderView.invalidate();
    }

    private void setChecked(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
    }

    private List<GraphData> convertToGraphsList(ChartData chartData) {
        List<GraphData> graphs = new ArrayList<>();

        for (Map.Entry<String, ChartType> entry : chartData.getTypes().entrySet()) {
            String name = entry.getKey();
            String color = chartData.getColors().get(name);
            String graphName = chartData.getNames().get(name);
            ChartType chartType = entry.getValue();
            if (chartType.equals(ChartType.X)) {
                List<String> dates = getDates(chartData.getColumns());
                graphs.add(new GraphDataX(color, graphName, entry.getValue(), dates));
            } else {
                List<Long> points = getPointsByName(name, chartData.getColumns());
                graphs.add(new GraphDataY(color, graphName, entry.getValue(), points));
            }
        }

        return graphs;
    }

    private List<GraphData> convertToGraphsNamesList(ChartData chartData) {
        List<GraphData> graphs = new ArrayList<>();
        chartNames.clear();

        for (Map.Entry<String, ChartType> entry : chartData.getTypes().entrySet()) {
            if (ChartType.LINE.equals(entry.getValue())) {
                chartNames.add(entry.getKey());
            }
        }

        for (String name : chartNames) {
            String color = chartData.getColors().get(name);
            String graphName = chartData.getNames().get(name);
            List<Long> points = getPointsByName(name, chartData.getColumns());
            graphs.add(new GraphDataY(color, graphName, ChartType.LINE, points));
        }

        return graphs;
    }

    private List<Long> getPointsByName(String name, List<Column> columns) {
        for (Column column : columns) {
            if (name.equals(column.getName())) {
                return ((ColumnY) column).getData();
            }
        }
        return null;
    }

    private List<String> getDates(List<Column> columns) {
        for (Column column : columns) {
            if (column instanceof ColumnX) {
                return ((ColumnX) column).getDates();
            }
        }
        return null;
    }

    private boolean isOnMainView(GraphData graph) {
        for (GraphData graphData : mainCharts) {
            if (graphData.getChartType().equals(ChartType.LINE) &&
                    graphData.getName().equals(graph.getName())) {
                return true;
            }
        }
        return false;
    }

    private void removeFromMainCharts(GraphData graph) {
        Iterator<GraphData> iterator = mainCharts.iterator();
        while (iterator.hasNext()) {
            GraphData graphData = iterator.next();
            if (graphData.getChartType().equals(ChartType.LINE) &&
                    graphData.getName().equals(graph.getName())) {
                iterator.remove();
            }
        }
    }

    private void switchTheme() {
        if (themeChecker.isDarkTheme()) {
            themeChecker.setDarkTheme(false);
            enableLightTheme();
        } else {
            themeChecker.setDarkTheme(true);
            enableDarkTheme();
        }
        chartView.setColorsByTheme();
        chartView.invalidate();
        chartSliderView.setColorsByTheme();
        chartSliderView.invalidate();

        graphNamesAdapter.notifyDataSetChanged();
    }

    private void enableLightTheme() {
        mainLayout.setBackgroundColor(getResources().getColor(R.color.main_background_color));
        backgroundLayout.setBackgroundColor(getResources().getColor(R.color.background_color));
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.color_primary)));
    }

    private void enableDarkTheme() {
        mainLayout.setBackgroundColor(getResources().getColor(R.color.main_background_color_dark));
        backgroundLayout.setBackgroundColor(getResources().getColor(R.color.background_color_dark));
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.color_primary_dark_theme)));
    }
}
