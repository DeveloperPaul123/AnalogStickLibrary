package com.devpaul.analogstick;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.devpaul.analogsticklib.AnalogStick;
import com.devpaul.analogsticklib.OnAnalogMoveListener;
import com.devpaul.analogsticklib.Quadrant;


public class MainActivity extends Activity {

    TextView angle, rawX, rawY, quadrant, scaledX, scaledY;
    AnalogStick analogStick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        angle = (TextView) findViewById(R.id.angle);
        rawX = (TextView) findViewById(R.id.raw_x);
        rawY = (TextView) findViewById(R.id.raw_y);
        quadrant = (TextView) findViewById(R.id.quadrant);
        scaledX = (TextView) findViewById(R.id.scaled_x);
        scaledY = (TextView) findViewById(R.id.scaled_y);

        analogStick = (AnalogStick) findViewById(R.id.analog_stick);
        analogStick.setMaxYValue(30f);
        analogStick.setMaxXValue(30f);
        analogStick.setOnAnalogMoveListner(new OnAnalogMoveListener() {
            @Override
            public void onAnalogMove(float x, float y) {
                rawX.setText("X: " + (int) x);
                rawY.setText("Y: " + (int) y);
            }

            @Override
            public void onAnalogMovedScaledX(float scaledX) {
                MainActivity.this.scaledX.setText("ScaledX: " + (int) scaledX);
            }

            @Override
            public void onAnalogMovedScaledY(float scaledY) {
                MainActivity.this.scaledY.setText("ScaledY: " + (int) scaledY);
            }

            @Override
            public void onAnalogMovedGetAngle(float angle) {
                MainActivity.this.angle.setText("Angle: " + (int) angle);
            }

            @Override
            public void onAnalogMovedGetQuadrant(Quadrant quadrant) {
                MainActivity.this.quadrant.setText("Quadrant " + quadrant.toString());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
