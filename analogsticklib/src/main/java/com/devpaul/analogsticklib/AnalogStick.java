package com.devpaul.analogsticklib;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Paul Tsouchlos
 * This is a custom analog for this application that only works in the y direction.
 * Movement in the x direction is not supported.
 */
public class AnalogStick extends View {

    public static final int DEFAULT_DIAMETER = 200;
    /**
     * Boolean for if the inner circle is touched.
     */
    private boolean innerTouched;

    /**
     * Outside circle for analog stick paint.
     */
    private Paint outerPaint;

    /**
     * Inner circle paint for the analog stick.
     */
    private Paint innerPaint;

    /**
     * Inner color.
     */
    private int innerColor;

    /**
     * Outer color.
     */
    private int outerColor;

    /**
     * Center x coordinate of analog stick.
     */
    private float cx;

    /**
     * Center y coordinate of analog stick.
     */
    private float cy;

    /**
     * Analog move listener.
     */
    private OnAnalogMoveListener listner;

    /**
     * The inner radius of the view.
     */
    int innerRadius;

    /**
     * The outer radius of the view.
     */
    int outerRadius;

    /**
     * Maximum radius of movement.
     */
    int maxRadius;

    /**
     * Current y coordinate of the center of the inner circle.
     */
    private float innerY;

    /**
     * Current x coordinate of the center of the inner circle.
     */
    private float innerX;

    /**
     * Animator for innerY
     */
    private ObjectAnimator yAnimator;

    /**
     * Animator for innerX
     */
    private ObjectAnimator xAnimator;

    /**
     * Current quadrant for the view.
     */
    private Quadrant curQuadrant;

    /**
     * Returns the max y value set for this view.
     * @return a float of the max value.
     */
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

    /**
     * Max y value.
     */
    private float maxYValue;

    /**
     * Max x value.
     */
    private float maxXValue;

    /**
     * Returns innerY coordinate
     * @return the inner circles center innerY coordinate.
     */
    public float getInnerY() {
        return innerY;
    }

    /**
     * Sets the innery value for the view. Used by the object animator.
     * @param innerY the innerY value to set.
     */
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

    /**
     * Returns the inner X coordinate.
     * @return
     */
    public float getInnerX() {
        return this.innerX;
    }

    /**
     * Sets the inner X coordinate. Used by the x object animator.
     * @param innerX the new inner X coordinate.
     */
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

    /**
     * Constructor for an analog stick. Make sure the width and height are the same.
     * @param context the context passed in to this view.
     * @param attrs Attribute set, needed for xml attributes.
     * @param defStyleAttr optional style.
     */
    public AnalogStick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    /**
     * Constructor for an analog stick.
     * @param context the context passed in to this view.
     * @param attrs an AttributeSet for reading the xml attributes.
     */
    public AnalogStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    /**
     * Simple Constructor
     * @param context the context passed in to this view.
     */
    public AnalogStick(Context context) {
        super(context);
        initialize(context, null);
    }

    /**
     * Initializes various parts of the view.
     * @param context the context passed in to this view.
     * @param attrs an AttributeSet for reading the xml attributes.
     */
    private void initialize(Context context, AttributeSet attrs) {
        //initialize everything
        //animator
        yAnimator = new ObjectAnimator().ofFloat(this, "innerY", 0f, 0f);
        xAnimator = new ObjectAnimator().ofFloat(this, "innerX", 0f, 0f);

        //set focus.
        setFocusable(true);
        if(attrs != null) {
            //read the attributes.
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnalogStick);
            innerColor = typedArray.getColor(R.styleable.AnalogStick_centerStickColor, getResources().getColor(android.R.color.holo_blue_bright));
            outerColor = typedArray.getColor(R.styleable.AnalogStick_outerCircleColor, getResources().getColor(android.R.color.darker_gray));

        }

        //initialize the paints.
        outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerPaint.setAntiAlias(false);
        outerPaint.setStyle(Paint.Style.FILL);
        outerPaint.setColor(outerColor);

        innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setAntiAlias(false);
        innerPaint.setStyle(Paint.Style.FILL);
        innerPaint.setColor(innerColor);

        //initialize the max values.
        maxXValue = 0;
        maxYValue = 0;
        //initialize touch boolean.
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

