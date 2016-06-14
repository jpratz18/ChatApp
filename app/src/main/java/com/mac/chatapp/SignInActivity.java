package com.mac.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.mac.chatapp.model.User;
import com.mac.chatapp.util.Constants;
import com.mac.chatapp.util.Validation;

public class SignInActivity extends BaseActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        signInButton = (Button) findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation.validateNetworkState(getSystemService(getBaseContext().CONNECTIVITY_SERVICE))) {
                    signIn();
                } else {
                    Toast.makeText(getBaseContext(), "There isn't an available network", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(getBaseContext(), "Sign In Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(emailEditText.getText().toString())) {
            emailEditText.setError("Required");
            result = false;
        } else {
            emailEditText.setError(null);
        }

        if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
            passwordEditText.setError("Required");
            result = false;
        } else {
            passwordEditText.setError(null);
        }

        return result;
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());
        writeNewUser(user.getUid(), username);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void writeNewUser(String userId, String name) {
        User user = new User(name);
        mDatabase.child(Constants.TABLE_USERS).child(userId).setValue(user);
    }

}
