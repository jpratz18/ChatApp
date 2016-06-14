package com.mac.chatapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mac.chatapp.R;
import com.mac.chatapp.model.User;

/**
 * Created by admin on 12/06/2016.
 */
public class UserViewHolder extends RecyclerView.ViewHolder {

    public TextView name;

    public UserViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.userNameCard);
    }

    public void bindToPost(User user) {
        name.setText(user.username);
    }

}
