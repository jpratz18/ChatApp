package com.mac.chatapp.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by admin on 12/06/2016.
 */
@IgnoreExtraProperties
public class Message {

    public String to;
    public String from;
    public Date date;
    public String message;
    public String typeMessage;

    public Message () {
    }

    public Message (String to, String from, Date date, String message, String typeMessage) {
        this.to = to;
        this.from = from;
        this.date = date;
        this.message = message;
        this.typeMessage = typeMessage;
    }

}
