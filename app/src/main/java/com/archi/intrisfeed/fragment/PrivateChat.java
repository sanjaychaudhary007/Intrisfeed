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
import com.archi.intrisfeed.adapter.PrivateChatMsgListAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.ImageFilePath;
import com.archi.intrisfeed.util.Util;
import com.archi.intrisfeed.util.Utility;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBMessageSentListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import static android.content.Intent.ACTION_GET_CONTENT;
import static com.archi.intrisfeed.fragment.ChattingFragment.chatService;

/**
 * Created by archi on 9/29/2016.
 */

public class PrivateChat extends AppCompatActivity implements View.OnClickListener {

    private int SELECT_FILE = 3, SELECT_VIDEO = 2, SELECT_IMAGE = 1;
    public String email, fullname, dialog_id;
    int opponentId, user_id;
    TextView userNameTv, emailTv, sendTv;
    EditText messageEdt;
    QBPrivateChatManagerListener privateChatManagerListener;
    QBMessageListener<QBPrivateChat> privateChatMessageListener;
    public String dia;
    public ArrayList<String> arrayDialogId;
    public ArrayList<QBChatDialog> dialogs;
    public ListView lvChatDetails;
    public ArrayList<HashMap<String, String>> chatArraylist;
    public HashMap<String, String> hashmap;
    PrivateChatMsgListAdapter chatListAdapter;
    ImageView ivAttachment;
    QBIncomingMessagesManager incomingMessagesManager;
    QBPrivateChatManager privateChatManager;
    QBRequestGetBuilder requestBuilder;
    String userChoosenTask;
    private File imageFile;
    String documentPath;
    Uri documentuploadUri = null;
    String uNameStr, uPwdStr;
    QBUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ivAttachment = (ImageView) findViewById(R.id.ivAttachment);

        lvChatDetails = (ListView) findViewById(R.id.lvChatDetails_TestChat);
        sendTv = (TextView) findViewById(R.id.activity_user_send);
        messageEdt = (EditText) findViewById(R.id.activity_user_message);
        String uid = Util.ReadSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_QB_USERID);
        Log.e("UID ","%%%%%% "+uid);
        user_id = Integer.parseInt(uid);
        Log.e("IDDDDD",">>>> "+user_id);
        arrayDialogId = new ArrayList<>();
        QBSettings.getInstance().init(getApplicationContext(), Constant.APP_ID, Constant.AUTH_KEY, Constant.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Constant.ACCOUNT_KEY);
        // get INTENT VALUES
        getDataFromIntent();


        sendTv.setOnClickListener(this);
        ivAttachment.setOnClickListener(this);

        uNameStr = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_EMAIL);
        uPwdStr = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_PASSWORD);
        Log.e("UNAME ", "NM " + uNameStr);
        Log.e("PASSWORD ", "PWD " + uPwdStr);
        user = new QBUser();
        user.setEmail(uNameStr);
        user.setPassword(uPwdStr);
        // LOG IN CHAT SERVICE
        if (!ChattingFragment.chatService.isLoggedIn()) {
            ChattingFragment.chatService.login(user, new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(QBUser qbUser, Bundle args) {
                    Log.e("$$$$$$$$$$$$", "loged chat");
//                    Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(QBResponseException errors) {
                    Log.e("$$$$$$$$$$$", "not loged\n" + errors.getMessage());
//                    Toast.makeText(getApplicationContext(), "Fail " + errors, Toast.LENGTH_SHORT).show();
                    //error

                }
            });
        }


        final QBPrivateChatManager chatMessage = ChattingFragment.chatService.getPrivateChatManager();
