package com.example.myfirstapp;

import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class CompassActivity extends AppCompatActivity implements CompassAzimuthReaderDelegate {

    private ImageView compass_img;
    private TextView txt_compass;
    private CompassAzimuthReader compassAzimuthReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassAzimuthReader = new CompassAzimuthReader((SensorManager) getSystemService(SENSOR_SERVICE), this, false);
        compass_img = (ImageView) findViewById(R.id.img_compass);
        txt_compass = (TextView) findViewById(R.id.txt_azimuth);
    }

    @Override
    public void updateCompass(int mAzimuth) {
        compass_img.setRotation(-mAzimuth);
        txt_compass.setText(mAzimuth + "° " + heading(mAzimuth));
    }

    private String heading(int mAzimuth) {
        if (mAzimuth >= 350 || mAzimuth <= 10) {
            return "N";
        } else if (mAzimuth < 350 && mAzimuth > 280) {
            return "NW";
        } else if (mAzimuth <= 280 && mAzimuth > 260) {
            return "W";
        } else if (mAzimuth <= 260 && mAzimuth > 190) {
            return "SW";
        } else if (mAzimuth <= 190 && mAzimuth > 170) {
            return "S";
        } else if (mAzimuth <= 170 && mAzimuth > 100) {
            return "SE";
        } else if (mAzimuth <= 100 && mAzimuth > 80) {
            return "E";
        } else {
            return "NE";
        }
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