package com.nikita.sender;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SenderAsyncTask extends AsyncTask {

    private Context context;
    private static final String TAG = "NIKITA";
    private int port;
    private String receiver;
    private InetAddress addressInfo;
    private Socket socket;
    private OutputStream outputStream;
    byte buf[] = new byte[1024];

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.receiver = address;
    }

    public void setInetAddress(InetAddress addressInfo) {
        this.addressInfo = addressInfo;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        socket = new Socket();
        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(receiver, port)), 500);
            outputStream = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
    }
}
