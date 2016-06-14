package com.mac.chatapp.util;

/**
 * Created by admin on 12/06/2016.
 */
public enum MessageType {

    TEXT("TEXT"),
    IMAGE("IMAGE");

    private String value;

    private MessageType (String value) {
        this.value = value;
    }

    public String getValue () {
        return this.value;
    }

}
