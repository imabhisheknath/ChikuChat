package com.techbona.chikuview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import android.os.Bundle;

import com.techbona.chikuview.model.Message;
import com.techbona.chikuview.view.ChikuChat;
import com.techbona.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ChikuChat mChikuchat;

    private ArrayList<User> mUsers;
    Context mContext;
    private static final int READ_REQUEST_CODE = 100;



    //define chat message here attribute here.
    @VisibleForTesting
    protected static final int RIGHT_BUBBLE_COLOR = R.color.colorPrimaryDark;
    @VisibleForTesting
    protected static final int LEFT_BUBBLE_COLOR = R.color.gray300;
    @VisibleForTesting
    protected static final int BACKGROUND_COLOR = R.color.white;
    @VisibleForTesting
    protected static final int SEND_BUTTON_COLOR = R.color.blueGray500;
    @VisibleForTesting
    protected static final int SEND_ICON = R.drawable.ic_action_send;
    @VisibleForTesting
    protected static final int OPTION_BUTTON_COLOR = R.color.teal500;
    @VisibleForTesting
    protected static final int RIGHT_MESSAGE_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int LEFT_MESSAGE_TEXT_COLOR = Color.BLACK;
    @VisibleForTesting
    protected static final int USERNAME_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int SEND_TIME_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int DATA_SEPARATOR_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int MESSAGE_STATUS_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final String INPUT_TEXT_HINT = "New message..";
    @VisibleForTesting
    protected static final int MESSAGE_MARGIN = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mChikuchat = findViewById(R.id.chat_view);

        mContext = MainActivity.this;

        initUsers();



        //Set UI parameters if you need
        mChikuchat.setRightBubbleColor(ContextCompat.getColor(mContext, RIGHT_BUBBLE_COLOR));
        mChikuchat.setLeftBubbleColor(ContextCompat.getColor(mContext, LEFT_BUBBLE_COLOR));
        mChikuchat.setBackgroundColor(ContextCompat.getColor(mContext, BACKGROUND_COLOR));
        mChikuchat.setSendButtonColor(ContextCompat.getColor(mContext, SEND_BUTTON_COLOR));
        mChikuchat.setSendIcon(SEND_ICON);
        mChikuchat.setOptionIcon(R.drawable.ic_account_circle);
        mChikuchat.setOptionButtonColor(OPTION_BUTTON_COLOR);
        mChikuchat.setRightMessageTextColor(RIGHT_MESSAGE_TEXT_COLOR);
        mChikuchat.setLeftMessageTextColor(LEFT_MESSAGE_TEXT_COLOR);
        mChikuchat.setUsernameTextColor(USERNAME_TEXT_COLOR);
        mChikuchat.setSendTimeTextColor(SEND_TIME_TEXT_COLOR);
        mChikuchat.setDateSeparatorColor(DATA_SEPARATOR_COLOR);
        mChikuchat.setMessageStatusTextColor(MESSAGE_STATUS_TEXT_COLOR);
        mChikuchat.setInputTextHint(INPUT_TEXT_HINT);
        mChikuchat.setMessageMarginTop(MESSAGE_MARGIN);
        mChikuchat.setMessageMarginBottom(MESSAGE_MARGIN);
        mChikuchat.setMaxInputLine(5);
        mChikuchat.setUsernameFontSize(getResources().getDimension(R.dimen.font_small));
        mChikuchat.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mChikuchat.setInputTextColor(ContextCompat.getColor(mContext, R.color.black));
        mChikuchat.setInputTextSize(TypedValue.COMPLEX_UNIT_SP, 20);



        //sending text
        mChikuchat.setOnClickSendButtonListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Message message = new Message.Builder()
                        .setUser(mUsers.get(0))
                        .setRight(true)
                        .setText(mChikuchat.getInputText())
                        .hideIcon(true)
                        .setStatusIconFormatter(new MyMessageStatusFormatter(mContext))
                        .setStatusTextFormatter(new MyMessageStatusFormatter(mContext))
                        .setStatusStyle(Message.Companion.getSTATUS_ICON())
                        .setStatus(MyMessageStatusFormatter.STATUS_DELIVERED)
                        .build();
                //Set to chat view
                mChikuchat.send(message);
                //Add message list
                // mMessageList.add(message);
                //Reset edit text
                mChikuchat.setInputText("");

            }
        });




        //some extra click  actions
        mChikuchat.setOnBubbleClickListener(new Message.OnBubbleClickListener() {
            @Override
            public void onClick(Message message) {
                mChikuchat.updateMessageStatus(message, MyMessageStatusFormatter.STATUS_SEEN);
                Toast.makeText(
                        mContext,
                        "click : " + message.getUser().getName() + " - " + message.getText(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        mChikuchat.setOnIconClickListener(new Message.OnIconClickListener() {
            @Override
            public void onIconClick(Message message) {
                Toast.makeText(
                        mContext,
                        "click : icon " + message.getUser().getName(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        mChikuchat.setOnIconLongClickListener(new Message.OnIconLongClickListener() {
            @Override
            public void onIconLongClick(Message message) {
                Toast.makeText(
                        mContext,
                        "Removed mContext message \n" + message.getText(),
                        Toast.LENGTH_SHORT
                ).show();
                mChikuchat.getMessageView().remove(message);
            }
        });



        //Click option button
        mChikuchat.setOnClickOptionButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


    }



    //initiate user (send  and receiver)
    private void initUsers() {
        mUsers = new ArrayList<>();
        //User id
        int myId = 0;
        //User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_2);
        //User name
        String myName = "Michael";

        int yourId = 1;
        Bitmap yourIcon;
        yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);

        final User me = new User(myId, myName, myIcon);
        final User you = new User(1, "Abhishek", yourIcon);

        mUsers.add(me);
        mUsers.add(you);
    }




    private void showDialog() {
        final String[] items = {"PIC","CLEAR MSG"
        };
        new AlertDialog.Builder(mContext)
                .setTitle("Options")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        switch (position) {
                            case 0:
                                openGallery();
                                break;
                            case 1:
                                mChikuchat.getMessageView().removeAll();
                                break;
                        }
                    }
                })
                .show();
    }





    private void openGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != READ_REQUEST_CODE || resultCode != RESULT_OK || data == null) {
            return;
        }
        Uri uri = data.getData();
        try {
            Bitmap picture = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            Message message = new Message.Builder()
                    .setRight(true)
                    .setText(Message.Type.PICTURE.name())
                    .setUser(mUsers.get(0))
                    .hideIcon(true)
                    .setPicture(picture)
                    .setType(Message.Type.PICTURE)
                    .setStatusIconFormatter(new MyMessageStatusFormatter(mContext))
                    .setStatusStyle(Message.Companion.getSTATUS_ICON())
                    .setStatus(MyMessageStatusFormatter.STATUS_DELIVERED)
                    .build();
            mChikuchat.send(message);
            //receiveMessage(Message.Type.PICTURE.name());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }

    }
}
