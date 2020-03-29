package com.example.myfirstapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.IOException;

public class CompassAzimuthReader implements SensorEventListener {

    private int mAzimuth;
    private SensorManager mSensorManager;
    private CompassAzimuthReaderDelegate delegate;

    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    private float[] rMat = new float[9];
    private float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    public CompassAzimuthReader(SensorManager sensorManager, CompassAzimuthReaderDelegate delegate) {
        this.mSensorManager = sensorManager;
        this.delegate = delegate;
    }

    public void start() throws IOException {
        try {
            mRotationV = registerSensor(Sensor.TYPE_ROTATION_VECTOR);
        } catch (IOException e) {
            mAccelerometer = registerSensor(Sensor.TYPE_ACCELEROMETER);
            mMagnetometer = registerSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    private Sensor registerSensor(int sensorType) throws IOException {
        if (mSensorManager.getDefaultSensor(sensorType) == null) {
            throw new IOException("Failed to get sensor of type: " + sensorType);
        }
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if(!mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)) {
            throw new IOException("Failed to register listener for sensor of type: " + sensorType);
        }
        return sensor;
    }

    public void stop() {
        unregisterListener(mRotationV);
        unregisterListener(mAccelerometer);
        unregisterListener(mMagnetometer);
    }

    private void unregisterListener(Sensor sensor) {
        if (sensor != null) {
            mSensorManager.unregisterListener(this, sensor);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        delegate.updateCompass(mAzimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
