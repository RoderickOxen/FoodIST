package com.tecnico.foodist.util;

public class TCPMessage {

    private String _message;
    private byte[] _signature;

    public TCPMessage(){
        this._message = null;
        this._signature = null;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        this._message = message;
    }

}
