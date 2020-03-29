package com.example.myfirstapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.IOException;

public class CompassAzimuthReader implements SensorEventListener {

    private double azimuth;
    private SensorManager sensorManager;
    private CompassAzimuthReaderDelegate delegate;

    private float lowPassFilterAlpha = 0.25f;
    private boolean shouldFilter = true;

    private Sensor rotationVectorSensor;
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;

    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private float[] RTmp = new float[9];
    private float[] I = new float[9];
    private float[] results = new float[3];
    private float[] accelerometerValues;
    private float[] magnetometerValues;

    private float[] lastUnfilteredAccelerometerValues = new float[3];
    private float[] lastUnfilteredMagnetometerValues = new float[3];

    public CompassAzimuthReader(SensorManager sensorManager, CompassAzimuthReaderDelegate delegate) {
        this.sensorManager = sensorManager;
        this.delegate = delegate;
    }

    public void startAccelerometerAndMagnetometer(boolean filter, float alpha) throws IOException {
        lowPassFilterAlpha = alpha;
        shouldFilter = filter;
        accelerometerSensor = registerSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = registerSensor(Sensor.TYPE_MAGNETIC_FIELD);
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
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
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
        updateAzimuth(event);
        delegate.updateCompass(azimuth);
    }

    private void updateAzimuth(SensorEvent event) {

        // A) Rotation vector

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            azimuth = (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;
            return;
        }

        // B) Accelerometer and magnetometer

        if (shouldFilter) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = lowPass(event.values.clone(), accelerometerValues);
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magnetometerValues = lowPass(event.values.clone(), magnetometerValues);
            }

            if (accelerometerValues != null && magnetometerValues != null) {
                SensorManager.getRotationMatrix(RTmp, I, accelerometerValues, magnetometerValues);
                SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Y, rotationMatrix);
                SensorManager.getOrientation(rotationMatrix, results);
                azimuth = (((results[0]*180)/Math.PI)+180);
            }

        } else {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, lastUnfilteredAccelerometerValues, 0, event.values.length);
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, lastUnfilteredMagnetometerValues, 0, event.values.length);
            }

            if (lastUnfilteredAccelerometerValues != null && lastUnfilteredMagnetometerValues != null) {
                SensorManager.getRotationMatrix(rotationMatrix, null, lastUnfilteredAccelerometerValues, lastUnfilteredMagnetometerValues);
                SensorManager.getOrientation(rotationMatrix, orientation);
                azimuth = (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;
            }
        }
    }

    private float[] lowPass( float[] input, float[] output ) {
        if (output == null) {
            return input;
        }

        for (int i=0; i<input.length; i++) {
            output[i] = output[i] + lowPassFilterAlpha * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}