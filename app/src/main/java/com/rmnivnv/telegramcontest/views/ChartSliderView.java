package com.rmnivnv.telegramcontest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rmnivnv.telegramcontest.R;
import com.rmnivnv.telegramcontest.ScaleListener;
import com.rmnivnv.telegramcontest.model.ChartType;
import com.rmnivnv.telegramcontest.model.GraphData;
import com.rmnivnv.telegramcontest.model.GraphDataY;
import com.rmnivnv.telegramcontest.model.TouchRangeType;
import com.rmnivnv.telegramcontest.utils.ThemeChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartSliderView extends View {

    private static final int INVALID_POINTER = -1;

    private ThemeChecker themeChecker;
    private List<GraphData> graphs = new ArrayList<>();
    private Paint paint;
    private Path path;
    private ScaleListener scaleListener;
    private TouchRangeType touchRange;
    private TouchRangeType secondaryTouchRange;

    private int width;
    private int height;
    private int backgroundRectColor;
    private int selectRectColor;
    private int primaryPointerId = INVALID_POINTER;
    private int secondaryPointerId = INVALID_POINTER;
    private float graphStrokeWidth;
    private float selectRectSideWidth;
    private float selectRectTopWidth;
    private float addedTouchArea;
    private float rectSize;
    private float minRectSize;
    private float leftDragArea;
    private float rightDragArea;
    private float leftPosition;
    private float rightPosition;
    private float leftPercent = 0;
    private float rightPercent = 0;


    public ChartSliderView(Context context) {
        super(context);
        init();
    }

    public ChartSliderView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    public ChartSliderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();

        selectRectColor = getResources().getColor(R.color.slider_select_rect_color);
        graphStrokeWidth = getResources().getDimensionPixelSize(R.dimen.chart_slider_graph_thickness);
        selectRectSideWidth = getResources().getDimensionPixelSize(R.dimen.select_rect_side_width);
        selectRectTopWidth = getResources().getDimensionPixelSize(R.dimen.select_rect_top_width);
        addedTouchArea = getResources().getDimensionPixelSize(R.dimen.added_touch_area);
    }

    public void setThemeChecker(ThemeChecker themeChecker) {
        this.themeChecker = themeChecker;
    }

    public void setColorsByTheme() {
        if (themeChecker.isDarkTheme()) {
            backgroundRectColor = getResources().getColor(R.color.slider_background_rect_color_dark);
        } else {
            backgroundRectColor = getResources().getColor(R.color.slider_background_rect_color);
        }
    }

    public void setGraphs(List<GraphData> graphs, ScaleListener scaleListener) {
        this.graphs = graphs;
        this.scaleListener = scaleListener;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        leftPosition = width * 0.75f;
        rightPosition = width;
        minRectSize = width * 0.2f;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (graphs.size() > 0) {
            long maxY = findMaxValueFromGraphs();
            float one = (float) height / (float) maxY;

            //draw graph
            setPaintToGraphs();
            for (GraphData graph : graphs) {
                if (graph.getChartType().equals(ChartType.LINE)) {
                    float totalPoints = ((GraphDataY) graph).getPoints().size();
                    float sectionDistance = width / (totalPoints - 1);
                    float startX = 0;
                    float startY = height - one * ((GraphDataY) graph).getPoints().get(0);

                    path.reset();
                    path.moveTo(startX, startY);
                    for (int i = 1; i < totalPoints; i++) {
                        float top = height - (one * ((GraphDataY) graph).getPoints().get(i));
                        path.lineTo(startX + sectionDistance, top);
                        startX = startX + sectionDistance;
                    }

                    paint.setColor(Color.parseColor(graph.getColor()));
                    canvas.drawPath(path, paint);
                }
            }
        }

        //draw select rect
        setPaintToDrawSelectRect();

        //draw left side
        canvas.drawRect(leftPosition, 0, leftPosition + selectRectSideWidth, height, paint);
        //draw right side
        canvas.drawRect(rightPosition - selectRectSideWidth, 0, rightPosition, height, paint);
        //draw top side
        canvas.drawRect(leftPosition + selectRectSideWidth, 0, rightPosition - selectRectSideWidth,
                selectRectTopWidth, paint);
        //draw bottom side
        canvas.drawRect(leftPosition + selectRectSideWidth, height - selectRectTopWidth,
                rightPosition - selectRectSideWidth, height, paint);

        //draw background rect
        setPaintToDrawBackgroundRect();

        //draw left side
        if (leftPosition > 0) {
            canvas.drawRect(0, 0, leftPosition, height, paint);
        }
        //draw right side
        if (rightPosition < width) {
            canvas.drawRect(rightPosition, 0, width, height, paint);
        }
    }

    private long findMaxValueFromGraphs() {
        long maxValue = 0;
        for (GraphData graph : graphs) {
            if (graph.getChartType().equals(ChartType.LINE)) {
                long temp = Collections.max(((GraphDataY) graph).getPoints());
                if (temp > maxValue) {
                    maxValue = temp;
                }
            }
        }
        return maxValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        float secondaryX;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                primaryPointerId = event.getPointerId(0);

                if (isLargerThenRightSide(x)) {
                    touchRange = TouchRangeType.OVER_RIGHT;
                } else if (isSmallerThenLeftSide(x)) {
                    touchRange = TouchRangeType.OVER_LEFT;
                } else if (isSelectRectLeftSide(x)) {
                    touchRange = TouchRangeType.LEFT_SIDE;
                } else if (isSelectRectRightSide(x)) {
                    touchRange = TouchRangeType.RIGHT_SIDE;
                } else if (isInsideSelectRect(x)) {
                    touchRange = TouchRangeType.INSIDE_RECT;

                    rectSize = rightPosition - leftPosition;
                    leftDragArea = x - leftPosition;
                    rightDragArea = rightPosition - x;
                } else {
                    touchRange = TouchRangeType.OUT_OF_RANGE;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (touchRange == TouchRangeType.INSIDE_RECT) return false;

                secondaryPointerId = event.getPointerId(event.getActionIndex());
                secondaryX = event.getX(secondaryPointerId);

                if (isLargerThenRightSide(secondaryX)) {
                    secondaryTouchRange = TouchRangeType.OVER_RIGHT;
                } else if (isSmallerThenLeftSide(secondaryX)) {
                    secondaryTouchRange = TouchRangeType.OVER_LEFT;
                } else if (isSelectRectLeftSide(secondaryX)) {
                    secondaryTouchRange = TouchRangeType.LEFT_SIDE;
                } else if (isSelectRectRightSide(secondaryX)) {
                    secondaryTouchRange = TouchRangeType.RIGHT_SIDE;
                } else {
                    secondaryTouchRange = TouchRangeType.OUT_OF_RANGE;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (primaryPointerId > INVALID_POINTER && secondaryPointerId > INVALID_POINTER) {
                    //two fingers
                    calculatePositions(touchRange, event.getX(event.findPointerIndex(primaryPointerId)));
                    calculatePositions(secondaryTouchRange, event.getX(event.findPointerIndex(secondaryPointerId)));
                } else {
                    //one finger
                    calculatePositions(touchRange, x);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if(event.getPointerId(event.getActionIndex()) == primaryPointerId) {
                    primaryPointerId = secondaryPointerId;
                }
                secondaryPointerId = INVALID_POINTER;
                break;
            case MotionEvent.ACTION_UP:
                primaryPointerId = INVALID_POINTER;
                break;
        }
        float leftPercent = leftPosition * 100 / width;
        float rightPercent = rightPosition * 100 / width;

        if (leftPercent != this.leftPercent || rightPercent != this.rightPercent) {
            this.leftPercent = leftPercent;
            this.rightPercent = rightPercent;

            scaleListener.onScaleChanged(leftPercent, rightPercent);
            invalidate();
        }

        return true;
    }

    private void setPaintToGraphs() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(graphStrokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    private void setPaintToDrawBackgroundRect() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backgroundRectColor);
        paint.setStrokeWidth(0);
    }

    private void setPaintToDrawSelectRect() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(selectRectColor);
        paint.setStrokeWidth(0);
    }

    private boolean isInsideSelectRect(float x) {
        return x > (leftPosition + selectRectSideWidth +addedTouchArea) &&
                x < (rightPosition - selectRectSideWidth - addedTouchArea);
    }

    private boolean isLargerThenRightSide(float x) {
        return x > width;
    }

    private boolean isSmallerThenLeftSide(float x) {
        return x < 0;
    }

    private boolean isSelectRectLeftSide(float x) {
        float leftSide = leftPosition - addedTouchArea;
        float rightSide = leftPosition + selectRectSideWidth + addedTouchArea;
        return x < rightSide && x > leftSide;
    }

    private boolean isSelectRectRightSide(float x) {
        float leftSide = rightPosition - selectRectSideWidth - addedTouchArea;
        float rightEdge = rightPosition;
        float rightSide;
        if (width - rightEdge < addedTouchArea) {
            rightSide = width;
        } else {
            rightSide = rightEdge + addedTouchArea;
        }
        return x < rightSide && x > leftSide;
    }

    private void calculatePositions(TouchRangeType touchRangeType, float x) {
        switch (touchRangeType) {
            case INSIDE_RECT:
                float newLeftPosition =  x - leftDragArea;
                float newRightPosition = x + rightDragArea;
                if (newLeftPosition < 0) {
                    leftPosition = 0;
                    rightPosition = leftPosition + rectSize;
                } else if (newRightPosition > width) {
                    rightPosition = width;
                    leftPosition = rightPosition - rectSize;
                } else {
                    leftPosition = newLeftPosition;
                    rightPosition = newRightPosition;
                }
                break;
            case LEFT_SIDE:
                if (rightPosition - x < minRectSize) {
                    leftPosition = rightPosition - minRectSize;
                } else if (x < 0) {
                    leftPosition = 0;
                } else {
                    leftPosition = x;
                }
                break;
            case RIGHT_SIDE:
                if (x - leftPosition < minRectSize) {
                    rightPosition = leftPosition + minRectSize;
                } else if (x > width) {
                    rightPosition = width;
                } else {
                    rightPosition = x;
                }
                break;
            case OVER_RIGHT:
                rightPosition = width;
                break;
            case OVER_LEFT:
                leftPosition = 0;
                break;
        }
    }
}
