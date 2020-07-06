package com.company;

public class Message {
    String sender, receiver;
    private String message;

    public Message(String sender, String receiver, String message) {
        this.sender = new String(sender);
        this.receiver = new String(receiver);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
