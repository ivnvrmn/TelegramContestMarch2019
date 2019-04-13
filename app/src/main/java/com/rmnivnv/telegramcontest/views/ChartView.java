package com.rmnivnv.telegramcontest.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.rmnivnv.telegramcontest.R;
import com.rmnivnv.telegramcontest.ScaleListener;
import com.rmnivnv.telegramcontest.model.ChartType;
import com.rmnivnv.telegramcontest.model.GraphData;
import com.rmnivnv.telegramcontest.model.GraphDataX;
import com.rmnivnv.telegramcontest.model.GraphDataY;
import com.rmnivnv.telegramcontest.utils.AnimationEndListener;
import com.rmnivnv.telegramcontest.utils.ThemeChecker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartView extends View implements ScaleListener {

    private static final int GUIDELINE_COUNT = 6;
    private static final int GUIDELINE_INTERVAL_COUNT = 5;
    private static final int NOT_SELECTED = -1;
    private static final int ALPHA_DEFAULT = 255;
    private static final int DEFAULT_ANIMATION_DURATION = 200;
    private static final int FAST_ANIMATION_DURATION = 100;
    private static final long MAX_ONE_LINE_POINT = 9999;
    private static final long PREVIOUS_MAX_DEFAULT = -1;
    private static final String X_DATE_FORMAT_PATTERN = "MMM dd";
    private static final String INFO_RECT_DATE_FORMAT_PATTERN = "EEE, MMM dd";
    private static final String PROPERTY_GO_UP = "propertyGoUp";
    private static final String PROPERTY_COME_FROM_DOWN = "propertyComeFromDown";
    private static final String PROPERTY_COME_FROM_UP = "propertyComeFromUp";
    private static final String PROPERTY_ALPHA_UP = "propertyAlphaUp";
    private static final String PROPERTY_ALPHA_DOWN = "propertyAlphaDown";
    private static final String PROPERTY_HIGH_TO_LOW_HEIGHT = "propertyHighToLowHeight";
    private static final String PROPERTY_LOW_TO_HIGH_HEIGHT = "propertyLowToHighHeight";
    private static final String PROPERTY_GO_DOWN = "propertyGoDown";

    private List<GraphData> graphs = new ArrayList<>();
    private ThemeChecker themeChecker;
    private GraphDataY biggerGraphToDelete;
    private GraphDataY newBiggerGraph;
    private GraphDataY smallGraphToDelete;
    private GraphDataY newSmallGraph;
    private Paint paint;
    private TextPaint textPaint;
    private Path path;
    private ValueAnimator goUpAnimator;
    private ValueAnimator goDownAnimator;
    private ValueAnimator comeFromDownAnimator;
    private ValueAnimator comeFromUpAnimator;
    private ValueAnimator alphaUpAnimator;
    private ValueAnimator alphaDownAnimator;
    private ValueAnimator deleteBiggerGraphAnimator;
    private ValueAnimator addNewBiggerGraphAnimator;
    private PropertyValuesHolder goUpProperty = PropertyValuesHolder.ofFloat(PROPERTY_GO_UP, 1, 3);
    private PropertyValuesHolder comeFromUpProperty = PropertyValuesHolder.ofFloat(PROPERTY_COME_FROM_UP, 3, 1);
    private PropertyValuesHolder comeFromDownProperty = PropertyValuesHolder.ofFloat(PROPERTY_COME_FROM_DOWN, 0, 1);
    private PropertyValuesHolder alphaUpProperty = PropertyValuesHolder.ofInt(PROPERTY_ALPHA_UP, 0, 255);
    private PropertyValuesHolder alphaDownProperty = PropertyValuesHolder.ofInt(PROPERTY_ALPHA_DOWN, 255, 0);
    private PropertyValuesHolder goDownProperty = PropertyValuesHolder.ofFloat(PROPERTY_GO_DOWN, 1, 0);
    private AnimatorSet showEmptyTextAnimatorSet;
    private AnimatorSet hideEmptyTextAnimatorSet;
    private AnimatorSet linesFromUpToDownAnimatorSet;
    private AnimatorSet linesFromDownToUpAnimatorSet;
    private Date date;
    private SimpleDateFormat xDateFormat;
    private SimpleDateFormat infoRectDateFormat;
    private String emptyText;

    private int width;
    private int height;
    private float leftPercent = 75;
    private float rightPercent = 100;
    private float totalGraphsLength;
    private float betweenDatesDistance;
    private long graphToDeleteMaxValue;
    private long withoutBiggerMaxValue;
    private long previousMaxYSelectedRange = PREVIOUS_MAX_DEFAULT;
    private long goDownMaxSelectedRange;
    private long goUpMaxSelectedRange;
    private boolean isHidingEmptyText = false;
    private boolean isEmptyText = false;
    private boolean isAnimateFromUpToDown = false;
    private boolean isAnimateFromDownToUp = false;

    private float goUpValue;
    private float goDownValue;
    private float comeFromDownValue = 1;
    private float comeFromUpValue = 3;
    private float comeFromDownToMiddleValue = 0.5f;
    private float goDownFromMiddleValue = 1;
    private int alphaUpValue = ALPHA_DEFAULT;
    private int alphaDownValue = 0;
    private int highToLowHeightValue;
    private int lowToHighHeightValue;

    private int dimensionsTextColor;
    private int guidelineColor;
    private int pickedDateLineColor;
    private int mainBackgroundColor;

    private float emptyTextSize;
    private float dimensionsTextSize;
    private float guidelineStrokeWidth;
    private float pickedDateLineStrokeWidth;
    private float graphStrokeWidth;
    private float textYMargin;
    private float dateTopMargin;
    private float pickedDateCircleRadius;
    private float paddingBottomTop;
    private float pickedDateXPosition = NOT_SELECTED;
    private float pickedPoint = NOT_SELECTED;
    private float pickedPointXPosition = NOT_SELECTED;
    float leftRangePosition;
    float rightRangePosition;

    private int infoRectShadowColor;
    private int infoRectDateTextColor;
    private int infoRectBackgroundColor;
    private float infoRectTop = 0;
    private float infoRectBottom = 0;
    private float infoRectStart = 0;
    private float infoRectEnd = 0;
    private float infoRectWidth;
    private float infoRectHeight;
    private float infoRectTopMargin;
    private float infoRectCornerRadius;
    private float infoRectStrokeWidth;
    private float infoRectShadowRadius;
    private float infoRectShadowYPosition;
    private float infoRectDefaultShift;
    private float infoRectDateTextSize;
    private float infoRectDateTextMargin;
    private float infoRectDateTextTopMargin;
    private float infoRectPointTextSize;
    private float infoRectPointTextMargin;
    private float infoRectPointTextTopMargin;
    private float infoRectPointNameTextSize;
    private float infoRectAdditionalHeight;
    private float infoRectHalfAdditionalHeight;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        initObjects();
        initDimensions();
        initAnimators();
        initOtherValues();
    }

    private void initObjects() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
        date = new Date();
        xDateFormat = new SimpleDateFormat(X_DATE_FORMAT_PATTERN, Locale.US);
        infoRectDateFormat = new SimpleDateFormat(INFO_RECT_DATE_FORMAT_PATTERN, Locale.US);
    }

    private void initDimensions() {
        paddingBottomTop = getResources().getDimensionPixelSize(R.dimen.chart_padding_bottom_top);
        guidelineStrokeWidth = getResources().getDimensionPixelSize(R.dimen.chart_guideline_thickness);
        pickedDateLineStrokeWidth = getResources().getDimensionPixelSize(R.dimen.chart_picked_date_line_thickness);
        graphStrokeWidth = getResources().getDimensionPixelSize(R.dimen.chart_graph_thickness);
        textYMargin = getResources().getDimensionPixelSize(R.dimen.chart_y_text_margin);
        dateTopMargin = getResources().getDimensionPixelSize(R.dimen.chart_date_text_top_margin);
        pickedDateCircleRadius = getResources().getDimensionPixelSize(R.dimen.picked_date_circle_radius);
        dimensionsTextSize = getResources().getDimensionPixelSize(R.dimen.chart_dimensions_text_size);
        betweenDatesDistance = getResources().getDimensionPixelSize(R.dimen.chart_between_dates_margin);
        emptyTextSize = getResources().getDimensionPixelSize(R.dimen.empty_text_size);

        infoRectDateTextSize = getResources().getDimensionPixelSize(R.dimen.info_rect_date_text_size);
        infoRectDateTextMargin = getResources().getDimensionPixelSize(R.dimen.info_rect_date_text_margin);
        infoRectDateTextTopMargin = getResources().getDimensionPixelSize(R.dimen.info_rect_date_text_top_margin);
        infoRectWidth = getResources().getDimensionPixelSize(R.dimen.info_rect_width);
        infoRectHeight = getResources().getDimensionPixelSize(R.dimen.info_rect_height);
        infoRectTopMargin = getResources().getDimensionPixelSize(R.dimen.info_rect_top_margin);
        infoRectCornerRadius = getResources().getDimensionPixelSize(R.dimen.info_rect_corner_radius);
        infoRectStrokeWidth = getResources().getDimensionPixelSize(R.dimen.info_rect_stroke_thickness);
        infoRectShadowRadius = getResources().getDimensionPixelSize(R.dimen.info_rect_shadow_radius);
        infoRectShadowYPosition = getResources().getDimensionPixelSize(R.dimen.info_rect_shadow_y_position);
        infoRectDefaultShift = getResources().getDimensionPixelSize(R.dimen.info_rect_default_shift);
        infoRectPointTextSize = getResources().getDimensionPixelSize(R.dimen.info_rect_point_text_size);
        infoRectPointTextMargin = getResources().getDimensionPixelSize(R.dimen.info_rect_point_text_margin);
        infoRectPointTextTopMargin = getResources().getDimensionPixelSize(R.dimen.info_rect_point_text_top_margin);
        infoRectPointNameTextSize = getResources().getDimensionPixelSize(R.dimen.info_rect_point_name_text_size);
        infoRectAdditionalHeight = getResources().getDimensionPixelSize(R.dimen.info_rect_additional_height);
        infoRectHalfAdditionalHeight = getResources().getDimensionPixelSize(R.dimen.info_rect_half_additional_height);
    }

    private void initAnimators() {
        goUpAnimator = ValueAnimator.ofFloat(1, 3);
        goUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                goUpValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        goUpAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimateFromDownToUp) {
                    isAnimateFromDownToUp = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        goDownAnimator = ValueAnimator.ofFloat(1, 0);
        goDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                goDownValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        goDownAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimateFromUpToDown) {
                    isAnimateFromUpToDown = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        comeFromDownAnimator = ValueAnimator.ofFloat(0, 1);
        comeFromDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                comeFromDownValue = (float) animation.getAnimatedValue();
            }
        });

        ValueAnimator comeFromDownToMiddleAnimator = ValueAnimator.ofFloat(1, 0.5f);
        comeFromDownToMiddleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                comeFromDownToMiddleValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator goDownFromMiddleAnimator = ValueAnimator.ofFloat(0.5f, 1);
        goDownFromMiddleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                goDownFromMiddleValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        goDownFromMiddleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                isHidingEmptyText = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        comeFromUpAnimator = ValueAnimator.ofFloat(3, 1);
        comeFromUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                comeFromUpValue = (float) animation.getAnimatedValue();
            }
        });

        alphaUpAnimator = ValueAnimator.ofInt(0, 255);
        alphaUpAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        alphaUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alphaUpValue = (int) animation.getAnimatedValue();
                if (newSmallGraph != null) {
                    invalidate();
                }
            }
        });
        alphaUpAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (newSmallGraph != null) {
                    newSmallGraph = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        alphaDownAnimator = ValueAnimator.ofInt(255, 0);
        alphaDownAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        alphaDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alphaDownValue = (int) animation.getAnimatedValue();
                if (smallGraphToDelete != null) {
                    invalidate();
                }
            }
        });
        alphaDownAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (smallGraphToDelete != null) {
                    smallGraphToDelete = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        deleteBiggerGraphAnimator = new DefaultValueAnimator();
        deleteBiggerGraphAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                goUpValue = (float) animation.getAnimatedValue(PROPERTY_GO_UP);
                comeFromDownValue = (float) animation.getAnimatedValue(PROPERTY_COME_FROM_DOWN);
                alphaUpValue = (int) animation.getAnimatedValue(PROPERTY_ALPHA_UP);
                alphaDownValue = (int) animation.getAnimatedValue(PROPERTY_ALPHA_DOWN);
                highToLowHeightValue = (int) animation.getAnimatedValue(PROPERTY_HIGH_TO_LOW_HEIGHT);
                invalidate();
            }
        });
        deleteBiggerGraphAnimator.addListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                biggerGraphToDelete = null;
            }
        });

        addNewBiggerGraphAnimator = new DefaultValueAnimator();
        addNewBiggerGraphAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                goDownValue = (float) animation.getAnimatedValue(PROPERTY_GO_DOWN);
                comeFromUpValue = (float) animation.getAnimatedValue(PROPERTY_COME_FROM_UP);
                alphaUpValue = (int) animation.getAnimatedValue(PROPERTY_ALPHA_UP);
                alphaDownValue = (int) animation.getAnimatedValue(PROPERTY_ALPHA_DOWN);
                lowToHighHeightValue = (int) animation.getAnimatedValue(PROPERTY_LOW_TO_HIGH_HEIGHT);
                invalidate();
            }
        });
        addNewBiggerGraphAnimator.addListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                newBiggerGraph = null;
            }
        });

        showEmptyTextAnimatorSet = new AnimatorSet();
        showEmptyTextAnimatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
        showEmptyTextAnimatorSet.setInterpolator(new AccelerateInterpolator());
        showEmptyTextAnimatorSet.playTogether(comeFromDownToMiddleAnimator, alphaUpAnimator);

        hideEmptyTextAnimatorSet = new AnimatorSet();
        hideEmptyTextAnimatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
        hideEmptyTextAnimatorSet.setInterpolator(new AccelerateInterpolator());
        hideEmptyTextAnimatorSet.playTogether(goDownFromMiddleAnimator, alphaDownAnimator);

        linesFromUpToDownAnimatorSet = new AnimatorSet();
        linesFromUpToDownAnimatorSet.setDuration(FAST_ANIMATION_DURATION);
        linesFromUpToDownAnimatorSet.setInterpolator(new AccelerateInterpolator());
        linesFromUpToDownAnimatorSet.playTogether(goDownAnimator, comeFromUpAnimator, alphaUpAnimator,
                alphaDownAnimator);

        linesFromDownToUpAnimatorSet = new AnimatorSet();
        linesFromDownToUpAnimatorSet.setDuration(FAST_ANIMATION_DURATION);
        linesFromDownToUpAnimatorSet.setInterpolator(new AccelerateInterpolator());
        linesFromDownToUpAnimatorSet.playTogether(goUpAnimator, comeFromDownAnimator, alphaUpAnimator,
                alphaDownAnimator);
    }

    private void initOtherValues() {
        emptyText = getResources().getString(R.string.empty_text);
        dimensionsTextColor = getResources().getColor(R.color.dimensions_text_color);
    }

    public void setThemeChecker(ThemeChecker themeChecker) {
        this.themeChecker = themeChecker;
    }

    public void setColorsByTheme() {
        if (themeChecker.isDarkTheme()) {
            guidelineColor = getResources().getColor(R.color.guideline_color_dark);
            pickedDateLineColor = getResources().getColor(R.color.picked_date_line_color_dark);
            mainBackgroundColor = getResources().getColor(R.color.main_background_color_dark);
            infoRectBackgroundColor = getResources().getColor(R.color.info_rect_background_color_dark);
            infoRectDateTextColor = getResources().getColor(R.color.info_rect_date_text_color_dark);
            infoRectShadowColor = getResources().getColor(R.color.info_rect_shadow_color_dark);
        } else {
            guidelineColor = getResources().getColor(R.color.guideline_color);
            pickedDateLineColor = getResources().getColor(R.color.picked_date_line_color);
            mainBackgroundColor = getResources().getColor(R.color.main_background_color);
            infoRectBackgroundColor = getResources().getColor(R.color.info_rect_background_color);
            infoRectDateTextColor = getResources().getColor(R.color.info_rect_date_text_color);
            infoRectShadowColor = getResources().getColor(R.color.info_rect_shadow_color);
        }
    }

    public void setGraphs(List<GraphData> graphs) {
        this.graphs = graphs;
        totalGraphsLength = ((GraphDataY) graphs.get(0)).getPoints().size();
        previousMaxYSelectedRange = PREVIOUS_MAX_DEFAULT;

        invalidate();
    }

    public void setGraphToDelete(GraphDataY graph) {
        long maxYSelectedRange = findMaxValueFromSelectedRange();
        long deleteMaxYSelectedRange = findMaxValueFromGraph(graph);
        if (maxYSelectedRange < deleteMaxYSelectedRange) {
            smallGraphToDelete = null;
            biggerGraphToDelete = graph;
            graphToDeleteMaxValue = deleteMaxYSelectedRange;

            PropertyValuesHolder highToLowHeightProperty = PropertyValuesHolder.ofInt(
                    PROPERTY_HIGH_TO_LOW_HEIGHT, (int) graphToDeleteMaxValue, (int) maxYSelectedRange);
            deleteBiggerGraphAnimator.setValues(highToLowHeightProperty, goUpProperty,
                    comeFromDownProperty, alphaUpProperty, alphaDownProperty);
        } else {
            biggerGraphToDelete = null;
            smallGraphToDelete = graph;
        }
    }

    public void addGraph(GraphDataY graph) {
        long maxYSelectedRange = findMaxValueFromSelectedRange();
        long newGraphMaxY = findMaxValueFromGraph(graph);
        if (newGraphMaxY > maxYSelectedRange) {
            newSmallGraph = null;
            newBiggerGraph = graph;
            withoutBiggerMaxValue = findMaxValueWithoutGraph(newBiggerGraph);

            PropertyValuesHolder lowToHighHeightProperty = PropertyValuesHolder.ofInt(
                    PROPERTY_LOW_TO_HIGH_HEIGHT, (int) withoutBiggerMaxValue, (int) newGraphMaxY);
            addNewBiggerGraphAnimator.setValues(goDownProperty, comeFromUpProperty, alphaUpProperty,
                    alphaDownProperty, lowToHighHeightProperty);
        } else {
            newBiggerGraph = null;
            newSmallGraph = graph;
        }
    }

    public void update() {
        if (biggerGraphToDelete != null) {
            deleteBiggerGraphAnimator.start();
        } else if (newBiggerGraph != null) {
            addNewBiggerGraphAnimator.start();
        } else if (smallGraphToDelete != null) {
            alphaDownAnimator.start();
        } else if (newSmallGraph != null) {
            alphaUpAnimator.start();
        }
        invalidate();
    }

    public void showEmptyText() {
        isEmptyText = true;
        pickedDateXPosition = NOT_SELECTED;
        showEmptyTextAnimatorSet.start();
    }

    public void hideEmptyText() {
        isEmptyText = false;
        isHidingEmptyText = true;
        hideEmptyTextAnimatorSet.start();
    }

    @Override
    public void onScaleChanged(float leftPercent, float rightPercent) {
        if (leftPercent != this.leftPercent || rightPercent != this.rightPercent) {
            this.leftPercent = leftPercent;
            this.rightPercent = rightPercent;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = (int) (width * 0.9);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (graphs.size() > 0) {
            final float gridBottom = height - paddingBottomTop;
            final float gridTop = paddingBottomTop;
            final float availableHeight = gridBottom - gridTop;
            final long maxYSelectedRange = findMaxValueFromSelectedRange();
            float oneYStep = availableHeight / maxYSelectedRange;

            startGuidelinesAnimationsOnYValueChange(maxYSelectedRange);
            drawGuidelinesAndYTextDisappearing(canvas, availableHeight, gridBottom);
            drawGuidelinesAndYTextAppearance(canvas, gridBottom, oneYStep, maxYSelectedRange);

            float oneWidthPercent = width / (100 - leftPercent - (100 - rightPercent));
            float addedLeftSpace = oneWidthPercent * leftPercent;
            float addedRightSpace = oneWidthPercent * (100 - rightPercent);
            float realTotalPoints = totalGraphsLength - 1;
            float sectionDistance = (width + addedLeftSpace + addedRightSpace) / realTotalPoints;

            List<String> datesList = getDatesList();
            drawDates(canvas, datesList, gridBottom, addedLeftSpace, sectionDistance, realTotalPoints);
            drawSelectedDateVerticalLine(canvas, addedLeftSpace, sectionDistance, gridBottom);

            float leftRangePosition = realTotalPoints / 100 * leftPercent;
            float rightRangePosition = totalGraphsLength / 100 * rightPercent;
            if ((int) rightRangePosition == totalGraphsLength) {
                rightRangePosition--;
            }

            drawGraphs(canvas, addedLeftSpace, leftRangePosition, rightRangePosition, maxYSelectedRange,
                    oneYStep, availableHeight, gridBottom, sectionDistance);
            deleteGraphs(canvas, leftRangePosition, addedLeftSpace, rightRangePosition, maxYSelectedRange,
                    availableHeight, oneYStep, gridBottom, sectionDistance);
//            drawSelectedDateInfoRect(canvas, datesList);
            drawEmptyView(canvas);
        }
    }

    private long findMaxValueFromSelectedRange() {
        long maxValue = 0;

        float leftRangePosition = totalGraphsLength / 100 * leftPercent;
        float rightRangePosition = totalGraphsLength / 100 * rightPercent;

        for (GraphData graph : graphs) {
            if (graph.getChartType().equals(ChartType.LINE)) {
                for (int i = (int) leftRangePosition; i < rightRangePosition; i++) {
                    long temp = ((GraphDataY) graph).getPoints().get(i);
                    if (temp > maxValue) {
                        maxValue = temp;
                    }
                }
            }
        }
        return maxValue;
    }

    private void startGuidelinesAnimationsOnYValueChange(long maxYSelectedRange) {
        if (newBiggerGraph == null && previousMaxYSelectedRange != PREVIOUS_MAX_DEFAULT &&
                maxYSelectedRange > previousMaxYSelectedRange) {
            linesFromUpToDownAnimatorSet.cancel();
            goDownMaxSelectedRange = previousMaxYSelectedRange;
            isAnimateFromUpToDown = true;
            linesFromUpToDownAnimatorSet.start();
        } else if (biggerGraphToDelete == null && previousMaxYSelectedRange != PREVIOUS_MAX_DEFAULT &&
                maxYSelectedRange < previousMaxYSelectedRange) {
            linesFromDownToUpAnimatorSet.cancel();
            goUpMaxSelectedRange = previousMaxYSelectedRange;
            isAnimateFromDownToUp = true;
            linesFromDownToUpAnimatorSet.start();
        }
        previousMaxYSelectedRange = maxYSelectedRange;
    }

    private void drawGuidelinesAndYTextDisappearing(Canvas canvas, float availableHeight,
                                                    float gridBottom) {
        //draw guidelines and text Y
        if (biggerGraphToDelete != null) {
            //animate guidelines up
            animateGuidelines(canvas, availableHeight, gridBottom, graphToDeleteMaxValue, goUpValue);
        } else if (newBiggerGraph != null) {
            //animate guidelines down
            animateGuidelines(canvas, availableHeight, gridBottom, withoutBiggerMaxValue, goDownValue);
        } else if (isAnimateFromUpToDown) {
            animateGuidelines(canvas, availableHeight, gridBottom, goDownMaxSelectedRange, goDownValue);
        } else if (isAnimateFromDownToUp) {
            animateGuidelines(canvas, availableHeight, gridBottom, goUpMaxSelectedRange, goUpValue);
        }
    }

    private void animateGuidelines(Canvas canvas, float availableHeight, float gridBottom,
                                   long maxYSelectedRange, float animateValue) {
        setPaintToGuideline(alphaDownValue);
        setTextPaintToXYDimensions(alphaDownValue);

        float oneYStep = availableHeight / maxYSelectedRange;
        double guidelineMaxValue = maxYSelectedRange * 0.95;
        float guidelineSpacing = (oneYStep * animateValue * (float) guidelineMaxValue) / GUIDELINE_INTERVAL_COUNT;
        float y = gridBottom;

        int textY;
        long stepY = (long) guidelineMaxValue / GUIDELINE_INTERVAL_COUNT;

        for (int i = 0; i < GUIDELINE_COUNT; i++) {
            canvas.drawLine(0, y, width, y, paint);

            textY = (int) (i * stepY);
            float textYPosition = y - textYMargin;
            canvas.drawText(String.valueOf(textY), 0, textYPosition, textPaint);

            y -= guidelineSpacing;
        }
    }

    private void drawGuidelinesAndYTextAppearance(Canvas canvas, float gridBottom, float oneYStep,
                                                  long maxYSelectedRange) {
        float oneYStepWithAnimationValue;
        if (biggerGraphToDelete != null || isAnimateFromDownToUp) {
            setPaintToGuideline(alphaUpValue);
            setTextPaintToXYDimensions(alphaUpValue);
            oneYStepWithAnimationValue = oneYStep * comeFromDownValue;
        } else if (newBiggerGraph != null || isAnimateFromUpToDown) {
            setPaintToGuideline(alphaUpValue);
            setTextPaintToXYDimensions(alphaUpValue);
            oneYStepWithAnimationValue = oneYStep * comeFromUpValue;
        } else {
            setPaintToGuideline(ALPHA_DEFAULT);
            setTextPaintToXYDimensions(ALPHA_DEFAULT);
            oneYStepWithAnimationValue = oneYStep;
        }

        double guidelineMaxValue = maxYSelectedRange * 0.95;
        float guidelineSpacing = (oneYStepWithAnimationValue * (float) guidelineMaxValue) /
                GUIDELINE_INTERVAL_COUNT;
        float y = gridBottom;

        long textY;
        float textYStartPosition = gridBottom;
        long stepY = (long) guidelineMaxValue / GUIDELINE_INTERVAL_COUNT;

        for (int i = 0; i < GUIDELINE_COUNT; i++) {
            canvas.drawLine(0, y, width, y, paint);
            y -= guidelineSpacing;

            textY =  i * stepY;
            float textYPosition = textYStartPosition - textYMargin;
            canvas.drawText(String.valueOf(textY), 0, textYPosition, textPaint);
            textYStartPosition -= guidelineSpacing;
        }
    }

    private void drawDates(Canvas canvas, List<String> datesList,  float gridBottom, float addedLeftSpace,
                           float sectionDistance, float realTotalPoints) {
        if (graphs.size() > 1) {
            setTextPaintToXYDimensions(ALPHA_DEFAULT);

            //draw first date
//            date.setTime(datesList.get(0));
            canvas.drawText(datesList.get(0), -addedLeftSpace, gridBottom + dateTopMargin, textPaint);

            //draw last date
//            date.setTime(datesList.get(datesList.size() - 1));
            textPaint.setTextAlign(Paint.Align.RIGHT);
            float xPos = sectionDistance * realTotalPoints - addedLeftSpace;
            canvas.drawText(datesList.get(datesList.size() - 1), xPos, gridBottom + dateTopMargin, textPaint);

//            float dateWidth = textPaint.measureText(xDateFormat.format(date));
            float dateWidth = textPaint.measureText(datesList.get(datesList.size() - 1));
            float availableWidth = sectionDistance * realTotalPoints - dateWidth;
            int datesCount = (int) (availableWidth / (dateWidth + betweenDatesDistance));

            float datesInterval = availableWidth / datesCount;
            float dateXPosition = -addedLeftSpace + (dateWidth / 2) + datesInterval;
            textPaint.setTextAlign(Paint.Align.CENTER);
            for (int i = 1; i < datesCount; i++) {
                int datePosition = (int) (i * realTotalPoints / datesCount);
//                date.setTime(datesList.get(datePosition));
                canvas.drawText(datesList.get(datePosition), dateXPosition, gridBottom + dateTopMargin, textPaint);
                dateXPosition += datesInterval;
            }
        }
    }

    private void drawSelectedDateVerticalLine(Canvas canvas, float addedLeftSpace, float sectionDistance,
                                              float gridBottom) {
        if (pickedDateXPosition != NOT_SELECTED) {
            pickedPoint = (addedLeftSpace + pickedDateXPosition) / sectionDistance;
            pickedPointXPosition = ((int) pickedPoint * sectionDistance) - addedLeftSpace;
            setPaintToPickedDateline();
            canvas.drawLine(pickedPointXPosition, paddingBottomTop, pickedPointXPosition, gridBottom, paint);
        }
    }

    private void drawGraphs(Canvas canvas, float addedLeftSpace, float leftRangePosition, float rightRangePosition,
                            long maxYSelectedRange, float oneYStep, float availableHeight, float gridBottom,
                            float sectionDistance) {
        setPaintToGraphs();
        int leftEdge;
        if (leftRangePosition == 0 || leftRangePosition == 1) {
            leftEdge = 0;
        } else {
            leftEdge = (int) leftRangePosition - 1;
        }
        int rightEdge;
        if (rightRangePosition == totalGraphsLength - 1) {
            rightEdge = (int) rightRangePosition;
        } else {
            rightEdge = (int) rightRangePosition + 1;
        }

        for (GraphData graph : graphs) {
            if (graph.getChartType().equals(ChartType.LINE)) {
                float startX = -addedLeftSpace;

                float leftRealYValue = findLeftRealYValue(leftRangePosition, ((GraphDataY) graph).getPoints());
                float rightRealYValue = findRightRealYValue(rightRangePosition, ((GraphDataY) graph).getPoints());

                if (leftRealYValue > maxYSelectedRange && leftRealYValue > rightRealYValue) {
                    oneYStep = availableHeight / leftRealYValue;
                } else if (rightRealYValue > maxYSelectedRange && rightRealYValue > leftRealYValue) {
                    oneYStep = availableHeight / rightRealYValue;
                }

                if (biggerGraphToDelete != null) {
                    oneYStep = availableHeight / highToLowHeightValue;
                } else if (newBiggerGraph != null && newBiggerGraph.equals(graph)) {
                    oneYStep *= comeFromUpValue;
                } else if (newBiggerGraph != null) {
                    oneYStep = availableHeight / lowToHighHeightValue;
                }

                float startY = gridBottom - oneYStep * ((GraphDataY) graph).getPoints().get(0);

                path.reset();
                path.moveTo(startX, startY);
                for (int i = 1; i < totalGraphsLength; i++) {
                    float top = gridBottom - oneYStep * ((GraphDataY) graph).getPoints().get(i);
                    if (i >= leftEdge && i <= rightEdge) {
                        path.lineTo(startX + sectionDistance, top);
                    }
                    startX += sectionDistance;
                }

                int graphColor = Color.parseColor(graph.getColor());
                paint.setColor(graphColor);
                checkIfThisANewGraphAndIfAddAlphaUp((GraphDataY) graph);
                canvas.drawPath(path, paint);

                //draw selected date circles
                if (pickedDateXPosition != NOT_SELECTED) {
                    float yPosition = gridBottom - (oneYStep * ((GraphDataY) graph).getPoints().get((int) pickedPoint));

                    setPaintToPickedDateFillCircle();
                    checkIfThisANewGraphAndIfAddAlphaUp((GraphDataY) graph);
                    canvas.drawCircle(pickedPointXPosition, yPosition, pickedDateCircleRadius, paint);

                    setPaintToGraphs();
                    paint.setColor(graphColor);
                    checkIfThisANewGraphAndIfAddAlphaUp((GraphDataY) graph);
                    canvas.drawCircle(pickedPointXPosition, yPosition, pickedDateCircleRadius, paint);
                }
            }
        }



//        for (GraphData graph : graphs) {
//            if (graph.getChartType().equals(ChartType.LINE)) {
//                float startX = -addedLeftSpace;
//
//                float leftRealYValue = findLeftRealYValue(leftRangePosition, ((GraphDataY) graph).getPoints());
//                float rightRealYValue = findRightRealYValue(rightRangePosition, ((GraphDataY) graph).getPoints());
//
//                if (leftRealYValue > maxYSelectedRange && leftRealYValue > rightRealYValue) {
//                    oneYStep = availableHeight / leftRealYValue;
//                } else if (rightRealYValue > maxYSelectedRange && rightRealYValue > leftRealYValue) {
//                    oneYStep = availableHeight / rightRealYValue;
//                }
//
//                if (biggerGraphToDelete != null) {
//                    oneYStep = availableHeight / highToLowHeightValue;
//                } else if (newBiggerGraph != null && newBiggerGraph.equals(graph)) {
//                    oneYStep *= comeFromUpValue;
//                } else if (newBiggerGraph != null) {
//                    oneYStep = availableHeight / lowToHighHeightValue;
//                }
//
//                float startY = gridBottom - oneYStep * ((GraphDataY) graph).getPoints().get(0);
//
//                path.reset();
//                path.moveTo(startX, startY);
//                for (int i = 1; i < totalGraphsLength; i++) {
//                    float top = gridBottom - oneYStep * ((GraphDataY) graph).getPoints().get(i);
//                    path.lineTo(startX + sectionDistance, top);
//                    startX += sectionDistance;
//                }
//
//                int graphColor = Color.parseColor(graph.getColor());
//                paint.setColor(graphColor);
//                checkIfThisANewGraphAndIfAddAlphaUp((GraphDataY) graph);
//                canvas.drawPath(path, paint);
//
//                //draw selected date circles
//                if (pickedDateXPosition != NOT_SELECTED) {
//                    float yPosition = gridBottom - (oneYStep * ((GraphDataY) graph).getPoints().get((int) pickedPoint));
//
//                    setPaintToPickedDateFillCircle();
//                    checkIfThisANewGraphAndIfAddAlphaUp((GraphDataY) graph);
//                    canvas.drawCircle(pickedPointXPosition, yPosition, pickedDateCircleRadius, paint);
//
//                    setPaintToGraphs();
//                    paint.setColor(graphColor);
//                    checkIfThisANewGraphAndIfAddAlphaUp((GraphDataY) graph);
//                    canvas.drawCircle(pickedPointXPosition, yPosition, pickedDateCircleRadius, paint);
//                }
//            }
//        }
    }

    private void checkIfThisANewGraphAndIfAddAlphaUp(GraphDataY graph) {
        if ((newBiggerGraph != null && newBiggerGraph.equals(graph)) ||
                (newSmallGraph != null && newSmallGraph.equals(graph))) {
            paint.setAlpha(alphaUpValue);
        }
    }

    private void deleteGraphs(Canvas canvas, float leftRangePosition, float addedLeftSpace,
                              float rightRangePosition, long maxYSelectedRange, float availableHeight,
                              float oneYStep, float gridBottom, float sectionDistance) {
        if (biggerGraphToDelete != null) {
            deleteGraph(canvas, biggerGraphToDelete, -addedLeftSpace, leftRangePosition, rightRangePosition,
                    maxYSelectedRange, availableHeight, oneYStep, gridBottom, sectionDistance);
        } else if (smallGraphToDelete != null) {
            deleteGraph(canvas, smallGraphToDelete, -addedLeftSpace, leftRangePosition, rightRangePosition,
                    maxYSelectedRange, availableHeight, oneYStep, gridBottom, sectionDistance);
        }
    }

    private void deleteGraph(Canvas canvas, GraphDataY graphData, float startX, float leftRangePosition,
                             float rightRangePosition, long maxYSelectedRange, float availableHeight,
                             float oneYStep, float gridBottom, float sectionDistance) {
        float leftRealYValue = findLeftRealYValue(leftRangePosition, graphData.getPoints());
        float rightRealYValue = findRightRealYValue(rightRangePosition, graphData.getPoints());

        if (leftRealYValue > maxYSelectedRange && leftRealYValue > rightRealYValue) {
            oneYStep = availableHeight / leftRealYValue;
        } else if (rightRealYValue > maxYSelectedRange && rightRealYValue > leftRealYValue) {
            oneYStep = availableHeight / rightRealYValue;
        }

        if (biggerGraphToDelete != null) {
            oneYStep *= goUpValue;
        }

        float startY = gridBottom - oneYStep * graphData.getPoints().get(0);

        path.reset();
        path.moveTo(startX, startY);
        for (int i = 1; i < totalGraphsLength; i++) {
            float top = gridBottom - (oneYStep * graphData.getPoints().get(i));
            path.lineTo(startX + sectionDistance, top);
            startX = startX + sectionDistance;
        }

        int graphColor = Color.parseColor(graphData.getColor());
        paint.setColor(graphColor);
        paint.setAlpha(alphaDownValue);
        canvas.drawPath(path, paint);

        //draw selected date circles
        if (pickedDateXPosition != NOT_SELECTED) {
            float yPosition = gridBottom - (oneYStep * graphData.getPoints().get((int) pickedPoint));

            setPaintToPickedDateFillCircle();
            paint.setAlpha(alphaDownValue);
            canvas.drawCircle(pickedPointXPosition, yPosition, pickedDateCircleRadius, paint);

            setPaintToGraphs();
            paint.setColor(graphColor);
            paint.setAlpha(alphaDownValue);
            canvas.drawCircle(pickedPointXPosition, yPosition, pickedDateCircleRadius, paint);
        }
    }

    private long findMaxValueWithoutGraph(GraphData graphData) {
        long maxValue = 0;

        float leftRangePosition = totalGraphsLength / 100 * leftPercent;
        float rightRangePosition = totalGraphsLength / 100 * rightPercent;

        for (GraphData graph : graphs) {
            if (graph.getChartType().equals(ChartType.LINE) && !graph.equals(graphData)) {
                for (int i = (int) leftRangePosition; i < rightRangePosition; i++) {
                    long temp = ((GraphDataY) graph).getPoints().get(i);
                    if (temp > maxValue) {
                        maxValue = temp;
                    }
                }
            }
        }
        return maxValue;
    }

    private long findMaxValueFromGraph(GraphData graph) {
        long maxValue = 0;

        leftRangePosition = totalGraphsLength / 100 * leftPercent;
        rightRangePosition = totalGraphsLength / 100 * rightPercent;

        for (int i = (int) leftRangePosition; i < rightRangePosition; i++) {
            long temp = ((GraphDataY) graph).getPoints().get(i);
            if (temp > maxValue) {
                maxValue = temp;
            }
        }

        return maxValue;
    }

    private float findLeftRealYValue(float leftRangePosition, List<Long> points) {
        float leftRealYValue = 0;

        float leftRemainder = leftRangePosition % 1;
        long leftLeftValue = points.get((int) leftRangePosition);
        long leftRightValue;
        if (leftRemainder > 0) {
            leftRightValue = points.get((int) ++leftRangePosition);
        } else {
            leftRightValue = points.get((int) leftRangePosition);
        }

        long leftDifference = leftLeftValue - leftRightValue;
        if (leftDifference > 0) {
            leftRealYValue = leftLeftValue - (leftDifference * leftRemainder);
        }

        return leftRealYValue;
    }

    private float findRightRealYValue(float rightRangePosition, List<Long> points) {
        float rightRealYValue = 0;

        float rightRemainder = rightRangePosition % 1;
        long rightLeftValue = points.get((int) rightRangePosition);
        long rightRightValue = points.get((int) rightRangePosition);
        if (rightRemainder > 0) {
            if ((int) rightRangePosition == totalGraphsLength - 1) {
                rightRightValue = points.get((int) rightRangePosition);
            } else {
                rightRightValue = points.get((int) ++rightRangePosition);
            }
        }

        long rightDifference = rightRightValue - rightLeftValue;
        if (rightDifference > 0) {
            rightRealYValue = rightRightValue - (rightDifference * (1 - rightRemainder));
        }

        return rightRealYValue;
    }

    private List<String> getDatesList() {
        for (GraphData graph : graphs) {
            if (graph instanceof GraphDataX) {
                return ((GraphDataX) graph).getDates();
            }
        }

        return null;
    }

    private void drawSelectedDateInfoRect(Canvas canvas, List<Long> datesList) {
        if (pickedDateXPosition != NOT_SELECTED) {
            infoRectTop = infoRectTopMargin;
            infoRectStart = pickedPointXPosition - infoRectDefaultShift;
            infoRectBottom = infoRectTop + infoRectHeight;
            infoRectEnd = infoRectStart + infoRectWidth;

            int addedBottomCount = 0;
            for (GraphData graphData : graphs) {
                if (graphData.getChartType().equals(ChartType.LINE)) {
                    long point = ((GraphDataY) graphData).getPoints().get((int) pickedPoint);
                    if (point > MAX_ONE_LINE_POINT) {
                        addedBottomCount++;
                    }
                }
            }
            if (addedBottomCount == 1 && graphs.size() > 2) {
                infoRectBottom += infoRectAdditionalHeight;
            } else if (addedBottomCount > 1) {
                for (int i = 1; i < addedBottomCount; i++) {
                    infoRectBottom += infoRectAdditionalHeight;
                }
            }

            if (pickedPointXPosition < infoRectDefaultShift) {
                infoRectStart = 0;
            } else if (width - pickedPointXPosition < infoRectWidth) {
                infoRectStart = width - infoRectWidth;
            }

            float right = infoRectStart + infoRectWidth;

            setPaintToDrawInfoRectBackground();
            canvas.drawRoundRect(infoRectStart, infoRectTop, right, infoRectBottom, infoRectCornerRadius, infoRectCornerRadius, paint);
            setPaintToDrawInfoRectStroke();
            canvas.drawRoundRect(infoRectStart, infoRectTop, right, infoRectBottom, infoRectCornerRadius, infoRectCornerRadius, paint);

            //draw date text
            setTextPaintToDrawInfoRectDate();
            date.setTime(datesList.get((int) pickedPoint));
            String dateText = infoRectDateFormat.format(date);
            canvas.drawText(dateText, infoRectStart + infoRectDateTextMargin, infoRectTop + infoRectDateTextTopMargin, textPaint);


            setTextPaintToDrawInfoRectPoint();
            float pointTop = infoRectTop + infoRectPointTextTopMargin;
            float pointLeft = infoRectStart + infoRectPointTextMargin;
            for (int i = 0; i < graphs.size(); i++) {
                GraphData graphData = graphs.get(i);
                if (graphData.getChartType().equals(ChartType.LINE)) {
                    long point = ((GraphDataY) graphData).getPoints().get((int) pickedPoint);

                    if (i > 1 && point > MAX_ONE_LINE_POINT) {
                        pointTop += infoRectAdditionalHeight;
                        pointLeft = infoRectStart + infoRectPointTextMargin;
                    } else if (i == 1 && point > MAX_ONE_LINE_POINT &&
                            ((GraphDataY) graphs.get(i - 1)).getPoints().get((int) pickedPoint) <= MAX_ONE_LINE_POINT) {
                        pointTop += infoRectAdditionalHeight;
                        pointLeft = infoRectStart + infoRectPointTextMargin;
                    }

                    textPaint.setColor(Color.parseColor(graphData.getColor()));
                    canvas.drawText(String.valueOf(point), pointLeft, pointTop, textPaint);

                    setTextPaintToDrawInfoRectPointName();
                    canvas.drawText(graphData.getName(), pointLeft, pointTop + infoRectHalfAdditionalHeight, textPaint);

                    setTextPaintToDrawInfoRectPoint();
                    pointLeft += textPaint.measureText(String.valueOf(point)) + infoRectPointTextMargin;

                    if (i == 0 && point > MAX_ONE_LINE_POINT) {
                        pointTop += infoRectAdditionalHeight;
                        pointLeft = infoRectStart + infoRectPointTextMargin;
                    }
                }
            }
        }
    }

    private void drawEmptyView(Canvas canvas) {
        if (graphs.size() == 1) {
            //show empty view
            setTextPaintToDrawEmptyText();
            textPaint.setAlpha(alphaUpValue);
            float emptyTextY = height * comeFromDownToMiddleValue;
            canvas.drawText(emptyText, width / 2f, emptyTextY, textPaint);
        }

        if (isHidingEmptyText) {
            //hide empty view
            setTextPaintToDrawEmptyText();
            textPaint.setAlpha(alphaDownValue);
            float emptyTextY = height * goDownFromMiddleValue;
            canvas.drawText(emptyText, width / 2f, emptyTextY, textPaint);
        }
    }

    private void setTextPaintToXYDimensions(int alpha) {
        textPaint.setColor(dimensionsTextColor);
        textPaint.setAlpha(alpha);
        textPaint.setTextSize(dimensionsTextSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(false);
    }

    private void setTextPaintToDrawInfoRectDate() {
        textPaint.setColor(infoRectDateTextColor);
        textPaint.setTextSize(infoRectDateTextSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
    }

    private void setTextPaintToDrawInfoRectPoint() {
        textPaint.setTextSize(infoRectPointTextSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
    }

    private void setTextPaintToDrawInfoRectPointName() {
        textPaint.setTextSize(infoRectPointNameTextSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(false);
    }

    private void setTextPaintToDrawEmptyText() {
        textPaint.setTextSize(emptyTextSize);
        textPaint.setColor(dimensionsTextColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }

    private void setPaintToGuideline(int alpha) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(guidelineColor);
        paint.setAlpha(alpha);
        paint.setStrokeWidth(guidelineStrokeWidth);
        paint.setShadowLayer(0, 0, 0, 0);
    }

    private void setPaintToPickedDateline() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(pickedDateLineColor);
        paint.setStrokeWidth(pickedDateLineStrokeWidth);
        paint.setShadowLayer(0, 0, 0, 0);
    }

    private void setPaintToGraphs() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(graphStrokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAlpha(ALPHA_DEFAULT);
        paint.setShadowLayer(0, 0, 0, 0);
    }

    private void setPaintToPickedDateFillCircle() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mainBackgroundColor);
        paint.setStrokeWidth(0);
        paint.setShadowLayer(0, 0, 0, 0);
    }

    private void setPaintToDrawInfoRectBackground() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(infoRectBackgroundColor);
        paint.setStrokeWidth(0);
        paint.setShadowLayer(infoRectShadowRadius, 0, infoRectShadowYPosition, infoRectShadowColor);
    }

    private void setPaintToDrawInfoRectStroke() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(pickedDateLineColor);
        paint.setStrokeWidth(infoRectStrokeWidth);
        paint.setShadowLayer(0, 0, 0, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEmptyText) return true;

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x != pickedDateXPosition && x >= 0 && x <= width) {
                    if (isInfoRectDrawn() && isInsideInfoRect(x, y)) {
                        removeInfoRect();
                    } else {
                        pickedDateXPosition = x;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isInfoRectDrawn() && x != pickedDateXPosition && x >= 0 && x <= width) {
                    pickedDateXPosition = x;
                    invalidate();
                }
                break;
        }
        return true;
    }

    private boolean isInfoRectDrawn() {
        return infoRectTop != 0 && infoRectBottom != 0 && infoRectStart >= 0 && infoRectEnd != 0;
    }

    private boolean isInsideInfoRect(float x, float y) {
        return x < infoRectEnd && x > infoRectStart && y > infoRectTop && y < infoRectBottom;
    }

    private void removeInfoRect() {
        infoRectTop = 0;
        infoRectBottom = 0;
        infoRectStart = 0;
        infoRectEnd = 0;
        pickedDateXPosition = NOT_SELECTED;
    }
}