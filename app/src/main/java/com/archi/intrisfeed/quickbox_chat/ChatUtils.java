package com.archi.intrisfeed.quickbox_chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.archi.intrisfeed.SignupActivity;
import com.archi.intrisfeed.util.Constant;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by archi on 9/29/2016.
 */

public class ChatUtils {

//    public static final String APP_ID = "47880";
//    public static final String AUTH_KEY = "hCQpjWq4HcFV5uk";
//    public static final String AUTH_SECRET = "NWKJSNhWHCZYgF9";
//    public static final String ACCOUNT_KEY = "2xzLtJGk1KXi7Uq8sys3";

//    //    public static final String APP_ID = "47880";
//    public static final String APP_ID = "48912";
//    //    public static final String AUTH_KEY = "hCQpjWq4HcFV5uk";
//    public static final String AUTH_KEY = "4MnSFA7XEX6KPP3";
//    //    public static final String AUTH_SECRET = "NWKJSNhWHCZYgF9";
//    public static final String AUTH_SECRET = "9Q842KO48ZdNsJd";
//    //    public static final String ACCOUNT_KEY = "2xzLtJGk1KXi7Uq8sys3";
//    public static final String ACCOUNT_KEY = "DtUDQqQxbeezkUprEuV7";

    Context context;
    QBUser user = new QBUser();

    public ChatUtils(Context getSignup) {

        QAsetting(getSignup);
    }

    private void QAsetting(Context getSignup) {

        QBSettings.getInstance().init(getSignup, Constant.APP_ID, Constant.AUTH_KEY, Constant.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Constant.ACCOUNT_KEY);
        context = getSignup;
    }

    //

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_chat);
//        QAsetting(getApplication());
//
//        //  TockenGenerate();
//        //   CreateSessionLogin();
//
//
//    }

    public void ChatRegister(SignupActivity signup, final String fullname, final String username, final String email, final String password) {
        final String[] strCustId = new String[0];
        QBSettings.getInstance().init(signup, Constant.APP_ID, Constant.AUTH_KEY, Constant.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Constant.ACCOUNT_KEY);
        QBChatService.setDebugEnabled(true); // enable chat logging
        QBChatService.setDefaultAutoSendPresenceInterval(60);
        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
        chatServiceConfigurationBuilder.setSocketTimeout(60); //Sets chat socket's read timeout in seconds
        chatServiceConfigurationBuilder.setKeepAlive(true); //Sets connection socket's keepAlive option.
        chatServiceConfigurationBuilder.setUseTls(true); //Sets the TLS security mode used when making the connection. By default TLS is disabled.
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);

        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession result, Bundle params) {
                // session created
                user.setId(result.getUserId());
//                final QBUser user = new QBUser();
//                user.setPhoto();
                user.setLogin(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setFullName(fullname);

                QBUsers.signUp(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        int SassionID = user.getId();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error", "Register in eroor  :" + e);
                    }
                });

            }

            @Override
            public void onError(QBResponseException responseException) {
//                Toast.makeText(getActivity(),"Eror :"+responseException.toString(),Toast.LENGTH_SHORT).show();
                Log.e("Error", "Register in error  :" + responseException);
            }
        });


//        QBAuth.createSession(new QBEntityCallback<QBSession>() {
//            @Override
//            public void onSuccess(QBSession session, Bundle params) {
//                // success
//                final QBUser user = new QBUser();
////                user.setPhoto();
//                user.setLogin(username);
//                user.setEmail(email);
//                user.setPassword(password);
//                user.setFullName(fullname);
//
//                Performer<ArrayList<QBUser>> performer = QBUsers.signUp(user);
//
//                QBUsers.signUp(user, new QBEntityCallback<QBUser>() {
//                    @Override
//                    public void onSuccess(QBUser user, Bundle args) {
//                        int SassionID = user.getId();
//                    }
//
//                    @Override
//                    public void onError(QBResponseException error) {
//                        // error
//                        Log.e("Error", "Register in eroor  :" + error);
//                    }
//                })
//            }
//
//            @Override
//            public void onError(QBResponseException error) {
//                // errors
//                // error
//                Log.e("Error", "Register in eroor  :" + error);
//            }
//        });

        //  String strId =  strCustId[0];
        //  Log.d("jai","get string from array :"+strId);
        return;
    }


    private void TockenGenerate() {
        String token = null;
        try {
            token = BaseService.getBaseService().getToken();
            Log.d("jai", "toekn :" + token);
        } catch (BaseServiceException e) {
            e.printStackTrace();
        }
        Date expirationDate = new Date();
        try {
            expirationDate = BaseService.getBaseService().getTokenExpirationDate();
        } catch (BaseServiceException e) {
            e.printStackTrace();
        }
// save to secure storage when your application goes offline or to the background

        Log.d("jai", "expire date :" + expirationDate);


// recreate session on next start app
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.HOUR, 2);
        Date twoHoursAfter = cal.getTime();
        Log.d("jai", "two hore after :" + twoHoursAfter);

        if (expirationDate.before(twoHoursAfter)) {
            try {
                QBAuth.createFromExistentToken(token, expirationDate);
            } catch (BaseServiceException e) {
                e.printStackTrace();
            }
        } else {
            // create a session


        }

    }

//    public void CreateSessionLogin(final Context login, String email, String password) {
//        QBChatService.setDebugEnabled(true); // enable chat logging
//        QBChatService.setDefaultAutoSendPresenceInterval(60);
//        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
//        chatServiceConfigurationBuilder.setSocketTimeout(60); //Sets chat socket's read timeout in seconds
//        chatServiceConfigurationBuilder.setKeepAlive(true); //Sets connection socket's keepAlive option.
//        chatServiceConfigurationBuilder.setUseTls(true); //Sets the TLS security mode used when making the connection. By default TLS is disabled.
//        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);
//        Log.d("jai", "test 1");
//        final QBChatService chatService = QBChatService.getInstance();
//        final QBUser user = new QBUser();
//        user.setEmail(email);
//        user.setPassword(password);
//        // final QBUser user = new QBUser("archirayan26@gmail.com", "archirayan26"
//
//        createSession(user, new QBEntityCallback<QBSession>() {
//            @Override
//            public void onSuccess(QBSession session, Bundle params) {
//                // success, login to chat
//
//
//                user.setId(session.getUserId());
//                QBUsers.signIn(user, new QBEntityCallback<QBUser>() {
//                    @Override
//                    public void onSuccess(QBUser user, Bundle params) {
//                        int ChatID = user.getId();
//                        Util.WriteSharePrefrence(login, Constant.ChatId, String.valueOf(ChatID));
//                        Log.d("jai", "my chat id :" + ChatID);
//
//                    }
//
//                    @Override
//                    public void onError(QBResponseException errors) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onError(QBResponseException errors) {
//
//            }
//        });
//
//    }

}







