package com.newland.nsdkdemo.common;

public class MessageEvent {
    private String message;
    private int messageType;

    public MessageEvent(String message, int messageType){
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
