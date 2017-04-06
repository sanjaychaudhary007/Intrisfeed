package com.archi.intrisfeed.fragment;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.archi.intrisfeed.R;
import com.archi.intrisfeed.adapter.ChatListQBAdapter;
import com.archi.intrisfeed.adapter.ChatUserListAdapter;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.archi.intrisfeed.MainActivity.occupantIdsList;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by archi_info on 9/24/2016.
 */
public class ChattingFragment extends Fragment implements View.OnClickListener {

    private static final String PROPERTY_OCCUPANTS_IDS = "occupany_id";
    private static final String PROPERTY_DIALOG_TYPE = "dialog_type";
    private static final String PROPERTY_DIALOG_NAME = "dialog_name";
    private static final String PROPERTY_NOTIFICATION_TYPE = "notification_type";
    private static final String CREATING_DIALOG = "create_dialog";
    //    TextView tvGotoChat;
    String groupName = "";
    public String uNameStr, uPwdStr;
    ChatUserListAdapter adapter;
    ChatListQBAdapter adapterChats;

    public QBUser userData;
    public ProgressBar pd, pdContacts;
    public ListView lvChats, lvContacts;
    public ArrayList<QBUser> qbUserList;
    public ArrayList<String> arrayUserName;
    public ImageView ivToolbarAdd;
    public Toolbar toolbar;
    QBSystemMessageListener systemMessageListener;
    public static QBChatService chatService;
    TextView tvChat, tvContact, tvChatsBottomLine, tvContactBottomLine;
    RelativeLayout rlContact, rlChats;
    QBUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chatting, container, false);


        // set option menu visible on fragment
        setHasOptionsMenu(true);

        occupantIdsList.clear();
        pd = (ProgressBar) rootView.findViewById(R.id.fragement_chatting_progress);
        lvChats = (ListView) rootView.findViewById(R.id.fragement_chatting_listview);
        lvContacts = (ListView) rootView.findViewById(R.id.fragement_contact_listview);

        QBSettings.getInstance().init(getActivity(), Constant.APP_ID, Constant.AUTH_KEY, Constant.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Constant.ACCOUNT_KEY);
        qbUserList = new ArrayList<>();
        tvChat = (TextView) rootView.findViewById(R.id.tvChats);
        tvContact = (TextView) rootView.findViewById(R.id.tvContacts);
        rlContact = (RelativeLayout) rootView.findViewById(R.id.rlContacts);
        rlChats = (RelativeLayout) rootView.findViewById(R.id.rlChats);
        tvChatsBottomLine = (TextView) rootView.findViewById(R.id.tvChatsBottomLine);
        tvContactBottomLine = (TextView) rootView.findViewById(R.id.tvContactsBottomLine);

        tvChat.setOnClickListener(this);
        tvContact.setOnClickListener(this);
//        toolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
//        ivToolbarAdd = (ImageView)toolbar.findViewById(R.id.iv_toolbar_add);
//        ivToolbarAdd.setVisibility(View.VISIBLE);


        uNameStr = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_EMAIL);
        uPwdStr = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_PASSWORD);
        arrayUserName = new ArrayList<>();
