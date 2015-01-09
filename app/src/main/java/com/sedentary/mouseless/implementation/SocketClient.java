package com.sedentary.mouseless.implementation;

import android.content.Context;
import android.os.Looper;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import 	android.os.Handler;
import android.widget.Toast;

/**
 * Created by Rodrigo Gomes da Silva on 29/10/2014.
 */
public class SocketClient {
    private Socket socket = null;
    private Callback callback = null;

    /**
     *
     * @param address
     * @param port
     * @param callback
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public SocketClient(String address, Integer port, Callback callback) throws URISyntaxException, MalformedURLException {
        this.callback = callback;

        URL url = new URL("http", address, port, "");

        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.reconnection = true;
        options.reconnectionDelay = 5000;

        socket = IO.socket(url.toURI(), options);

        registerEvents();
    }

    /**
     *
     */
    private void registerEvents() {
        // onConnect
        socket.on(Socket.EVENT_CONNECT, onConnect);
        // onDisconnect
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        // onMessage
        socket.on(Socket.EVENT_MESSAGE, onMessage);
        // onConnectError
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        // onError
        socket.on(Socket.EVENT_ERROR, onError);
        //onConnectTimeout
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
        //onReconnect
        socket.on(Socket.EVENT_RECONNECT, onReconnect);
        //onReconnectAttempt
        socket.on(Socket.EVENT_RECONNECT_ATTEMPT, onReconnectAttempt);
        //onReconnectError
        socket.on(Socket.EVENT_RECONNECT_ERROR, onReconnectError);
        //onReconnectFailed
        socket.on(Socket.EVENT_RECONNECT_FAILED, onReconnectFailed);
        //onReconnecting
        socket.on(Socket.EVENT_RECONNECTING, onReconnecting);
    }

    /**
     *
     */
    public void connect() {
        socket.connect();
    }

    /**
     *
     */
    public void disconnect() {
        socket.disconnect();
        socket.close();
    }

    /**
     *
     * @param args
     */
    public void emit(String event, Object... args) {
        socket.emit(event, args);
    }

    Emitter.Listener onConnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.connectionStatusChanged(ConnectionStatus.CONNECTED);
            }
        }
    };

    Emitter.Listener onDisconnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.connectionStatusChanged(ConnectionStatus.DISCONNECTED);
            }
        }
    };

    Emitter.Listener onMessage = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.message(args);
            }
        }
    };

    Emitter.Listener onConnectError = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.error(ConnectionErrorType.CONNECT_ERROR);
            }
        }
    };

    Emitter.Listener onReconnectError = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.error(ConnectionErrorType.RECONNECT_ERROR);
            }
        }
    };

    Emitter.Listener onError = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.error(ConnectionErrorType.ERROR);
            }
        }
    };

    Emitter.Listener onConnectTimeout = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.error(ConnectionErrorType.CONNECT_TIMEOUT);
            }
        }
    };

    Emitter.Listener onReconnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.connectionStatusChanged(ConnectionStatus.RECONNECTED);
            }
        }
    };

    Emitter.Listener onReconnectAttempt = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.connectionStatusChanged(ConnectionStatus.RECONNECT_ATTEMPT);
            }
        }
    };

    Emitter.Listener onReconnectFailed = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.error(ConnectionErrorType.RECONNECT_FAILED);
            }
        }
    };

    Emitter.Listener onReconnecting = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.connectionStatusChanged(ConnectionStatus.RECONNECTING);
            }
        }
    };

    /**
     *
     */
    public interface Callback {
        void connectionStatusChanged(ConnectionStatus status);
        void message(Object... args);
        void error(ConnectionErrorType type);
    }

    /**
     *
     */
    public enum ConnectionStatus {
        CONNECTED, DISCONNECTED, RECONNECTED, RECONNECT_ATTEMPT, RECONNECTING
    }

    public enum ConnectionErrorType {
        ERROR, CONNECT_ERROR, CONNECT_TIMEOUT, RECONNECT_ERROR, RECONNECT_FAILED
    }
}
