package com.nooty.nootylivenoots.models;

public class EncapsulatedMessage {
    public String messageType;
    public String messageData;

    public EncapsulatedMessage(String messageType, String messageData) {
        this.messageType = messageType;
        this.messageData = messageData;
    }
}