//      new GetFriendList().execute();
        user = new QBUser();
        user.setEmail(uNameStr);
        user.setPassword(uPwdStr);
        createSession(user);


        return rootView;
    }

    private void createSession(final QBUser QBuser) {


        QBAuth.createSession(QBuser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                // session created
                user.setId(result.getUserId());
                Log.d("ID",""+result.getUserId());

                chatService = QBChatService.getInstance();
                chatService.startAutoSendPresence(10);
                // LOG IN CHAT SERVICE
                if (!chatService.isLoggedIn()) {
                    chatService.login(user, new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle args) {
                            Log.e("$$$$$$$$$$$$", "loged chat");
                            Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(QBResponseException errors) {
                            Log.e("$$$$$$$$$$$", "not loged\n" + errors.getMessage());
                            Toast.makeText(getApplicationContext(), "Fail " + errors, Toast.LENGTH_SHORT).show();
                            //error
                        }
                    });
                }
                loginForQuickBlox(QBuser);
            }

            @Override
            public void onError(QBResponseException responseException) {
               Toast.makeText(getActivity(), "Eror :" + responseException.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Session not created, please try again.", Toast.LENGTH_SHORT).show();
//                createSession(QBuser);
                pd.setVisibility(View.GONE);

            }
        });
    }
  /*  @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), PrivateChat.class);
        startActivity(intent);
    }*/


    private boolean loginForQuickBlox(final QBUser user) {
        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Log.e("QBUSER", "********* " + qbUser);

                Toast.makeText(getActivity(), "Login Successfully", Toast.LENGTH_LONG).show();
                userData = new QBUser();
                userData.setEmail(uNameStr);
                userData.setPassword(uPwdStr);
                userData.setId(qbUser.getId());

                Log.e("UID "," ##### "+qbUser.getId());
                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_QB_USERID, "" + qbUser.getId());
                Log.e("USER", "******** USER DETAILS **********");
                Log.e("ID", "" + qbUser.getId());
                Log.e("LoginId", "" + qbUser.getLogin());
                Log.e("Email", "" + qbUser.getEmail());
                Log.e("NAME", "" + qbUser.getFullName());


                QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
                pagedRequestBuilder.setPage(1);
                pagedRequestBuilder.setPerPage(50);
                QBUsers.getUsers(pagedRequestBuilder, bundle).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
//                        Toast.makeText(getActivity(), "DATA", Toast.LENGTH_LONG).show();
                        int totalEntries = bundle.getInt("total_entries");
                        ArrayList<QBUser> arrySelectedUser = new ArrayList<QBUser>();
                        for (int i = 0; i < bundle.size(); i++) {
                            if (!Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_EMAIL).equalsIgnoreCase(users.get(i).getEmail()) && !users.get(i).getEmail().equalsIgnoreCase("piccitipics@gmail.com")){
                                Log.e(">>>>>>>>> ", "=========== USERS ============" + i);
                                Log.e("ID", "" + users.get(i).getId());
                                Log.e("NAME", "" + users.get(i).getFullName());
                                Log.e("USER_ID", "" + users.get(i).getId());
                                Log.e("Email", "" + users.get(i).getEmail());
                                arrySelectedUser.add(users.get(i));
                            }
                        }

                        adapter = new ChatUserListAdapter(getActivity(), arrySelectedUser);
                        lvContacts.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        pd.setVisibility(View.GONE);


                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getActivity(), "ERR " + e, Toast.LENGTH_SHORT);
                        pd.setVisibility(View.GONE);
                    }
                });


                QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
                requestBuilder.setLimit(100);

                QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(
                        new QBEntityCallback<ArrayList<QBChatDialog>>() {
                            @Override
                            public void onSuccess(ArrayList<QBChatDialog> result, Bundle params) {
                                int totalEntries = params.getInt("total_entries");
                                ArrayList<QBChatDialog> arrySelectedUserChat = new ArrayList<QBChatDialog>();
                                for (int i = 0; i < result.size(); i++) {

                                    if (result.get(i).getType().toString().equalsIgnoreCase("GROUP")) {
                                        Log.e(">>>>>>>>> ", "===========GROUP============" + i);
                                        Log.e("ID", "" + result.get(i).getDialogId());
                                        Log.e("NAME", "" + result.get(i).getName());
                                        Log.e("USER_ID", "" + result.get(i).getUserId());
                                        Log.e("RECEIPNT_ID", "" + result.get(i).getRecipientId());
                                        arrySelectedUserChat.add(result.get(i));
                                    } else {
//                                    for (int k = 0; k < arrayUserName.size(); k++) {
//                                    if (dialogs.get(i).getName().equals(arrayUserName.get(k))) {
                                        Log.e("ADDED ", "" + result.get(i).getName());
                                        Log.e(">>>>>>>>> ", "===========PRIVATE============" + i);
                                        Log.e("ID", "" + result.get(i).getDialogId());
                                        Log.e("NAME", "" + result.get(i).getName());
                                        Log.e("USER_ID", "" + result.get(i).getUserId());
                                        Log.e("RECEIPNT_ID", "" + result.get(i).getRecipientId());
                                        // ADD Record TO the List
                                        arrySelectedUserChat.add(result.get(i));
//                                    }
//                                }
                                    }
                                }
                                if (arrySelectedUserChat.size() > 0) {
                                    adapterChats = new ChatListQBAdapter(getActivity(), arrySelectedUserChat);
                                    lvChats.setAdapter(adapterChats);
                                    adapterChats.notifyDataSetChanged();
                                }
                                pd.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(QBResponseException responseException) {
                                pd.setVisibility(View.GONE);
                            }
                        });


            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(getActivity(), "Exception " + e, Toast.LENGTH_SHORT).show();
                pd.setVisibility(View.GONE);
            }
        });

        return true;
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvChats:
                rlContact.setVisibility(View.GONE);
                rlChats.setVisibility(View.VISIBLE);
                tvChatsBottomLine.setVisibility(View.VISIBLE);
                tvContactBottomLine.setVisibility(View.GONE);
                break;

            case R.id.tvContacts:
                rlContact.setVisibility(View.VISIBLE);
                rlChats.setVisibility(View.GONE);
                tvChatsBottomLine.setVisibility(View.GONE);
                tvContactBottomLine.setVisibility(View.VISIBLE);

                break;


        }
    }

    private class GetFriendList extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String userID = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            //http://181.224.157.105/~hirepeop/host1/intrisfeed/api/get_friend_list.php?id=26

