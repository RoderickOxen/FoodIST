package com.tecnico.foodist.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tecnico.foodist.ui.GlobalClass;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("DuplicateBranchesInSwitch")
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
            String[] convertedMessage = message.split("-");
            String operation = convertedMessage[0];

            DataOutputStream dataOutputStream =
                    new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream =
                    new DataInputStream(socket.getInputStream());
            switch (operation) {
                case "ARU":
                    dataOutputStream.writeUTF(message);
                    dataOutputStream.close();
                    break;

                case "LR":
                    //asks for queue time
                    dataOutputStream.writeUTF(message);

                    //wait for response
                    String response = dataInputStream.readUTF();

                    Log.w("response",response);

                    dataOutputStream.close();
                    dataInputStream.close();
                    break;

                case "LRALL":
                    //asks for queue time
                    dataOutputStream.writeUTF(message);

                    //wait for response
                    String resp = dataInputStream.readUTF();

                    Log.w("response",resp);

                    GlobalClass globalVariable = (GlobalClass) context.getApplicationContext();
                    globalVariable.setAllQueueTime(resp);

                    dataOutputStream.close();
                    dataInputStream.close();
                    break;

                case "LRALLT":
                    //asks for queue time
                    dataOutputStream.writeUTF(message);

                    //wait for response
                    String answ = dataInputStream.readUTF();

                    Log.w("response",answ);

                    globalVariable = (GlobalClass) context.getApplicationContext();
                    globalVariable.setAllQueueTimeTagus(answ);
                    dataOutputStream.close();
                    dataInputStream.close();
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }
}
