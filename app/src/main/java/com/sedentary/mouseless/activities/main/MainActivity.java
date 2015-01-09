package com.sedentary.mouseless.activities.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sedentary.mouseless.R;
import com.sedentary.mouseless.commons.Coordinates;
import com.sedentary.mouseless.implementation.Accelerometer;
import com.sedentary.mouseless.activities.settings.SettingsActivity;

import com.github.nkzawa.socketio.client.Socket;
import com.sedentary.mouseless.commons.MouseClickType;
import com.sedentary.mouseless.implementation.SocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    public static final String COORDINATES_EVENT = "coordinate";
    public static final String MOUSE_CLICK_EVENT = "mouseclick";

    private Accelerometer accelerometer;

    SocketClient socketClient;

    Button btnMouseLeft;
    Button btnMouseRight;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
            if (socketClient == null) {
                createSocket();
            }
            if (socketClient != null) {
                socketClient.connect();
            }
        } else if (id == R.id.action_disconnect) {
            if (socketClient != null) {
                socketClient.disconnect();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        accelerometer.unload();
        if (socketClient != null) {
            socketClient.disconnect();
        }

        super.onDestroy();
    }

    View.OnTouchListener onBtnMouseLeftTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String click = null;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    click = MouseClickType.LEFT_DOWN.toString();
                    break;
                case MotionEvent.ACTION_UP:
                    click = MouseClickType.LEFT_UP.toString();
                    break;
            }

            sendClick(click).run();

            return false;
        }
    };

    View.OnTouchListener onBtnMouseRightTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String click = null;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    click = MouseClickType.RIGHT_DOWN.toString();
                    break;
                case MotionEvent.ACTION_UP:
                    click = MouseClickType.RIGHT_UP.toString();
                    break;
                }

            sendClick(click).run();

            return false;
        }
    };

    /**
     *
     */
    public void createSocket() {
        try {
            socketClient = new SocketClient(
                    settings.getString("serverIp", ""),
                    Integer.valueOf(settings.getString("serverPort", "0")),
                    socketClientCallback);
        } catch (URISyntaxException e) {
            socketClient = null;
        } catch (MalformedURLException e) {
            socketClient = null;
        }

        if (socketClient == null) {
            Toast.makeText(
                    getApplicationContext(), getString(R.string.malformed_url), Toast.LENGTH_LONG).show();
        }
    }

    Accelerometer.Callback accelerometerCallback = new Accelerometer.Callback() {

        @Override
        public void sensorChanged(SensorEvent e) {
            String c = new Gson().toJson(new Coordinates(e.values[0], e.values[1], e.values[2]));
            try {
                JSONObject coords = new JSONObject(c);
                if (socketClient != null) {
                    socketClient.emit(COORDINATES_EVENT, coords);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    };

    /**
     * @param click
     * @return
     */
    private Runnable sendClick(final Object click) {

        Runnable aRunnable = new Runnable(){
            public void run(){
                if (socketClient != null) {
                    socketClient.emit(MOUSE_CLICK_EVENT, click);
                }
            }
        };

        return aRunnable;
    }

    /**
     * @param c
     * @return
     */
    private Runnable sendCoordinates(final Coordinates c) {

        Runnable aRunnable = new Runnable(){
            public void run(){
                String coords = new Gson().toJson(c);

                if (socketClient != null) {
                    socketClient.emit(COORDINATES_EVENT, coords);
                }
            }
        };

        return aRunnable;
    }

    SocketClient.Callback socketClientCallback = new SocketClient.Callback() {

        @Override
        public void connectionStatusChanged(final SocketClient.ConnectionStatus status) {
            final Context context = getApplicationContext();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (status) {
                        case CONNECTED:
                            Toast.makeText(context, getString(R.string.event_connection_connected), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_connected));
                            break;
                        case DISCONNECTED:
                            Toast.makeText(context, getString(R.string.event_connection_disconnected), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_disconnected));
                            break;
                        case RECONNECT_ATTEMPT:
                            Toast.makeText(context, getString(R.string.event_connection_reconnect_attempt), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_reconnect_attempt));
                            break;
                        case RECONNECTED:
                            Toast.makeText(context, getString(R.string.event_connection_reconected), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_reconected));
                            break;
                        case RECONNECTING:
                            Toast.makeText(context, getString(R.string.event_connection_reconnecting), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_reconnecting));
                            break;
                    }
                }
            });
        }

        @Override
        public void message(Object... args) {
            Log.d(TAG, "Mensagem: " + args.toString());
        }

        @Override
        public void error(final SocketClient.ConnectionErrorType type) {
            final Context context = getApplicationContext();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (type) {
                        case ERROR:
                            Toast.makeText(context, getString(R.string.event_connection_error), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_error));
                            break;
                        case CONNECT_ERROR:
                            Toast.makeText(getApplicationContext(), getString(R.string.event_connection_connect_error), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_connect_error));
                            break;
                        case CONNECT_TIMEOUT:
                            Toast.makeText(getApplicationContext(), getString(R.string.event_connection_connect_timeout), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_connect_timeout));
                            break;
                        case RECONNECT_FAILED:
                            Toast.makeText(context, getString(R.string.event_connection_reconnect_failed), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_reconnect_failed));
                            break;
                        case RECONNECT_ERROR:
                            Toast.makeText(context, getString(R.string.event_connection_reconnect_error), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, getString(R.string.event_connection_reconnect_error));
                            break;
                    }
                }
            });
        }
    };
}