//            String Url = Constant.BASE_URL + "get_friend_list.php?id=" + userID;
            HashMap<String, String> hashmap = new HashMap<>();
            hashmap.put("id", "" + userID);
//             String Url = "http://181.224.157.105/~hirepeop/host1/intrisfeed/api/get_friend_list.php?id=26";
            return Util.getResponseofPost(Constant.BASE_URL + "get_friend_list.php?", hashmap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("RESPONSE", " " + s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String strUserName = jsonObject1.getString("email");
                            arrayUserName.add(strUserName);
                    }
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            /** EDIT **/
            case R.id.action_cart:
                //openEditProfile(); //Open Edit Profile Fragment
//                Toast.makeText(getApplicationContext(),"GROUP CHAT",Toast.LENGTH_LONG).show();

                if (item.getTitle().toString().equalsIgnoreCase("CREATE GROUP")) {
                    if (occupantIdsList.size() > 0) {
                        item.setTitle("SAVE");
//                        Log.e("OCCUPANT", "" + occupantIdsList);
                        getGroupNamePopup();
                    } else {
                        Toast.makeText(getActivity(), "Please select user to create group", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // create Group of the User
                    addChatGroup(groupName, occupantIdsList);
                }


                return true;


            default:
                return super.onOptionsItemSelected(item);
        }//end switch
    }//end onOptionsItemSelected

    public void getGroupNamePopup() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.popup_add_group_name);
        dialog.setTitle("Add Group Name");
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        final EditText etGroupName = (EditText) dialog.findViewById(R.id.etGroupName);
        TextView ok = (TextView) dialog.findViewById(R.id.tvCreate_CreateGroup);
        TextView cancel = (TextView) dialog.findViewById(R.id.tvCalcel_CreateGroup);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etGroupName.getText().toString().equalsIgnoreCase("")) {
                    groupName = etGroupName.getText().toString();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please Enter Group Name.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    // create User chat group
    public void addChatGroup(String name, ArrayList<Integer> userIds) {
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(name);
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(userIds);

        QBGroupChatManager groupChatManager = chatService.getGroupChatManager();
        groupChatManager.createDialog(dialog, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog dialog, Bundle args) {

                occupantIdsList.clear();
                Toast.makeText(getActivity(), "Group Successfully Created", Toast.LENGTH_SHORT).show();
                adapter.updateUserList();
                Log.e("RESPONSE ", "GROUP CREATE " + dialog);
//                Log.e("ARGS ", " " + args);
                // create Group chat notification
                createChatNotificationForGroupChatCreation(dialog);

                QBSystemMessagesManager systemMessagesManager = chatService.getSystemMessagesManager();
//                Log.e("systemMessagesManager", "" + systemMessagesManager);

                for (Integer userID : dialog.getOccupants()) {
                    QBChatMessage chatMessage = createChatNotificationForGroupChatCreation(dialog);
                    Log.e("chatMessage", "" + chatMessage);
                    Calendar calander = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

                    String time = simpleDateFormat.format(calander.getTime());
//                    long time = DateUtils.getCurrentTime();
//                    Log.e("time", "" + time);
                    chatMessage.setProperty("date_sent", time + "");
//                    Log.e("userID", "" + userID);
                    chatMessage.setRecipientId(userID);

                    try {
                        systemMessagesManager.sendSystemMessage(chatMessage);
                        systemMessagesManager.addSystemMessageListener(systemMessageListener);
                    } catch (SmackException.NotConnectedException e) {
                        Log.e("Exception ", "EXC " + e);
                    } catch (IllegalStateException ee) {
                        Log.e("Exception ", "EE " + ee);
                    }
                }


                systemMessageListener = new QBSystemMessageListener() {
                    @Override
                    public void processMessage(QBChatMessage qbChatMessage) {
                        Log.e("SUCCESS", ">> " + qbChatMessage);

                    }

                    @Override
                    public void processError(QBChatException e, QBChatMessage qbChatMessage) {
                        Log.e("SUCCESS", ">> " + qbChatMessage);
                    }
                };


            }

            @Override
            public void onError(QBResponseException errors) {
                Toast.makeText(getActivity(), "FAIL", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // send notification to the user which are selected in group
    public static QBChatMessage createChatNotificationForGroupChatCreation(QBChatDialog dialog) {
        String dialogId = String.valueOf(dialog.getDialogId());
        String roomJid = dialog.getRoomJid();
        String occupantsIds = TextUtils.join(",", dialog.getOccupants());
        String dialogName = dialog.getName();
        String dialogTypeCode = String.valueOf(dialog.getType().ordinal());
        Log.e("DATA ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Log.e("dialogID", ">> " + dialogId);
        Log.e("roomJid", ">> " + roomJid);
        Log.e("occupantsIds", ">> " + occupantsIds);
        Log.e("dialogName", ">> " + dialogName);
        Log.e("dialogTypeCode", ">> " + dialogTypeCode);


        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody("optional text");

        // Add notification_type=1 to extra params when you created a group chat
        //
        chatMessage.setProperty("notification_type", "2");

        chatMessage.setProperty("_id", dialogId);
        if (!TextUtils.isEmpty(roomJid)) {
            chatMessage.setProperty("room_jid", roomJid);
        }
        chatMessage.setProperty("occupants_ids", occupantsIds);
        if (!TextUtils.isEmpty(dialogName)) {
            chatMessage.setProperty("name", dialogName);
        }
        chatMessage.setProperty("type", dialogTypeCode);
        return chatMessage;
    }

    private QBChatMessage buildSystemMessageAboutCreatingGroupDialog(QBChatDialog dialog) {
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setDialogId(dialog.getDialogId());
        qbChatMessage.setProperty(PROPERTY_OCCUPANTS_IDS, String.valueOf(dialog.getOccupants()));
        qbChatMessage.setProperty(PROPERTY_DIALOG_TYPE, String.valueOf(dialog.getType().getCode()));
        qbChatMessage.setProperty(PROPERTY_DIALOG_NAME, String.valueOf(dialog.getName()));
        qbChatMessage.setProperty(PROPERTY_NOTIFICATION_TYPE, CREATING_DIALOG);

        return qbChatMessage;
    }

    //Let's notify occupants
    public void sendSystemMessageAboutCreatingDialog(QBSystemMessagesManager systemMessagesManager, QBChatDialog dialog) {
        QBChatMessage systemMessageCreatingDialog = buildSystemMessageAboutCreatingGroupDialog(dialog);

        for (Integer recipientId : dialog.getOccupants()) {
            if (!recipientId.equals(chatService.getUser().getId())) {
                systemMessageCreatingDialog.setRecipientId(recipientId);
                try {
                    systemMessagesManager.sendSystemMessage(systemMessageCreatingDialog);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
