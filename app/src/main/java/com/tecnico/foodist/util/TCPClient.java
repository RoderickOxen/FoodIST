package com.tecnico.foodist.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPClient extends AsyncTask<String, Void, Void> {

    //Client has server socket
    static Socket socket;
    static final String host = "10.0.2.2";
    static final int portNumber = 8888;
    static PrintWriter printWriter;

    String message;
    Context context;

    public TCPClient(Context c, String m){
        this.context=c;
        this.message = m;
    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected Void doInBackground(String... strings) {
        try {

            socket = new Socket(host, portNumber);
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.write(message);
            printWriter.flush();
            printWriter.close();



        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }
}
