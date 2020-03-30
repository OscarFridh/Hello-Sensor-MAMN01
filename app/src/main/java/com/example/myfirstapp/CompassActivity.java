package com.example.myfirstapp;

import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class CompassActivity extends AppCompatActivity implements CompassAzimuthReaderDelegate {

    private ImageView imageView;
    private TextView textView;
    private View backgroundView;

    private CompassAzimuthReader compassAzimuthReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassAzimuthReader = new CompassAzimuthReader((SensorManager) getSystemService(SENSOR_SERVICE), this);

        imageView = (ImageView) findViewById(R.id.img_compass);
        textView = (TextView) findViewById(R.id.txt_azimuth);
        backgroundView = findViewById(R.id.activity_compass).getRootView();
    }

    @Override
    public void updateCompass(double azimuth) {
        imageView.setRotation((float)-azimuth);
        textView.setText(Math.round(azimuth) + "° " + heading(azimuth));

        float fraction = (float)headingNorth(azimuth);

        int backgroundColor = interpolateColor(getResources().getColor(android.R.color.white), getResources().getColor(android.R.color.holo_red_dark), fraction);
        int textColor = interpolateColor(getResources().getColor(android.R.color.black), getResources().getColor(android.R.color.white), fraction);

        backgroundView.setBackgroundColor(backgroundColor);
        textView.setTextColor(textColor);
    }

    private float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }

    /** Returns an interpoloated color, between <code>a</code> and <code>b</code> */
    private int interpolateColor(int a, int b, float proportion) {
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        Color.colorToHSV(a, hsva);
        Color.colorToHSV(b, hsvb);
        for (int i = 0; i < 3; i++) {
            hsvb[i] = interpolate(hsva[i], hsvb[i], proportion);
        }
        return Color.HSVToColor(hsvb);
    }

    private String heading(double azimuth) {
        if (azimuth >= 350 || azimuth <= 10) {
            return "N";
        } else if (azimuth < 350 && azimuth > 280) {
            return "NW";
        } else if (azimuth <= 280 && azimuth > 260) {
            return "W";
        } else if (azimuth <= 260 && azimuth > 190) {
            return "SW";
        } else if (azimuth <= 190 && azimuth > 170) {
            return "S";
        } else if (azimuth <= 170 && azimuth > 100) {
            return "SE";
        } else if (azimuth <= 100 && azimuth > 80) {
            return "E";
        } else {
            return "NE";
        }
    }

    private double headingNorth(double azimuth) {

        double threshold = 30.0;

        // First map to a value between -1 and 1 based on threshold.
        // Then clam it and scale it to -90 to 90
        // Finally return the cosine of that value
        double p = (Math.sin(Math.toRadians(azimuth)) > 0) ? azimuth / threshold : (azimuth - 360) / threshold;
        p = Math.max(Math.min(p, 1), -1);
        p *= 90;
        return Math.cos(Math.toRadians(p));
    }

    @Override
    protected void onPause() {
        super.onPause();
        compassAzimuthReader.stop();
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();

            compassAzimuthReader.start();

            // Use these instead for a custom low pass filter:
            //compassAzimuthReader.startAccelerometerAndMagnetometer(true, 0.125f); // Low pass filter
            //compassAzimuthReader.startAccelerometerAndMagnetometer(false, 0f); // No filter

        } catch (IOException e) {
            noSensorsAlert();
        }
    }

    private void noSensorsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }
}