package com.sedentary.mouseless.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sedentary.mouseless.R;
import com.sedentary.mouseless.implementation.Accelerometer;
import com.sedentary.mouseless.activities.settings.SettingsActivity;

import com.github.nkzawa.socketio.client.Socket;
import com.sedentary.mouseless.commons.MouseClickType;
import com.sedentary.mouseless.implementation.SocketClient;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    public static final String COORDINATES_TYPE	= "C";
    public static final String MOUSE_CLICK_TYPE	= "M";

    private Accelerometer accelerometer;

    SocketClient socketClient;

    Button btnMouseLeft;
    Button btnMouseRight;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Accelerometer
        accelerometer = new Accelerometer(getApplicationContext(), accelerometerCallback);
        // Buttons
        btnMouseLeft = (Button) findViewById(R.id.btnMouseLeft);
        btnMouseRight = (Button) findViewById(R.id.btnMouseRight);
        // Buttons listeners
        btnMouseLeft.setOnTouchListener(onBtnMouseLeftTouch);
        btnMouseRight.setOnTouchListener(onBtnMouseRightTouch);
        // Settings
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        // SocketClient
        try {
            socketClient = new SocketClient(
                    settings.getString("foo", "foo"),
                    settings.getInt("bar", 0),
                    socketClientCallback);
        } catch (URISyntaxException e) {
            socketClient = null;
        } catch (MalformedURLException e) {
            socketClient = null;
        }

        if (socketClient == null) {
            Toast.makeText(
                    getApplicationContext(), getString(R.string.malformed_url), Toast.LENGTH_LONG);
        }
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
            socketClient.connect();
        } else if (id == R.id.action_disconnect) {
            socketClient.disconnect();
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

            socketClient.emit(touch);
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

            socketClient.emit(touch);
            return false;
        }
    };

    Accelerometer.Callback accelerometerCallback = new Accelerometer.Callback() {

        @Override
        public void sensorChanged(SensorEvent e) {
            Log.d(TAG, e.toString());
        }
    };

    SocketClient.Callback socketClientCallback = new SocketClient.Callback() {

        @Override
        public void connected() {
            Toast.makeText(getApplicationContext(), getString(R.string.event_connected), Toast.LENGTH_LONG);
        }

        @Override
        public void disconnected() {
            Toast.makeText(getApplicationContext(), getString(R.string.event_disconnected), Toast.LENGTH_LONG);
        }

        @Override
        public void message(Object... args) {

        }
    };
}
