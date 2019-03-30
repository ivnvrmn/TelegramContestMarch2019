package com.rmnivnv.telegramcontest;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.rmnivnv.telegramcontest.model.GraphData;
import com.rmnivnv.telegramcontest.utils.ThemeChecker;

import java.util.List;

public class GraphNamesAdapter extends ArrayAdapter<GraphData> {

    private List<GraphData> graphs;
    private int[][] checkBoxStates;
    private ThemeChecker themeChecker;
    private int textPadding;

    GraphNamesAdapter(Context context, List<GraphData> graphs, ThemeChecker themeChecker) {
        super(context, R.layout.graph_name_item, graphs);
        this.graphs = graphs;
        this.themeChecker = themeChecker;
        checkBoxStates = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };
        textPadding = context.getResources().getDimensionPixelSize(R.dimen.check_box_text_padding);
    }

    private static class ViewHolder {
        CheckBox checkBox;
    }

    void setGraphs(List<GraphData> graphs) {
        this.graphs = graphs;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.graph_name_item, parent, false);
            viewHolder.checkBox = convertView.findViewById(R.id.check_box);
            viewHolder.checkBox.setChecked(true);
            viewHolder.checkBox.setPadding(viewHolder.checkBox.getPaddingLeft() + textPadding,
                    viewHolder.checkBox.getPaddingTop(),
                    viewHolder.checkBox.getPaddingRight(),
                    viewHolder.checkBox.getPaddingBottom());

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GraphData graphData = graphs.get(position);
        int checkBoxColor = Color.parseColor(graphData.getColor());
        viewHolder.checkBox.setButtonTintList(createColorStateList(checkBoxColor));
        viewHolder.checkBox.setText(graphData.getName());

        int textColor;
        if (themeChecker.isDarkTheme()) {
            textColor = getContext().getResources().getColor(R.color.check_box_text_color_dark);
        } else {
            textColor = getContext().getResources().getColor(R.color.check_box_text_color);
        }
        viewHolder.checkBox.setTextColor(textColor);

        return convertView;
    }

    @Override
    public int getCount() {
        return graphs.size();
    }

    private ColorStateList createColorStateList(int color) {
        return new ColorStateList(checkBoxStates, toColorArray(color));
    }

    private int[] toColorArray(int color) {
        return new int[]{color, color};
    }
}
