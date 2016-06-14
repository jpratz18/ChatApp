package com.mac.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by admin on 12/06/2016.
 */
public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    protected FirebaseAuth mAuth;
    protected DatabaseReference mDatabase;

    public BaseActivity () {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void showProgressDialog () {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading....");
        }
        progressDialog.show();
    }

    public void hideProgressDialog () {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public String getUserId () {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void signOut () {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
    }

}