//        QBPrivateChat chat = chatMessage.getChat(12);
//        boolean isLoggedIn = chatService.isLoggedIn();
        QBUser user = ChattingFragment.chatService.getUser();
        String data = user.getEmail();
        final QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        if (dialog_id == null) {
            ChattingFragment.chatService.getChatDialogs(QBDialogType.PRIVATE, requestBuilder, new QBEntityCallback<ArrayList<QBChatDialog>>() {
                @Override
                public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle args) {
                    int totalEntries = args.getInt("total_entries");
                Log.d("*********","*********************************");
                Log.d("MainData",dialogs.get(0).getLastMessage());
                dialogs.get(0).getDialogId();
                dia = dialogs.get(0).getDialogId();
                Log.d("Dialog_Data", dia);
                    for (int i = 0; i < dialogs.size(); i++) {
//                    Log.e("COMPARE", " " + id + " WITH " + dialogs.get(i).getOccupants().get(0));

                        for (int j = 0; j < dialogs.get(i).getOccupants().size(); j++) {
                            Log.d("*********","%%%%%%%%%%%%%%%%%%%%%%");
                            Log.d("UID",""+Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_QB_USERID)));
                            Log.d("OCCupantID",""+dialogs.get(i).getOccupants().get(j));
                            if (Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(),Constant.SHRED_PR.KEY_QB_USERID)) == dialogs.get(i).getOccupants().get(j)) {
                                Toast.makeText(PrivateChat.this, "GetCHat", Toast.LENGTH_SHORT).show();
                                dia = dialogs.get(i).getDialogId();
                                QBChatDialog qbDialog = new QBChatDialog(dia);
                                //        QBDialog qbDialog = new QBDialog(dia);
                                requestBuilder.setLimit(100);
                                QBChatService.getDialogMessages(qbDialog, requestBuilder, new QBEntityCallback<ArrayList<QBChatMessage>>() {
                                    @Override
                                    public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {

                                        for (int i = 0; i < messages.size(); i++) {
                                            hashmap = new HashMap<String, String>();
                                            hashmap.put("id", "" + messages.get(i).getId());
                                            hashmap.put("msg", "" + messages.get(i).getBody());
                                            hashmap.put("recipient_id", "" + messages.get(i).getRecipientId());
                                            hashmap.put("sender_id", "" + messages.get(i).getSenderId());
                                            hashmap.put("updated_at", "" + messages.get(i).getProperties().get("updated_at"));

                                            Log.d("id", "" + messages.get(i).getId());
                                            Log.d("recipient_id", "" + messages.get(i).getRecipientId());
                                            Log.d("sender_id", "" + messages.get(i).getSenderId());
                                            Log.d("getMESSAGE", "" + messages.get(i).getBody());
                                            Log.d("updated_at ", ">> " + messages.get(i).getProperties().get("updated_at"));
                                            Log.e("MSG ", ">> " + messages);
                                            chatArraylist.add(hashmap);
                                        }

                                        chatListAdapter = new PrivateChatMsgListAdapter(getApplicationContext(), chatArraylist);
                                        lvChatDetails.setAdapter(chatListAdapter);
//                                      chatListAdapter.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onError(QBResponseException errors) {
                                        Log.e("ERROR ", ">> " + errors);
                                    }
                                });
                                arrayDialogId.add(dialogs.get(i).getDialogId());
                            } else {
                                Log.e("DID", "ELSE>>> " + dialogs.get(i).getDialogId());
                            }
                        }
                    }
                }

                @Override
                public void onError(QBResponseException errors) {

                }
            });
        } else {
            // clear chat list
            getUserChatList();
        }


