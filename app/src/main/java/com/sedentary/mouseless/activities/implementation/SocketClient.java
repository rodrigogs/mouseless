package com.sedentary.mouseless.activities.implementation;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Rodrigo Gomes da Silva on 29/10/2014.
 */
public class SocketClient {
    Socket socket = null;


    public SocketClient(String address, Integer port) throws URISyntaxException {
        StringBuilder url = new StringBuilder();
        url.append("http://");
        url.append(address);
        url.append(":");
        url.append(String.valueOf(port));

        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.reconnection = true;

        socket = IO.socket(url.toString(), options);
    }



    /**
     *
     */
    public interface Callback {
        public void connected();
        public void disconnected();
    }
}
