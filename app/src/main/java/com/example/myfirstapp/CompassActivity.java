package com.example.myfirstapp;

import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class CompassActivity extends AppCompatActivity implements CompassAzimuthReaderDelegate {

    private ImageView compass_img;
    private TextView txt_compass;
    private View backgroundView;

    private CompassAzimuthReader compassAzimuthReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassAzimuthReader = new CompassAzimuthReader((SensorManager) getSystemService(SENSOR_SERVICE), this);
        compass_img = (ImageView) findViewById(R.id.img_compass);
        txt_compass = (TextView) findViewById(R.id.txt_azimuth);
        backgroundView = findViewById(R.id.activity_compass).getRootView();
    }

    @Override
    public void updateCompass(double azimuth) {
        compass_img.setRotation((float)-azimuth);
        txt_compass.setText(Math.round(azimuth) + "° " + heading(azimuth));

        if (headingNorth(azimuth)) {
            backgroundView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            txt_compass.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            backgroundView.setBackgroundColor(getResources().getColor(android.R.color.white));
            txt_compass.setTextColor(getResources().getColor(android.R.color.black));
        }
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

    private boolean headingNorth(double azimuth) {
        return azimuth >= 345 || azimuth <= 15;
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