//        createSession(user);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_user_send:
                // semd message to user
                sendMessage();
                break;

            case R.id.ivAttachment:
                // select Attachment
                selectAttachment();
                break;
        }

    }

    private void getDataFromIntent() {
        if (getIntent().getExtras() != null) {
            fullname = getIntent().getExtras().getString("fullname");
            if (getIntent().getExtras().getString("dialog_id") != null) {
                dialog_id = getIntent().getExtras().getString("dialog_id");
            }
            opponentId = getIntent().getExtras().getInt("oponantID");
            Log.e("OPPONANT ID ", ">> " + opponentId);
            user_id = getIntent().getExtras().getInt("user_id");
            Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_CHAT_ID, "" + user_id);
            userNameTv = (TextView) findViewById(R.id.activity_user_name);
            emailTv = (TextView) findViewById(R.id.activity_user_email);
            userNameTv.setText(fullname);
//            if (dialog_id.equalsIgnoreCase("")) {
//                createPrivateChatDialog();
//            }

        }
    }

    private void getUserChatList() {
        chatArraylist = new ArrayList<HashMap<String, String>>();
        chatArraylist.clear();

        // GET CHAT MESSAGES FROM THE LIST
        QBChatDialog qbDialog = new QBChatDialog(dialog_id);
        Log.e("DIALOG_ID", ">>>> " + dialog_id);

        requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);
        if (dialog_id != null) {
            ChattingFragment.chatService.getDialogMessages(qbDialog, requestBuilder, new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                    Log.e("MEHUL", "MSG " + messages);
                    Log.e("ARGS ", "" + args);
                    for (int i = 0; i < messages.size(); i++) {

                        String RecivedUrl = "";
                        String imageid = "";
                        Log.e("*****", "********************************");

                        Collection<QBAttachment> collection = messages.get(i).getAttachments();
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
                        Log.e("MSG", "" + messages.get(i).getBody());
                        hashmap = new HashMap<String, String>();
                        hashmap.put("id", "" + messages.get(i).getId());
                        hashmap.put("msg", "" + messages.get(i).getBody());
                        hashmap.put("recipient_id", "" + messages.get(i).getRecipientId());
                        hashmap.put("sender_id", "" + messages.get(i).getSenderId());
                        hashmap.put("updated_at", "" + messages.get(i).getProperties().get("updated_at"));
                        hashmap.put("imageid", imageid);
                        hashmap.put("url", RecivedUrl);
                        chatArraylist.add(hashmap);
                    }

                    chatListAdapter = new PrivateChatMsgListAdapter(getApplicationContext(), chatArraylist);
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
    }


    private void scrollMyListViewToBottom() {
        lvChatDetails.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lvChatDetails.setSelection(chatArraylist.size() - 1);
            }
        });
    }

    // SEND MESSAGE IN CHAT
    private void sendMessage() {


//        privateChatManager = chatService.getPrivateChatManager();
        Log.d("PRIVATE", ">>>>> " + privateChatManager);
        Log.d("INST", ">>>>> " + QBChatService.getInstance());
        Log.d("CHAT ", "" + chatService);
        Log.d("PRI", ">>>>> " + QBChatService.getInstance().getPrivateChatManager());

        //login to chat firstly


//            privateChatManager.createDialog(opponentId, new QBEntityCallback<QBChatDialog>() {
//                @Override
//                public void onSuccess(QBChatDialog dialog, Bundle args) {
//                    Log.e("DIALOG "," "+dialog);
//                    Log.e("DIALOG "," "+args);
//                }
//
//                @Override
//                public void onError(QBResponseException errors) {
//                    Log.e("ERROR "," "+errors);
//                }
//            });

        if (messageEdt.length() > 0) {
            privateChatMessageListener = new QBMessageListener<QBPrivateChat>() {
                @Override
                public void processMessage(QBPrivateChat privateChat, final QBChatMessage chatMessage) {
                    Log.e("privateChat ", " " + privateChat);
                    Log.e("chatMessage", "" + chatMessage);
                    chatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void processError(QBPrivateChat privateChat, QBChatException error, QBChatMessage originMessage) {
                    Log.e("privateChat ", " " + privateChat);
                    Log.e("QBChatMessage", "" + originMessage);
                    Log.e("error", "" + error);
                }
            };

            privateChatManagerListener = new QBPrivateChatManagerListener() {
                @Override
                public void chatCreated(final QBPrivateChat privateChat, final boolean createdLocally) {
                    if (!createdLocally) {
                        privateChat.addMessageListener(privateChatMessageListener);
                    }
                }
            };

//            privateChatManager.addPrivateChatManagerListener(privateChatManagerListener);
//                ChattingFragment.chatService.getPrivateChatManager().addPrivateChatManagerListener(privateChatManagerListener);
            try {
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setBody(messageEdt.getText().toString());
                chatMessage.setProperty("save_to_history", "1"); // Save a message to history
                chatMessage.setProperty("notification_type", "1");
                chatMessage.setSenderId(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_QB_USERID)));
                chatMessage.setRecipientId(opponentId);
//                chatMessage.setDialogId(dialog_id);
                chatMessage.setMarkable(true);
                privateChatManager = QBChatService.getInstance().getPrivateChatManager();
//                privateChatManager = ChattingFragment.chatService.getPrivateChatManager();
                QBPrivateChat privateChat = privateChatManager.getChat(opponentId);
//                    Log.e("oponantID", "" + oponantID);
                if (privateChat == null) {
                    privateChat = privateChatManager.createChat(opponentId, privateChatMessageListener);
                }
                // send message
                privateChat.sendMessage(chatMessage);
                privateChat.addMessageSentListener(privateChatMessageSentListener);
                privateChat.addMessageListener(privateChatMessageListener);


            } catch (SmackException.NotConnectedException e) {
                Toast.makeText(PrivateChat.this, "Exception " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private QBMessageSentListener<QBPrivateChat> privateChatMessageSentListener = new QBMessageSentListener<QBPrivateChat>() {
        @Override
        public void processMessageSent(QBPrivateChat qbChat, QBChatMessage qbChatMessage) {
            Log.d("MEHUL", "M " + qbChat);
            Log.d("MSG", "MSG " + qbChatMessage);
            hashmap = new HashMap<String, String>();
            hashmap.put("id", "" + qbChatMessage.getId());
            if (qbChatMessage.getBody() != null) {
                hashmap.put("msg", "" + qbChatMessage.getBody());
            } else {
                hashmap.put("msg", "");
            }
            hashmap.put("recipient_id", "" + opponentId);
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

            chatListAdapter = new PrivateChatMsgListAdapter(getApplicationContext(), chatArraylist);
            lvChatDetails.setAdapter(chatListAdapter);
            lvChatDetails.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            lvChatDetails.setStackFromBottom(true);
            chatListAdapter.notifyDataSetChanged();
            scrollMyListViewToBottom();
        }

        @Override
        public void processMessageFailed(QBPrivateChat qbChat, QBChatMessage qbChatMessage) {
            Log.d("MEHUL", "M " + qbChat);
            Log.d("MSG", "ERR " + qbChatMessage);
        }
    };


    // SELECT ATTACHMENT
    private void selectAttachment() {
        final CharSequence[] items = {"Choose from Gallery",
                "Choose Document", "Choose video", "cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PrivateChat.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(PrivateChat.this);
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
                    Toast.makeText(PrivateChat.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    sendVideoInPrivateChat(data);
                } else {
                    Toast.makeText(PrivateChat.this, "No Video Selected", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                if (resultCode == RESULT_OK) {
                    sendDocumentResultInPrivateChat(data);
                } else {
                    Toast.makeText(PrivateChat.this, "No Document Selected", Toast.LENGTH_SHORT).show();
                }
                break;


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
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    Toast.makeText(PrivateChat.this, "succes", Toast.LENGTH_SHORT).show();
                    QBChatMessage chatMessage = new QBChatMessage();
                    chatMessage.setBody(messageEdt.getText().toString());
                    chatMessage.setSenderId(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_QB_USERID)));
                    chatMessage.setRecipientId(opponentId);
                    chatMessage.setProperty("notification_type", "1");
                    chatMessage.setProperty("save_to_history", "1"); // Save a message to history
                    // create a message
//                        int opponentId = oponantID;
                    QBAttachment attachment = new QBAttachment("video");
                    attachment.setId(qbFile.getId().toString());
                    attachment.setUrl(qbFile.getPublicUrl().toString());
                    chatMessage.addAttachment(attachment);
                    privateChatManager = chatService.getPrivateChatManager();
                    QBPrivateChat privateChat = privateChatManager.getChat(opponentId);
                    if (privateChat == null) {
                        privateChat = privateChatManager.createChat(opponentId, privateChatMessageListener);
                    }

                    try {
                        privateChat.sendMessage(chatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }

                    privateChat.addMessageSentListener(privateChatMessageSentListener);
                    privateChat.addMessageListener(privateChatMessageListener);
                    Toast.makeText(getApplicationContext(), "video Sent Successfully", Toast.LENGTH_SHORT).show();

//                    hashmap.put("url", qbFile.getPublicUrl().toString());
//                    chatArraylist.add(hashmap);
//                    chatListAdapter.notifyDataSetChanged();
//                    chatListAdapter = new PrivateChatMsgListAdapter(getApplicationContext(), chatArraylist);
//                    chatListAdapter.notifyDataSetChanged();
//                    lvChatDetails.setAdapter(chatListAdapter);
//                    lvChatDetails.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//                    lvChatDetails.setStackFromBottom(true);
//
//                    scrollMyListViewToBottom();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        } else {
            Toast.makeText(PrivateChat.this, "something went wrong", Toast.LENGTH_SHORT).show();
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
        if (imageFile != null) {
            sendImage();
        } else {
            Toast.makeText(getApplicationContext(), "Image not Found, Please again select image to send", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendImage() {
        QBContent.uploadFileTask(imageFile, true, "").performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                Toast.makeText(PrivateChat.this, "succes", Toast.LENGTH_SHORT).show();
                QBChatMessage chatMessage = new QBChatMessage();
                if (!messageEdt.getText().toString().equalsIgnoreCase("")) {
                    chatMessage.setBody(messageEdt.getText().toString());
                } else {
                    chatMessage.setBody("");
                }
                chatMessage.setSenderId(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_QB_USERID)));
                chatMessage.setRecipientId(opponentId);
                chatMessage.setProperty("notification_type", "1");
                chatMessage.setProperty("save_to_history", "1"); // Save a message to history
                chatMessage.setMarkable(true);

                // create a message
//                        int opponentId = oponantID;
                QBAttachment attachment = new QBAttachment("photo");
                attachment.setId(qbFile.getId().toString());
                attachment.setUrl(qbFile.getPublicUrl().toString());
                chatMessage.addAttachment(attachment);
                privateChatManager = chatService.getPrivateChatManager();
                QBPrivateChat privateChat = privateChatManager.getChat(opponentId);
                if (privateChat == null) {
                    privateChat = privateChatManager.createChat(opponentId, privateChatMessageListener);
                }

                try {
                    privateChat.sendMessage(chatMessage);
                    privateChat.addMessageSentListener(privateChatMessageSentListener);
                    privateChat.addMessageListener(privateChatMessageListener);
                    // save packet ID of current message
//                            String _id = chatMessage.getId();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(getApplicationContext(), "Error " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // SEND DOCUMENT In CHAT
    private void sendDocumentResultInPrivateChat(Intent data) {
        if (data != null) {
            documentuploadUri = data.getData();
            String pdfPath = documentuploadUri.getPath();
            imageFile = new File(pdfPath);
            sendDocument();

            Log.e("documentuploadUri", "DOC " + documentuploadUri);
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//                // Do something for 19 and above versions
//                // OI FILE Manager
//                String wholeID = DocumentsContract.getDocumentId(documentuploadUri);
//
//                // Split at colon, use second item in the array
//                String id = wholeID.split(":")[1];
//
//                String[] column = {MediaStore.Images.Media.DATA};
//
//                // where id is equal to
//                String sel = MediaStore.Images.Media._ID + "=?";
//
//                Cursor cursor = getContentResolver().
//                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                column, sel, new String[]{id}, null);
//                int columnIndex = cursor.getColumnIndex(column[0]);
//
//                if (cursor.moveToFirst()) {
//                    documentPath = cursor.getString(columnIndex);
//                }
//                cursor.close();
//                String DocName = documentPath.substring(documentPath.lastIndexOf('/') + 1);
//                Log.e("DOC", "Path : " + documentPath);
//                Log.e("DOC", "NAME : " + DocName);
//                imageFile = new File(documentPath);
//            } else {
//                documentPath = getPath(documentuploadUri);
//                imageFile = new File(documentPath);
//            }
//            if (!documentPath.equalsIgnoreCase("")) {
//                sendDocument();
//            } else {
//                Toast.makeText(PrivateChat.this, "No Path Found", Toast.LENGTH_SHORT).show();
//            }
        } else {
            Toast.makeText(getApplicationContext(), "No Document Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendDocument() {
        if (imageFile != null) {
            QBContent.uploadFileTask(imageFile, true, "").performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    Toast.makeText(PrivateChat.this, "succes", Toast.LENGTH_SHORT).show();
                    QBChatMessage chatMessage = new QBChatMessage();
                    chatMessage.setBody(messageEdt.getText().toString());
                    chatMessage.setSenderId(Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_QB_USERID)));
                    chatMessage.setRecipientId(opponentId);
                    chatMessage.setProperty("notification_type", "1");
                    chatMessage.setProperty("save_to_history", "1"); // Save a message to history
                    // create a message
//                        int opponentId = oponantID;
                    QBAttachment attachment = new QBAttachment("file");
                    attachment.setId(qbFile.getId().toString());
                    attachment.setUrl(qbFile.getPublicUrl().toString());
                    chatMessage.addAttachment(attachment);

                    Log.d("file", "attached fil Url :" + qbFile.getPublicUrl().toString());

                    privateChatManager = chatService.getPrivateChatManager();
                    QBPrivateChat privateChat = privateChatManager.getChat(opponentId);
                    if (privateChat == null) {
                        privateChat = privateChatManager.createChat(opponentId, privateChatMessageListener);
                    }

                    try {
                        privateChat.sendMessage(chatMessage);
                        privateChat.addMessageSentListener(privateChatMessageSentListener);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("Error Exception", "" + e);
                }
            });
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