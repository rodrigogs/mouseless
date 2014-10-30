package com.sedentary.mouseless.implementation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Rodrigo Gomes da Silva on 29/10/2014.
 */
public class Accelerometer {
    private SensorManager sensorManager = null;
    private Callback callback = null;

    /**
     *
     * @param context
     * @param callback
     */
    public Accelerometer(Context context, Callback callback) {
        this.callback = callback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        load();
    }

    public void load() {
        sensorManager.registerListener(listener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    public void unload() {
        sensorManager.unregisterListener(listener);
    }

    private SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent e) {
            if (callback != null) {
                callback.sensorChanged(e);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // unused
        }
    };

    /**
     *
     */
    public interface Callback {
        void sensorChanged(SensorEvent e);
    }
}
