package com.devpaul.analogsticklib;

public interface OnAnalogMoveListener {
    /**
     * Returns the raw x and y coordinates of the center stick relative to the center of the larger
     * circle.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void onAnalogMove(float x, float y);

    /**
     * Returns a scaled version of the x value. This is based on the max value set
     * @param scaledX the scaled x value. The most it can be is the max that was set.
     */
    public void onAnalogMovedScaledX(float scaledX);

    /**
     * Returns a scaled version of the y value. This is based on the max y value set.
     * @param scaledY the scaled y value. The most it can be is the max that was set.
     */
    public void onAnalogMovedScaledY(float scaledY);

    /**
     * Returns the current angle of the analog stick. This is measured counterclockwise from the
     * right half of the x axis of the larger circle.
     * @param angle the angle in degrees.
     */
    public void onAnalogMovedGetAngle(float angle);

    /**
     * Returns the quadrant location of the analog stick.
     * @param quadrant the quadrant its in. See {@link com.devpaul.analogsticklib.Quadrant} for more
     *                 info.
     */
    public void onAnalogMovedGetQuadrant(Quadrant quadrant);
}
