package com.archi.intrisfeed.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.GroupChatMessageListAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.ImageFilePath;
import com.archi.intrisfeed.util.Util;
import com.archi.intrisfeed.util.Utility;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBGroupChatManagerListener;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBMessageSentListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import static android.content.Intent.ACTION_GET_CONTENT;
import static org.acra.ACRA.log;

/**
 * Created by archi_info on 11/26/2016.
 */

public class GroupChat extends AppCompatActivity implements View.OnClickListener {
    private int SELECT_FILE = 3, SELECT_VIDEO = 2, SELECT_IMAGE = 1;
    public String email, groupName, jID, dialog_id, user_id, occupants_id, opponantID;
    TextView userNameTv, emailTv, sendTv;
    private EditText messageEdt;
    private ImageView ivAttachment;

    public String dia;
    public ArrayList<String> arrayDialogId;
    public ArrayList<QBChatDialog> dialogs;
    public ListView lvChatDetails;

    public ArrayList<HashMap<String, String>> chatArraylist;
    public HashMap<String, String> hashmap;
    GroupChatMessageListAdapter chatListAdapter;
    QBGroupChatManagerListener groupChatManagerListener;
    QBGroupChatManager groupChatManager;
    QBIncomingMessagesManager incomingMessagesManager;
    QBMessageListener<QBGroupChat> groupChatMessageListener;
    QBRequestGetBuilder requestBuilder;
    QBSystemMessagesManager systemMessagesManager;
    QBSystemMessageListener systemMessageListener;

    // select attachment
    String userChoosenTask;
    File imageFile;
    Boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvChatDetails = (ListView) findViewById(R.id.lvChatDetails_GROUP);
        sendTv = (TextView) findViewById(R.id.activity_user_send_GROUP);
        messageEdt = (EditText) findViewById(R.id.activity_user_message_GROUP);
        ivAttachment = (ImageView) findViewById(R.id.ivAttachmentGroup);

        arrayDialogId = new ArrayList<>();
        QBSettings.getInstance().init(getApplicationContext(), Constant.APP_ID, Constant.AUTH_KEY, Constant.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Constant.ACCOUNT_KEY);
        // clear chat list
        chatArraylist = new ArrayList<HashMap<String, String>>();
        chatArraylist.clear();


        // get Data From Intent
        getDataFromIntent();
        // get Group Chatlist
        getGroupUserChatList();
        // get Message Listener
        incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();

        ivAttachment.setOnClickListener(this);


