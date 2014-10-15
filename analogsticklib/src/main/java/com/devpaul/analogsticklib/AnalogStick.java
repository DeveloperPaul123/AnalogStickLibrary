package com.devpaul.analogsticklib;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Paul Tsouchlos
 * This is a custom analog for this application that only works in the y direction.
 * Movement in the x direction is not supported.
 */
public class AnalogStick extends View {

    private boolean innerTouched;

    public enum DirectionX {NULL, LEFT, RIGHT};
    public enum DirectionY {NULL, UP, DOWN};

    private Paint outerPaint;
    private Paint innerPaint;

    private int innerColor;
    private int outerColor;

    private float cx;
    private float cy;

    private OnAnalogMoveListener listner;

    private DirectionX curXDirection;
    private DirectionY curYDirection;

    int innerRadius;
    int outerRadius;
    int maxRadius;

    private float innerY;
    private float innerX;

    private ObjectAnimator yAnimator;
    private ObjectAnimator xAnimator;

    private Quadrant curQuadrant;

    public float getMaxYValue() {
        return maxYValue;
    }

    /**
     * This will scale all the movement of the stick so that when the stick is all the way up you
     * get +maxYValue returned in OnAnalogStickMovedListener and -maxYValue when it's all the way
     * down
     * @param maxYValue
     */
    public void setMaxYValue(float maxYValue) {
        this.maxYValue = maxYValue;
    }

    public float getMaxXValue() {
        return maxXValue;
    }

    /**
     * This will scale the movement of the analog stick in the x direction so that +maxXValue is
     * returned when all the way to the right and -maxXValue all the way to the left.
     * @param maxXValue
     */
    public void setMaxXValue(float maxXValue) {
        this.maxXValue = maxXValue;
    }

    private float maxYValue;
    private float maxXValue;

    public float getInnerY() {
        return innerY;
    }

    public void setInnerY(float innerY) {
        this.innerY = innerY;
        if(listner != null) {
            if(maxYValue != 0) {
                //max y value has been set so scale the movements to this value.
                //get max distance
                float maxY = outerRadius - innerRadius;
                float curY = cy - innerY; //remember y is positive down.
                float ratio = curY/maxY;
                listner.onAnalogMovedScaledY(maxYValue * ratio);
            }
        }

        invalidate();
    }

    public float getInnerX() {
        return this.innerX;
    }
    public void setInnerX(float innerX) {
        this.innerX = innerX;
        if(listner !=  null) {
            if(maxXValue != 0) {
                //max x value has been set so scale movemenets to this value.
                //get max distance.
                float maxX = outerRadius - innerRadius;
                float curX = innerX - cx;
                float ratio = curX/maxX;
                listner.onAnalogMovedScaledX(maxXValue * ratio);
            }
        }

        invalidate();
    }

    public AnalogStick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    public AnalogStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public AnalogStick(Context context) {
        super(context);
        initialize(context, null);
    }

