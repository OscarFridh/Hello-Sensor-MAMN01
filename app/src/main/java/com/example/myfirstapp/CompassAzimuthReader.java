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

    private boolean ignoreRotationVector;
    private float lowPassFilterAlpha;

    private Sensor rotationVectorSensor;
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;

    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    private float RTmp[] = new float[9];
    private float Rot[] = new float[9];
    private float I[] = new float[9];
    private float results[] = new float[3];

    private float[] gravSensorVals;
    private float[] magSensorVals;



    public CompassAzimuthReader(SensorManager sensorManager, CompassAzimuthReaderDelegate delegate, boolean ignoreRotationVector, float lowPassFilterAlpha) {
        this.sensorManager = sensorManager;
        this.delegate = delegate;
        this.ignoreRotationVector = ignoreRotationVector;
        this.lowPassFilterAlpha = lowPassFilterAlpha;
    }

    public void start() throws IOException {
        try {
            if (ignoreRotationVector) {
                throw new IOException();
            }
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

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            azimuth = (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravSensorVals = lowPass(event.values.clone(), gravSensorVals);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magSensorVals = lowPass(event.values.clone(), magSensorVals);
        }

        if (gravSensorVals != null && magSensorVals != null) {
            SensorManager.getRotationMatrix(RTmp, I, gravSensorVals, magSensorVals);
            SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Y, Rot);
            SensorManager.getOrientation(Rot, results);
            azimuth = (((results[0]*180)/Math.PI)+180);
        }
    }

    private float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + lowPassFilterAlpha * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}