        //the action
        int action = event.getAction();
        //touch points.
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
                    float vecx = touchX - cx;
                    //y is positive down
                    float vecy = touchY - cy;
                    float angle = getAngle(vecx, vecy);
                    //all movement of the analog stick center should be within the radius of the larger
                    //circle minus the radius of the analog stick.
                    int magnitude = getVectorMagnitude(touchX - (int) cx, touchY - (int) cy);
                    if(magnitude >= maxRadius) {
                        //inner stick is too far away so we need to fix that.
                        innerX = (float) Math.cos(Math.toRadians(angle))  * maxRadius;
                        innerY = (float) Math.sin(Math.toRadians(angle)) * maxRadius;
                        innerX += cx;
                        innerY += cy;
                    } else if(magnitude < maxRadius) {
                        //inner stick isn't too far away.
                        innerX = touchX;
                        innerY = touchY;
                    } else {
                        //not really needed but oh well.
                        innerX = touchX;
                        innerY = touchY;
                    }

                    //handle scaled events.
                    if(listner != null) {
                        if(maxXValue != 0) {
                           //max x valule has been set so scale the movements.
                           //get the max distance you can move in the x axis.
                            float maxX = maxRadius;
                            float curX = innerX - cx;
                            float ratio = curX/maxX;
                            listner.onAnalogMovedScaledX(maxXValue * ratio);

                        }
                        if(maxYValue != 0) {
                            //max y value has been set so scale the movements to this value.
                            //get max distance
                            float maxY = maxRadius;
                            float curY = cy - innerY; //remember y is positive down.
                            float ratio = curY/maxY;
                            listner.onAnalogMovedScaledY(maxYValue * ratio);
                        }

                        //notify the listener of the raw movements.
                        listner.onAnalogMove(innerX, innerY);
                        //notify the listener of the angle.
                        listner.onAnalogMovedGetAngle(angle);
                        //notify the listener of the quadrant.
                        if(angle >=0 && angle <=90) {
                            curQuadrant = Quadrant.BOTTOM_RIGHT;
                        } else if (angle > 90 && angle <= 180) {
                            curQuadrant = Quadrant.BOTTOM_LEFT;
                        } else if(angle > 180 && angle <= 270) {
                            curQuadrant = Quadrant.TOP_LEFT;
                        } else {
                            curQuadrant = Quadrant.TOP_RIGHT;
                        }
                        if(curQuadrant != null) {
                            listner.onAnalogMovedGetQuadrant(curQuadrant);
                        }
                    }
                    //invalidate the view.
                    invalidate();

                }
                break;

            case MotionEvent.ACTION_UP:
                //let go of the stick so return it to the center.
                returnSticktoCenter();
                innerTouched = false;
                break;
        }
        return true;
    }

    /**
     * Returns the magnitude of a vector
     * @param vecx the x component of the vector.
     * @param vecy the y component of the vector
     * @return the value of the magnitude of the vector.
     */
    private int getVectorMagnitude(int vecx, int vecy) {
        return (int) Math.sqrt(Math.pow(vecx, 2) + Math.pow(vecy, 2));
    }

    /**
     * Gets the angle between two points with respect to the x axis. A positive angle is
     * clockwise from the x axis.
     * @param touchX the touch x coordinate.
     * @param touchY the touch y coordinate.
     * @return the angle in degrees.
     */
    public float getAngle(float touchX, float touchY) {
        //use atan2, make sure its (y, x) not (x, y)
        float angle = (float) Math.toDegrees(Math.atan2(touchY, touchX));
        //scale the value so that we never get negative values.
        if(angle < 0) {
            angle += 360;
        }
        return angle;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = getWidth() / 2;
        cy = getWidth() / 2;
        innerY = cy;
        innerX = cx;
        int d = Math.min(w, h);
        innerRadius = (int) (d / 2 * 0.25);
        outerRadius = (int) (d / 2 * 0.75);

        maxRadius = outerRadius- (int) (innerRadius * 0.65);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));
        setMeasuredDimension(d, d);
    }

    /**
     * Measures the view given a measure spec.
     * @param measureSpec the measure spec from onMeasure.
     * @return the measurement.
     */
    private int measure(int measureSpec) {
        int measurement = 0;

        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            measurement = DEFAULT_DIAMETER;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            measurement = specSize;
        }
        return measurement;
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


