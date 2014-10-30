package com.sedentary.mouseless.implementation;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

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
     * @param socketClientCallback
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public SocketClient(String address, Integer port, Callback callback) throws URISyntaxException, MalformedURLException {
        this.callback = callback;
        URL url = new URL("http", address, port, "");

        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.reconnection = true;

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
                callback.connected();
            }
        }
    };

    Emitter.Listener onDisconnect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (callback != null) {
                callback.disconnected();
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

    /**
     *
     */
    public interface Callback {
        public void connected();
        public void disconnected();
        public void message(Object... args);
    }
}
