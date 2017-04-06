package com.archi.intrisfeed;

/**
 * Created by archi_info on 9/26/2016.
 */


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.archi.intrisfeed.fragment.BrowseFragment;
import com.archi.intrisfeed.fragment.ChattingFragment;
import com.archi.intrisfeed.fragment.ContentAuthorFragment;
import com.archi.intrisfeed.fragment.DreamTeamFragment;
import com.archi.intrisfeed.fragment.FeedFragment;
import com.archi.intrisfeed.fragment.HomeFragment;
import com.archi.intrisfeed.fragment.InboxFragment;
import com.archi.intrisfeed.fragment.InterisfeedFragment;
import com.archi.intrisfeed.fragment.LinkRequestFragment;
import com.archi.intrisfeed.fragment.SettingFragment;
import com.archi.intrisfeed.fragment.SocialNetworksFragment;
import com.archi.intrisfeed.slidemenu.FragmentDrawer;
import com.archi.intrisfeed.util.Constant;
import com.archi.intrisfeed.util.Util;
import com.facebook.FacebookSdk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {
    public static ArrayList<Integer> occupantIdsList;
    public static ArrayList<String> groupName;

    private FragmentDrawer drawerFragment;
    private Toolbar mToolbar;
    public ImageView ivUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        occupantIdsList = new ArrayList<Integer>();
        groupName = new ArrayList<String>();
        occupantIdsList.clear();
        groupName.clear();


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ivUser = (ImageView) findViewById(R.id.ivUserImage);
//        ivUser.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_linkrequest));
        Picasso.with(getApplicationContext()).load(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PROFILE_PIC)).error(R.drawable.ic_user).into(ivUser);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        displayView(0);
    }












    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (String.valueOf(position)) {
            case "0":
                title = getString(R.string.home);
                fragment = new HomeFragment();
                break;
            case "1":
                title = getString(R.string.Browse);
                fragment = new BrowseFragment();
                break;
            case "2":
                title = getString(R.string.Linkrequest);
                fragment = new LinkRequestFragment();
                break;
            case "3":
                title = getString(R.string.Chatting);
                fragment = new ChattingFragment();
                break;

            case "4":
                title = getString(R.string.DreamTeam);
                fragment = new DreamTeamFragment();
                break;
            case "5":
                title = getString(R.string.Inbox);
                fragment = new InboxFragment();
                break;
            case "6":
                title = getString(R.string.feed);
                fragment = new FeedFragment();
                break;

//            case "6":
//                fragment = new SharingFragment();
//                break;
            case "7":
                title = getString(R.string.author);
                fragment = new ContentAuthorFragment();
                break;

            case "8":
                title = getString(R.string.Interisfeed);
                fragment = new InterisfeedFragment();
                break;
            case "9":
                title = getString(R.string.SocialNetworks);
                fragment = new SocialNetworksFragment();
                break;

            case "10":

                title = getString(R.string.setting);
                fragment = new SettingFragment();
                break;
            case "11":
                // logout
                alertDialogShow();

                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    public void alertDialogShow() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage(R.string.logout_note);

        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
//                QBUsers.signOut(new QBEntityCallback() {
//                    @Override
//                    public void onSuccess(Object o, Bundle bundle) {
                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID, "");
                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_IS_LOGGEDIN, "0");
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                dialog.dismiss();
//                    }
//
//                    @Override
//                    public void onError(QBResponseException errors) {
//                        toast("something went wrong."+errors.getMessage());
//                    }
//                });
            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button ybutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        ybutton.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        ybutton.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));

    }

    @Override
    public void onBackPressed() {

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_cart:
//                Toast.makeText(getApplicationContext(),"GROUP CHAT",Toast.LENGTH_LONG).show();
//                if(occupantIdsList.size() > 0) {
//                    Log.e("OCCUPANT",""+occupantIdsList);
//                    createGroup();
//                }else{
//                    Toast.makeText(MainActivity.this, "Please select user to create group", Toast.LENGTH_SHORT).show();
//                }
//                return true;
//          default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

//    // create User chat group
//    private void createGroup(){
//        QBDialog dialog = new QBDialog();
////        +ChattingFragment.groupName
//        dialog.setName("MEHUL's GROUP");
//        dialog.setPhoto("1786");
//        dialog.setType(QBDialogType.GROUP);
//        dialog.setOccupantsIds(occupantIdsList);
//
//        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
//        groupChatManager.createDialog(dialog, new QBEntityCallback<QBDialog>() {
//            @Override
//            public void onSuccess(QBDialog dialog, Bundle args) {
//            Log.e("DIALOG",">> "+dialog);
//                Log.e("args",">> "+args);
//                createChatNotificationForGroupChatCreation(dialog);
//                QBSystemMessagesManager systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
//
//                for (Integer userID : dialog.getOccupants()) {
//
//                    QBChatMessage chatMessage = createChatNotificationForGroupChatCreation(dialog);
//                    Calendar calander = Calendar.getInstance();
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
//
//                    String time = simpleDateFormat.format(calander.getTime());
////                    long time = DateUtils.getCurrentTime();
//                    chatMessage.setProperty("date_sent", time + "");
//
//                    chatMessage.setRecipientId(userID);
//
//                    try {
//                        systemMessagesManager.sendSystemMessage(chatMessage);
//                    } catch (SmackException.NotConnectedException e) {
//
//                    } catch (IllegalStateException ee){
//
//                    }
//                }
//            }
//
//            @Override
//            public void onError(QBResponseException errors) {
//                Log.e("args",">> "+errors);
//            }
//        });
//    }
//
//    // send notification to the user which are selected in group
//    public static QBChatMessage createChatNotificationForGroupChatCreation(QBDialog dialog) {
//        String dialogId = String.valueOf(dialog.getDialogId());
//        String roomJid = dialog.getRoomJid();
//        String occupantsIds = TextUtils.join(",", dialog.getOccupants());
//        String dialogName = dialog.getName();
//        String dialogTypeCode = String.valueOf(dialog.getType().ordinal());
//
//        QBChatMessage chatMessage = new QBChatMessage();
//        chatMessage.setBody("optional text");
//
//        // Add notification_type=1 to extra params when you created a group chat
//        //
//        chatMessage.setProperty("notification_type", "1");
//
//        chatMessage.setProperty("_id", dialogId);
//        if (!TextUtils.isEmpty(roomJid)) {
//            chatMessage.setProperty("room_jid", roomJid);
//        }
//        chatMessage.setProperty("occupants_ids", occupantsIds);
//        if (!TextUtils.isEmpty(dialogName)) {
//            chatMessage.setProperty("name", dialogName);
//        }
//        chatMessage.setProperty("type", dialogTypeCode);
//
//        return chatMessage;
//    }






}