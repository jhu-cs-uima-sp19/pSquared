package com.example.psquared;

import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
        //dummy = false;
    }
/*
    public ChatMessage(String messageText, String messageUser, Boolean dum) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
        dummy = true;
    }
*/
    public ChatMessage() {

    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

}
