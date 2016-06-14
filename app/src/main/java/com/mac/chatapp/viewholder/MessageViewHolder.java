package com.mac.chatapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mac.chatapp.R;
import com.mac.chatapp.model.Message;
import com.mac.chatapp.service.LoadImage;
import com.mac.chatapp.util.MessageType;

import java.text.DateFormat;

/**
 * Created by admin on 12/06/2016.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messageText;
    public TextView dateMessageText;
    public ImageView imageView;
    public Button imageButton;
    public String url;

    public MessageViewHolder(View itemView) {
        super(itemView);

        messageText = (TextView) itemView.findViewById(R.id.messageCard);
        dateMessageText = (TextView) itemView.findViewById(R.id.dateMessageCard);
        imageView = (ImageView) itemView.findViewById(R.id.imageMessageCard);
        imageButton = (Button) itemView.findViewById(R.id.imageButtonCard);
    }

    public void bindToPost(Message message) {
        dateMessageText.setText(DateFormat.getDateTimeInstance().format(message.date));
        imageView = null;
        if (message.typeMessage.equals(MessageType.TEXT.getValue())) {
            messageText.setText(message.message);
        } else if (message.typeMessage.equals(MessageType.IMAGE.getValue())) {
            messageText.setText("");
            url = message.message;
            imageButton.setVisibility(View.VISIBLE);
        }
    }

}
