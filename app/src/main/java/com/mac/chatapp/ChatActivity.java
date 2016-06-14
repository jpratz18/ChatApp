package com.mac.chatapp;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mac.chatapp.model.Message;
import com.mac.chatapp.service.LoadImage;
import com.mac.chatapp.util.Constants;
import com.mac.chatapp.util.MessageType;
import com.mac.chatapp.viewholder.MessageViewHolder;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends BaseActivity
        implements EasyPermissions.PermissionCallbacks {

    private String toUser;
    private RecyclerView messageRecycler;
    private EditText messageText;
    private TextView nameUser;

    private Uri mFileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        toUser = intent.getStringExtra(Constants.EXTRA_TO_USER_KEY);
        String name = intent.getStringExtra(Constants.EXTRA_USER_NAME_KEY);

        if (toUser == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.addNewMessage);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMessage();
            }
        });


        FloatingActionButton image = (FloatingActionButton) findViewById(R.id.addNewImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewImage();
            }
        });

        nameUser = (TextView) findViewById(R.id.nameUserChat);
        messageText = (EditText) findViewById(R.id.textMessage);

        nameUser.setText(name);

        messageRecycler = (RecyclerView) findViewById(R.id.messageRecycler);
        messageRecycler.setLayoutManager(new LinearLayoutManager(this));
        messageRecycler.setHasFixedSize(true);
        messageRecycler.setItemAnimator(new DefaultItemAnimator());
        messageRecycler.setAdapter(createAdapterRecycler());
    }

    private void addNewMessage () {
        saveMessage(new Message(toUser, getUserId(), new Date(),
                messageText.getText().toString(), MessageType.TEXT.getValue()));
        messageText.setText("");
    }

    private void saveMessage (Message message) {
        DatabaseReference db = mDatabase.child(Constants.TABLE_MESSAGES);
        db.push().setValue(message);
    }

    private FirebaseRecyclerAdapter createAdapterRecycler () {
        mDatabase.child(Constants.TABLE_MESSAGES)
                .limitToLast(100);
        return new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
            Message.class,
            R.layout.message_card,
            MessageViewHolder.class,
            mDatabase.child(Constants.TABLE_MESSAGES)
        ){
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, final Message model, final int position) {
                if (model.from.equals(getUserId())) {
                    viewHolder.messageText.setGravity(Gravity.RIGHT);
                    viewHolder.dateMessageText.setGravity(Gravity.RIGHT);
                } else {
                    viewHolder.messageText.setGravity(Gravity.LEFT);
                    viewHolder.dateMessageText.setGravity(Gravity.LEFT);
                }
                viewHolder.bindToPost(model);
                viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadImage loadImage = new LoadImage();
                        loadImage.activity = ChatActivity.this;
                        loadImage.image = viewHolder.imageView;
                        showProgressDialog();
                        loadImage.execute(viewHolder.url);
                    }
                });
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @AfterPermissionGranted(Constants.RC_STORAGE_PERMS)
    private void addNewImage () {
        String perm = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !EasyPermissions.hasPermissions(this, perm)) {
            EasyPermissions.requestPermissions(this, "Reads images from your camera.",
                    Constants.RC_STORAGE_PERMS, perm);
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpg");
        mFileUri = Uri.fromFile(file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(takePictureIntent, Constants.RC_TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                }
            }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        StorageReference phoneRef = FirebaseStorage.getInstance().getReference()
                .child("photos").child(fileUri.getLastPathSegment());
        showProgressDialog();
        phoneRef.putFile(fileUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                saveMessage(new Message(toUser, getUserId(), new Date(),
                        mDownloadUrl.toString(), MessageType.IMAGE.getValue()));
                hideProgressDialog();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(getBaseContext(), "It was a problems sending the image.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
