package com.example.myfirstapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.IOException;

public class CompassAzimuthReader implements SensorEventListener {

    private int azimuth;
    private SensorManager sensorManager;
    private CompassAzimuthReaderDelegate delegate;

    private Sensor rotationVectorSensor;
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;

    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    public CompassAzimuthReader(SensorManager sensorManager, CompassAzimuthReaderDelegate delegate) {
        this.sensorManager = sensorManager;
        this.delegate = delegate;
    }

    public void start() throws IOException {
        try {
            rotationVectorSensor = registerSensor(Sensor.TYPE_ROTATION_VECTOR);
        } catch (IOException e) {
            accelerometerSensor = registerSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometerSensor = registerSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    private Sensor registerSensor(int sensorType) throws IOException {
        if (sensorManager.getDefaultSensor(sensorType) == null) {
            throw new IOException("Failed to get sensor of type: " + sensorType);
        }
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if(!sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)) {
            throw new IOException("Failed to register listener for sensor of type: " + sensorType);
        }
        return sensor;
    }

    public void stop() {
        unregisterListener(rotationVectorSensor);
        unregisterListener(accelerometerSensor);
        unregisterListener(magnetometerSensor);
    }

    private void unregisterListener(Sensor sensor) {
        if (sensor != null) {
            sensorManager.unregisterListener(this, sensor);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }
        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;
        }

        azimuth = Math.round(azimuth);
        delegate.updateCompass(azimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}