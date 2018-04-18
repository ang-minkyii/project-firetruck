package saco.ProjectFireTruckV2.etc_utilities;

/**
 * Created by FTJX73 on 1/19/2016.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import saco.ProjectFireTruckV2.R;

public class CoachmarkArrow extends View {

    private static final float CIRCLE_RADIUS = 1.5f;
    private static final int TOPLEFT = 0;
    private static final int TOPRIGHT = 1;
    private static final int BOTTOMLEFT = 2;
    private static final int BOTTOMRIGHT = 3;

    Paint paint;
    int from, to;

    PointF padding = new PointF();
    PointF fromPoint = new PointF();
    PointF toPoint = new PointF();

    public CoachmarkArrow(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CoachmarkArrow, 0, 0);

        getPaint(a);
        getFromTo(a);

        a.recycle();
    }

    private void getFromTo(TypedArray a) {

        from = a.getInt(R.styleable.CoachmarkArrow_from, BOTTOMLEFT);
        to = a.getInt(R.styleable.CoachmarkArrow_to, TOPLEFT);
    }

    private void getPaint(TypedArray a) {
        paint = new Paint();

        paint.setColor(a.getColor(R.styleable.CoachmarkArrow_kolor, Color.WHITE));
        paint.setStrokeWidth(a.getDimensionPixelSize(R.styleable.CoachmarkArrow_size, 0));

        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw (Canvas canvas){
        calculatePadding(canvas);
        calculateLinePoints();

        drawLine(canvas, fromPoint, toPoint);
        drawCircle(canvas, fromPoint);
    }

    private void calculateLinePoints() {
        fromPoint = getPoint(from);
        toPoint = getPoint(to);
    }

    private void calculatePadding(Canvas canvas) {

        padding.x = CIRCLE_RADIUS * paint.getStrokeWidth() / canvas.getWidth();
        padding.y = CIRCLE_RADIUS * paint.getStrokeWidth() / canvas.getHeight();
    }

    private PointF getPoint(int position) {
        PointF point = new PointF();

        if (position == TOPRIGHT || position == BOTTOMRIGHT){
            point.x = 1.0f - padding.x;
        }
        else {
            point.x = padding.x;
        }

        if (position == BOTTOMLEFT || position == BOTTOMRIGHT){
            point.y = 1.0f - padding.y;
        }
        else {
            point.y = padding.y;
        }

        return point;
    }

    private void drawCircle(Canvas canvas, PointF fromPoint) {

        canvas.drawCircle(
                canvas.getWidth() * fromPoint.x,
                canvas.getHeight() * fromPoint.y,
                CIRCLE_RADIUS * paint.getStrokeWidth(),
                paint
        );
    }

    private void drawLine(Canvas canvas, PointF fromPoint, PointF toPoint) {
        canvas.drawLine(
                canvas.getWidth() * fromPoint.x,
                canvas.getHeight() * fromPoint.y,
                canvas.getWidth() * toPoint.x,
                canvas.getHeight() * toPoint.y,
                paint
        );
    }
}