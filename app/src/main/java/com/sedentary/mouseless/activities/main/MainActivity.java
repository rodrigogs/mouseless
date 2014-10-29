package com.sedentary.mouseless.activities.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sedentary.mouseless.R;
import com.sedentary.mouseless.activities.implementation.Accelerometer;
import com.sedentary.mouseless.activities.settings.SettingsActivity;

import com.github.nkzawa.socketio.client.Socket;
import com.sedentary.mouseless.commons.MouseClickType;

public class MainActivity extends Activity implements Accelerometer.Callback {

    private static final String TAG = "MainActivity";

    public static final String COORDINATES_TYPE	= "C";
    public static final String MOUSE_CLICK_TYPE	= "M";

    private Accelerometer accelerometer;

    Socket socket;

    Button btnMouseLeft;
    Button btnMouseRight;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Accelerometer
        accelerometer   = new Accelerometer(getApplicationContext(), this);
        // Buttons
        btnMouseLeft	= (Button) findViewById(R.id.btnMouseLeft);
        btnMouseRight	= (Button) findViewById(R.id.btnMouseRight);
        // Buttons listeners
        btnMouseLeft.setOnTouchListener(onBtnMouseLeftTouch);
        btnMouseRight.setOnTouchListener(onBtnMouseRightTouch);
        // Settings
        settings = PreferenceManager.getDefaultSharedPreferences(this);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_connect) {
            //TODO
            Toast.makeText(getApplicationContext(), "Connect", Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_disconnect) {
            //TODO
            Toast.makeText(getApplicationContext(), "Disconect", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    View.OnTouchListener onBtnMouseLeftTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String touch = "";

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch = MOUSE_CLICK_TYPE + ":" + MouseClickType.LEFT_DOWN;
                    break;
                case MotionEvent.ACTION_UP:
                    touch = MOUSE_CLICK_TYPE + ":" + MouseClickType.LEFT_UP;
                    break;
            }

            socket.send(touch);
            return false;
        }
    };

    View.OnTouchListener onBtnMouseRightTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String touch = "";

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch = MOUSE_CLICK_TYPE + ":" + MouseClickType.RIGHT_DOWN;
                    break;
                case MotionEvent.ACTION_UP:
                    touch = MOUSE_CLICK_TYPE + ":" + MouseClickType.RIGHT_UP;
                    break;
            }

            socket.send(touch);
            return false;
        }
    };

    @Override
    public void sensorChanged(SensorEvent e) {

    }
}
