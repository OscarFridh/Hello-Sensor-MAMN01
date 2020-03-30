package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class AccelerometersActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    TextView textViewX;
    TextView textViewY;
    TextView textViewZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometers);

        textViewX = findViewById(R.id.x);
        textViewY = findViewById(R.id.y);
        textViewZ = findViewById(R.id.z);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        textViewX.setText("X: " + Math.round(event.values[0]));
        textViewY.setText("Y: " + Math.round(event.values[1]));
        textViewZ.setText("Z: " + Math.round(event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    private void stop() {
        sensorManager.unregisterListener(this, sensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    private void start() {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

}