    private void initialize(Context context, AttributeSet attrs) {
        //initialize everything
        //animator
        yAnimator = new ObjectAnimator().ofFloat(this, "innerY", 0f, 0f);
        xAnimator = new ObjectAnimator().ofFloat(this, "innerX", 0f, 0f);

        //set focus.
        setFocusable(true);
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnalogStick);
            innerColor = typedArray.getColor(R.styleable.AnalogStick_centerStickColor, getResources().getColor(android.R.color.holo_blue_bright));
            outerColor = typedArray.getColor(R.styleable.AnalogStick_outerCircleColor, getResources().getColor(android.R.color.darker_gray));

        }

        //initialize the paint.
        outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerPaint.setAntiAlias(false);
        outerPaint.setStyle(Paint.Style.FILL);
        outerPaint.setColor(outerColor);

        innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setAntiAlias(false);
        innerPaint.setStyle(Paint.Style.FILL);
        innerPaint.setColor(innerColor);

        maxXValue = 0;
        maxYValue = 0;

        innerTouched = false;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        //draw the circles
        canvas.drawCircle(cx, cy, outerRadius, outerPaint);
        //inner circle
        canvas.drawCircle(innerX, innerY, innerRadius, innerPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //check to see if the inner circle has been touched.
                if(touchY <= innerY + innerRadius && touchY >= innerY - innerRadius) {
                    if(touchX <= innerX + innerRadius && touchX >= innerX - innerRadius) {
                        innerTouched = true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if(innerTouched) {
                    //stop any animation that is running.
                    if(yAnimator.isRunning() || xAnimator.isRunning()) {
                        yAnimator.cancel();
                        xAnimator.cancel();
                    }
                    //get the angle.
                    float vecx = innerX - cx;
                    //y is positive down
                    float vecy = cy - innerY;
                    float angle = getAngle(vecx, vecy);

                    //all movement of the analog stick center should be within the radius of the larger
                    //circle minus the radius of the analog stick.
                    float magnitude = getVectorMagnitude(touchX - maxRadius, touchY - maxRadius);
                    if(magnitude <= maxRadius) {
                        innerX = touchX;
                        innerY = touchY;
                    }
                    else if(magnitude > maxRadius) {
                        //inner stick is too far away so we need to fix that.
                        innerX = (float) Math.cos(Math.toRadians(angle))  * maxRadius;
                        innerY = (float) Math.sin(Math.toRadians(angle)) * maxRadius;
                    }

                    //handle scaled events.
                    if(listner != null) {
                        if(maxXValue != 0) {
                           //max x valule has been set so scale the movements.
                           //get the max distance you can move in the x axis.
                            float maxX = outerRadius - innerRadius;
                            float curX = touchX - cx;
                            float ratio = curX/maxX;
                            listner.onAnalogMovedScaledX(maxXValue * ratio);

                        }
                        if(maxYValue != 0) {
                            //max y value has been set so scale the movements to this value.
                            //get max distance
                            float maxY = outerRadius - innerRadius;
                            float curY = cy - innerY; //remember y is positive down.
                            float ratio = curY/maxY;
                            listner.onAnalogMovedScaledY(maxYValue * ratio);
                        }

                        listner.onAnalogMove(cx, innerY);
                    }
                    invalidate();

                }
                break;
            case MotionEvent.ACTION_UP:
                returnSticktoCenter();
                innerTouched = false;
                break;
        }
        return true;
    }

    private float getVectorMagnitude(int vecx, int vecy) {
        return (float) Math.sqrt(Math.pow(vecx, 2) + Math.pow(vecy, 2));
    }

    public float getAngle(float touchX, float touchY) {

        float angle = (float) Math.toDegrees(Math.atan2(touchY, touchX));
        if(angle < 0) {
            angle += 360;
        }
        Log.d("ANALOG", "Angle: " + angle);
        return angle;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = (int) getWidth() / 2;
        cy = (int) getWidth() / 2;
        innerY = cy;
        innerX = cx;
        int d = Math.min(w, h);
        innerRadius = (int) (d / 2 * 0.25);
        outerRadius = (int) (d / 2 * 0.75);

        maxRadius = outerRadius-innerRadius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;

        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    /**
     * Returns the stick to the center after the stick is let go.
     */
    private void returnSticktoCenter() {
        yAnimator.setDuration(500);
        yAnimator.setFloatValues(cy);

        xAnimator.setDuration(500);
        xAnimator.setFloatValues(cx);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(yAnimator).with(xAnimator);
        animatorSet.start();
    }

    /**
     * Sets a listener for this view that returns various values.
     * @param listner the listener to use for this view. See {@link com.devpaul.analogsticklib.OnAnalogMoveListener}
     *                for more info.
     */
    public void setOnAnalogMoveListner(OnAnalogMoveListener listner) {
        this.listner = listner;
    }


}