        QBIncomingMessagesManager incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessagesManager.addDialogMessageListener(
                new QBChatDialogMessageListener() {
                    @Override
                    public void processMessage(String dialogId, QBChatMessage message, Integer senderId) {
                        Log.e("@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@@@@@@");
                        Log.e("DID", "D " + dialogId);
                        Log.e("message", "message " + message);
                        Log.e("senderId", "senderId " + senderId);
                        if (!senderId.toString().equalsIgnoreCase(Util.ReadSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_QB_USERID))) {
                            String RecivedUrl = "";
                            String imageid = "";
                            Log.e("*****", "********************************");

                            Collection<QBAttachment> collection = message.getAttachments();
                            if (collection != null && collection.size() > 0) {
                                for (QBAttachment attachment : collection) {
                                    imageid = attachment.getId();
                                    RecivedUrl = attachment.getUrl();
                                    Log.e("URL", "U " + RecivedUrl);
                                }
                                //Here is the AsyncTask where I am trying to download image
                                //
                            }
                            // arrayAttached.add((QBAttachment) messages.get(i).getAttachments());
                            Log.e("MSG", "" + message.getBody());
                            hashmap = new HashMap<String, String>();
                            hashmap.put("id", "" + message.getId());
                            hashmap.put("msg", "" + message.getBody());
                            hashmap.put("recipient_id", "" + message.getRecipientId());
                            hashmap.put("sender_id", "" + message.getSenderId());
                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            hashmap.put("updated_at", "" + currentDateTimeString);
                            hashmap.put("imageid", imageid);
                            hashmap.put("url", RecivedUrl);
                            chatArraylist.add(hashmap);
                            chatListAdapter.notifyDataSetChanged();
                            scrollMyListViewToBottom();
                        }
                    }

                    @Override
                    public void processError(String dialogId, QBChatException exception, QBChatMessage message, Integer senderId) {
                        Log.e("@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@@@@@@");
                        Log.e("exception", "E " + exception);
                        Log.e("DID", "D " + dialogId);
                        Log.e("message", "message " + message);
                        Log.e("senderId", "senderId " + senderId);
                    }
                });


        groupChatMessageListener = new QBMessageListener<QBGroupChat>() {
            @Override
            public void processMessage(QBGroupChat qbGroupChat, QBChatMessage qbChatMessage) {
                Log.e("privateChat ", " " + qbGroupChat);
                Log.e("chatMessage", "" + qbChatMessage);
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void processError(QBGroupChat qbGroupChat, QBChatException e, QBChatMessage qbChatMessage) {
                Log.e("privateChat ", " " + qbGroupChat);
                Log.e("QBChatMessage", "" + qbChatMessage);
                Log.e("error", "" + e);
            }
        };

        groupChatManagerListener = new QBGroupChatManagerListener() {
            @Override
            public void chatCreated(QBGroupChat qbGroupChat) {
                qbGroupChat.addMessageListener(groupChatMessageListener);
            }
        };
    }


    // GET DATA FROM CONTENT
    private void getDataFromIntent() {
        if (getIntent().getExtras() != null) {
            groupName = getIntent().getExtras().getString("group_name");
            user_id = getIntent().getExtras().getString("user_id");
            jID = getIntent().getExtras().getString("jID");
            dialog_id = getIntent().getExtras().getString("dialog_id");
            occupants_id = getIntent().getExtras().getString("occupant_id");
            opponantID = getIntent().getExtras().getString("oponantID");

            Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_CHAT_ID, "" + user_id);

            userNameTv = (TextView) findViewById(R.id.activity_user_name_GROUP);
            emailTv = (TextView) findViewById(R.id.activity_user_email_GROUP);

            userNameTv.setText(groupName);
            sendTv.setOnClickListener(this);
        }
    }

    // GET USER CHAT LIST
    private void getGroupUserChatList() {
        QBChatDialog qbDialog = new QBChatDialog(dialog_id);

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        QBChatService.getDialogMessages(qbDialog, requestBuilder, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                Log.e("MESSAGES ", " " + messages);
                for (int i = 0; i < messages.size(); i++) {
                    log.e("MSGS ", "MSG " + messages.get(i).getBody());
                    String RecivedUrl = "";
                    String imageid = "";

                    Collection<QBAttachment> collection = messages.get(i).getAttachments();
                    if (collection != null && collection.size() > 0) {
                        for (QBAttachment attachment : collection) {
                            imageid = attachment.getId();
                            RecivedUrl = attachment.getUrl();
                            Log.e("******* ", "" + attachment.getUrl());
                        }
                        //Here is the AsyncTask where I am trying to download image
                        //
                    }
                    // arrayAttached.add((QBAttachment) messages.get(i).getAttachments());
                    hashmap = new HashMap<String, String>();
                    hashmap.put("id", "" + messages.get(i).getId());
                    hashmap.put("msg", "" + messages.get(i).getBody());
                    hashmap.put("recipient_id", "" + messages.get(i).getRecipientId());
                    hashmap.put("sender_id", "" + messages.get(i).getSenderId());
//                    Log.e("sender_id",""+ messages.get(i).getSenderId());
//                    Log.e("recipient_id",""+ messages.get(i).getRecipientId());
                    hashmap.put("updated_at", "" + messages.get(i).getProperties().get("updated_at"));
                    hashmap.put("imageid", imageid);
                    hashmap.put("url", RecivedUrl);
                    chatArraylist.add(hashmap);
//                    Log.e("getMESSAGE", "" + messages.get(i).getBody());
//                    Log.e("RecivedUrl ", ">> " + RecivedUrl);
                }

                chatListAdapter = new GroupChatMessageListAdapter(getApplicationContext(), chatArraylist);
                lvChatDetails.setAdapter(chatListAdapter);
                chatListAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("ERRORS", "ER " + errors);
            }
        });
    }

    // SCROLL TO BOTTOM WHEN SCROLL DOWN
    private void scrollMyListViewToBottom() {
        lvChatDetails.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lvChatDetails.setSelection(chatArraylist.size() - 1);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_user_send_GROUP:
                // send Message in Group
                sendMessageInGroup();
                break;

            case R.id.ivAttachmentGroup:
                // select Attachment
                selectAttachment();
                break;
        }

    }

    private void sendMessageInGroup() {
        if (messageEdt.length() > 0) {
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);
            groupChatManager = QBChatService.getInstance().getGroupChatManager();
            final QBGroupChat currentChatRoom = groupChatManager.createGroupChat(jID);
            currentChatRoom.join(history, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    currentChatRoom.addMessageListener(groupChatMessageListener);
                    QBChatService.getInstance().getGroupChatManager().addGroupChatManagerListener(groupChatManagerListener);
                    try {
                        QBChatMessage chatMessage = new QBChatMessage();
                        if (!messageEdt.getText().toString().equalsIgnoreCase("")) {
                            chatMessage.setBody(messageEdt.getText().toString());
                        } else {
                            chatMessage.setBody("");
                        }
                        chatMessage.setProperty("save_to_history", "1"); // Save a message to history
                        chatMessage.setSenderId(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_QB_USERID)));
                        chatMessage.setRecipientId(Integer.parseInt(opponantID));
                        chatMessage.setDialogId(dialog_id);
                        chatMessage.setMarkable(true);


                        groupChatManager = QBChatService.getInstance().getGroupChatManager();
                        QBGroupChat groupChat = groupChatManager.getGroupChat(jID);
                        if (groupChat == null) {
                            groupChat = groupChatManager.createGroupChat(jID);
                        }
                        // send message
                        groupChat.sendMessage(chatMessage);
                        groupChat.addMessageSentListener(groupChatMessageSentListner);
//                        groupChat.addMessageListener(groupChatMessageListener);
                    } catch (SmackException.NotConnectedException e) {
                        Toast.makeText(GroupChat.this, "Exception " + e, Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onError(QBResponseException errors) {
                    Toast.makeText(getApplicationContext(), "ERROR" + errors, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private QBMessageSentListener<QBGroupChat> groupChatMessageSentListner = new QBMessageSentListener<QBGroupChat>() {
        @Override
        public void processMessageSent(QBGroupChat qbGroupChat, QBChatMessage qbChatMessage) {
            Log.d("MEHUL", "M " + qbGroupChat);
            Log.d("MSG", "MSG " + qbChatMessage);
            hashmap = new HashMap<String, String>();
            hashmap.put("id", "" + qbChatMessage.getId());
            if (qbChatMessage.getBody() != null) {
                hashmap.put("msg", "" + qbChatMessage.getBody());
            } else {
                hashmap.put("msg", "");
            }
            hashmap.put("recipient_id", "" + occupants_id);
            hashmap.put("sender_id", "" + user_id);

            Collection<QBAttachment> collection = qbChatMessage.getAttachments();
            if (collection != null && collection.size() > 0) {
                for (QBAttachment attachment : collection) {
                    String imageid = attachment.getId();
                    String RecivedUrl = attachment.getUrl();
                    hashmap.put("url", "" + RecivedUrl);
                }
                //Here is the AsyncTask where I am trying to download image
                //
            }

            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            // textView is the TextView view that should display it
            hashmap.put("updated_at", "" + currentDateTimeString);
            chatArraylist.add(hashmap);

            Toast.makeText(getApplicationContext(), "Message Sent Successfully", Toast.LENGTH_SHORT).show();
            messageEdt.setText("");

            chatListAdapter = new GroupChatMessageListAdapter(getApplicationContext(), chatArraylist);
            lvChatDetails.setAdapter(chatListAdapter);
            lvChatDetails.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            lvChatDetails.setStackFromBottom(true);
            chatListAdapter.notifyDataSetChanged();
            scrollMyListViewToBottom();
        }

        @Override
        public void processMessageFailed(QBGroupChat qbGroupChat, QBChatMessage qbChatMessage) {
            Log.d("MEHUL", "M " + qbGroupChat);
            Log.d("MSG", "ERR " + qbChatMessage);
        }
    };


    private void getListOfIncommingMessageInGroup() {
        Log.e("MSG ", "MSG SENT IN GROUP SUCCESSFULLY");
        incomingMessagesManager.addDialogMessageListener(
                new QBChatDialogMessageListener() {
                    @Override
                    public void processMessage(String dialogId, QBChatMessage message, Integer senderId) {
                        Log.e("Dialog_id", "dialogId  " + dialogId);
                        Log.e("message", "message  " + message);
                        Log.e("senderId", "senderId  " + senderId);
                        hashmap = new HashMap<String, String>();
                        hashmap.put("id", "" + message.getId());
                        if (message.getBody() != null) {
                            hashmap.put("msg", "" + message.getBody());
                        } else {
                            hashmap.put("msg", "");
                        }
                        hashmap.put("recipient_id", "");
                        hashmap.put("sender_id", "" + senderId);

                        Collection<QBAttachment> collection = message.getAttachments();
                        if (collection != null && collection.size() > 0) {
                            for (QBAttachment attachment : collection) {
                                String imageid = attachment.getId();
                                String RecivedUrl = attachment.getUrl();
                                hashmap.put("url", "" + RecivedUrl);
                            }
                            //Here is the AsyncTask where I am trying to download image
                            //
                        }

                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                        // textView is the TextView view that should display it
                        hashmap.put("updated_at", "" + currentDateTimeString);
                        chatArraylist.add(hashmap);

                        Toast.makeText(getApplicationContext(), "Message Sent Successfully", Toast.LENGTH_SHORT).show();

                        messageEdt.setText("");
                        chatListAdapter = new GroupChatMessageListAdapter(getApplicationContext(), chatArraylist);
                        lvChatDetails.setAdapter(chatListAdapter);
                        lvChatDetails.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        lvChatDetails.setStackFromBottom(true);
                        chatListAdapter.notifyDataSetChanged();
                        scrollMyListViewToBottom();

                    }

                    @Override
                    public void processError(String dialogId, QBChatException exception, QBChatMessage message, Integer senderId) {

                    }
                });

    }


    // SELECT ATTACHMENT
    private void selectAttachment() {
        final CharSequence[] items = {"Choose from Gallery",
                "Choose Document", "Choose video", "cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupChat.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(GroupChat.this);
                if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    galleryIntent();
                } else if (items[item].equals("Choose Document")) {
                    userChoosenTask = "Choose Document";
                    selectDocument();
//                    Toast.makeText(PrivateChat.this, "this is the document section", Toast.LENGTH_SHORT).show();
                } else if (items[item].equals("Choose video")) {
                    userChoosenTask = "Choose video";
                    chooseVideos();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void galleryIntent() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_IMAGE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        }

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    private void chooseVideos() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            startActivityForResult(intent, SELECT_VIDEO);
        } else {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), SELECT_VIDEO);
        }
    }

    private void selectDocument() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, SELECT_FILE);
        } else {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select Document"), SELECT_FILE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    sendPhotoInPrivateChat(data);
                } else {
                    Toast.makeText(GroupChat.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    sendVideoInPrivateChat(data);
                } else {
                    Toast.makeText(GroupChat.this, "No Video Selected", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                if (resultCode == RESULT_OK) {
                    sendDocumentResultInPrivateChat(data);
                } else {
                    Toast.makeText(GroupChat.this, "No Document Selected", Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }

    private void sendDocumentResultInPrivateChat(Intent data) {
        Uri data1 = data.getData();
        String pdfPath = data1.getPath();
        imageFile = new File(pdfPath);
        if (imageFile != null) {
            QBContent.uploadFileTask(imageFile, true, "").performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(final QBFile qbFile, Bundle bundle) {
                    DiscussionHistory history = new DiscussionHistory();
                    history.setMaxStanzas(0);
                    groupChatManager = QBChatService.getInstance().getGroupChatManager();
                    final QBGroupChat currentChatRoom = groupChatManager.createGroupChat(jID);
                    currentChatRoom.join(history, new QBEntityCallback() {
                        @Override
                        public void onSuccess(Object o, Bundle bundle) {
                            currentChatRoom.addMessageListener(groupChatMessageListener);
                            QBChatMessage chatMessage = new QBChatMessage();
                            if (!messageEdt.getText().toString().equalsIgnoreCase("")) {
                                chatMessage.setBody(messageEdt.getText().toString());
                            } else {
                                chatMessage.setBody("");
                            }
                            chatMessage.setProperty("save_to_history", "1"); // Save a message to history
                            chatMessage.setSenderId(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_QB_USERID)));
                            chatMessage.setRecipientId(Integer.parseInt(opponantID));
                            chatMessage.setDialogId(dialog_id);
                            QBAttachment attachment = new QBAttachment("file");
                            attachment.setId(qbFile.getId().toString());
                            attachment.setUrl(qbFile.getPublicUrl().toString());
                            chatMessage.addAttachment(attachment);
                            chatMessage.setMarkable(true);
                            groupChatManager = QBChatService.getInstance().getGroupChatManager();
                            QBGroupChat groupChat = groupChatManager.getGroupChat(jID);
                            if (groupChat == null) {
                                groupChat = groupChatManager.createGroupChat(jID);
                            }
                            // send message
                            try {
                                groupChat.sendMessage(chatMessage);
                                groupChat.addMessageListener(groupChatMessageListener);

                                Toast.makeText(GroupChat.this, "file send sucssefully", Toast.LENGTH_SHORT).show();
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(GroupChat.this, "exception :" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                    getListOfIncommingMessageInGroup();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
//            getListOfIncommingMessageInGroup();
        } else {
            Toast.makeText(GroupChat.this, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    // SEND VIDEO IN CHAT
    private void sendVideoInPrivateChat(Intent data) {
        Uri selectedVideoUri = data.getData();
        String videoPath = null;
        Log.e("Video LINK", ">> " + selectedVideoUri.getPath());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // Do something for 19 and above versions
            // OI FILE Manager
            // OI FILE Manager
            //MEDIA GALLERY
            String selectedImagePath;
            Uri selectedImageUri = data.getData();
            selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
            Log.i("Video File Path", "" + selectedImagePath);

            String wholeID = DocumentsContract.getDocumentId(selectedVideoUri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Video.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Video.Media._ID + "=?";

            Cursor cursor = getContentResolver().
                    query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                videoPath = cursor.getString(columnIndex);
            }
            cursor.close();
            Log.e("Video Path", "Video " + videoPath);
            imageFile = new File(videoPath);

        } else {
            videoPath = getPath(selectedVideoUri);
            Log.e("Video Path", ">> " + videoPath);
            imageFile = new File(videoPath);
        }
        sendVideo();
    }

    // SEND VIDEO IN CHAT
    private void sendVideo() {
        if (imageFile != null) {
            QBContent.uploadFileTask(imageFile, true, "").performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(final QBFile qbFile, Bundle bundle) {
                    DiscussionHistory history = new DiscussionHistory();
                    history.setMaxStanzas(0);
                    groupChatManager = QBChatService.getInstance().getGroupChatManager();
                    final QBGroupChat currentChatRoom = groupChatManager.createGroupChat(jID);
                    currentChatRoom.join(history, new QBEntityCallback() {
                        @Override
                        public void onSuccess(Object o, Bundle bundle) {
                            currentChatRoom.addMessageListener(groupChatMessageListener);
                            QBChatMessage chatMessage = new QBChatMessage();
                            if (!messageEdt.getText().toString().equalsIgnoreCase("")) {
                                chatMessage.setBody(messageEdt.getText().toString());
                            } else {
                                chatMessage.setBody("");
                            }
                            chatMessage.setProperty("save_to_history", "1"); // Save a message to history
                            chatMessage.setSenderId(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_QB_USERID)));
                            chatMessage.setRecipientId(Integer.parseInt(opponantID));
                            chatMessage.setDialogId(dialog_id);
                            QBAttachment attachment = new QBAttachment("video");
                            attachment.setId(qbFile.getId().toString());
                            attachment.setUrl(qbFile.getPublicUrl().toString());
                            chatMessage.addAttachment(attachment);
                            chatMessage.setMarkable(true);
                            groupChatManager = QBChatService.getInstance().getGroupChatManager();
                            QBGroupChat groupChat = groupChatManager.getGroupChat(jID);
                            if (groupChat == null) {
                                groupChat = groupChatManager.createGroupChat(jID);
                            }
                            // send message
                            try {
                                groupChat.sendMessage(chatMessage);

                                Toast.makeText(GroupChat.this, "video send sucssefully", Toast.LENGTH_SHORT).show();
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(GroupChat.this, "exception :" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
//            getListOfIncommingMessageInGroup();
        } else {
            Toast.makeText(GroupChat.this, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    // SEND PHOTO IN CHAT
    private void sendPhotoInPrivateChat(Intent data) {
        Uri selectedImageUri = data.getData();
        String imagePath = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // Do something for 19 and above versions
            // OI FILE Manager
            String wholeID = DocumentsContract.getDocumentId(selectedImageUri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                imagePath = cursor.getString(columnIndex);
                Log.e("PATH", "Image Path : " + imagePath);
            }
            cursor.close();
            imageFile = new File(imagePath);
        } else {
            imagePath = getPath(selectedImageUri);
            Log.e("File Path", ">> " + imagePath);
            imageFile = new File(imagePath);
        }
        sendImageToGroupChat();
    }


    private void sendImageToGroupChat() {
        if (imageFile != null) {
            QBContent.uploadFileTask(imageFile, true, "").performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(final QBFile qbFile, Bundle bundle) {
                    DiscussionHistory history = new DiscussionHistory();
                    history.setMaxStanzas(0);
                    groupChatManager = QBChatService.getInstance().getGroupChatManager();
                    final QBGroupChat currentChatRoom = groupChatManager.createGroupChat(jID);
                    currentChatRoom.join(history, new QBEntityCallback() {
                        @Override
                        public void onSuccess(Object o, Bundle bundle) {
                            currentChatRoom.addMessageListener(groupChatMessageListener);
                            QBChatMessage chatMessage = new QBChatMessage();
                            if (!messageEdt.getText().toString().equalsIgnoreCase("")) {
                                chatMessage.setBody(messageEdt.getText().toString());
                            } else {
                                chatMessage.setBody("");
                            }
                            chatMessage.setProperty("save_to_history", "1"); // Save a message to history
                            chatMessage.setSenderId(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_QB_USERID)));
                            chatMessage.setRecipientId(Integer.parseInt(opponantID));
                            chatMessage.setDialogId(dialog_id);
                            QBAttachment attachment = new QBAttachment("image");
                            attachment.setId(qbFile.getId().toString());
                            attachment.setUrl(qbFile.getPublicUrl().toString());
                            chatMessage.addAttachment(attachment);
                            chatMessage.setMarkable(true);
                            groupChatManager = QBChatService.getInstance().getGroupChatManager();
                            QBGroupChat groupChat = groupChatManager.getGroupChat(jID);
                            if (groupChat == null) {
                                groupChat = groupChatManager.createGroupChat(jID);
                            }
                            // send message
                            try {
                                groupChat.sendMessage(chatMessage);

                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {
                        }
                    });


                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("Error Exception", "" + e);
                    Toast.makeText(GroupChat.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
//            getListOfIncommingMessageInGroup();
        } else {
            Toast.makeText(getApplicationContext(), "Image not Found, Please again select image to send", Toast.LENGTH_SHORT).show();
        }
    }


    private String getPath(Uri selectedImageUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImageUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(proj[0]);
            res = cursor.getString(columnIndex);
        }
        cursor.close();
        return res;
    }

}
