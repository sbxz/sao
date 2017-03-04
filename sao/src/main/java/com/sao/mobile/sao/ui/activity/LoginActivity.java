package com.sao.mobile.sao.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.saolib.ui.base.BaseActivity;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.Date;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private UserManager mUserManager = UserManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();

    private LoginButton mLoginButton;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCallbackManager = CallbackManager.Factory.create();

        mLoginButton = (LoginButton) findViewById(R.id.fb_button);
        mLoginButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends"));
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginSuccess(loginResult);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "On facebook cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "On facebook error cause= " + error.getCause() + " message= " + error.getMessage());
            }
        });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        Uri uri = profile.getProfilePictureUri(75,75);
        if(AccessToken.getCurrentAccessToken() != null) {
            graphFriendRequest();
            graphUserRequest();
            graphUserFriendRequest();
            graphUserFriendListRequest();
        }
    }

    private void loginSuccess(LoginResult loginResult) {
        AccessToken accessToken = loginResult.getAccessToken();
        String token = accessToken.getToken();
        String userId = accessToken.getUserId();
        Date expires = accessToken.getExpires();
        Date lastRefresh = accessToken.getLastRefresh();
        Log.i(TAG, "On login success");

        graphFriendRequest();

    }

    private void graphFriendRequest() {
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {

                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        Log.i(TAG, "graphFriendRequest " + objects.toString());
                    }
                });

        request.executeAsync();
    }

    private void graphUserRequest() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + Profile.getCurrentProfile().getId(),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.i(TAG, "graphUserRequest " + response.getJSONObject().toString());
                    }
                }
        ).executeAsync();
    }

    private void graphUserFriendRequest() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + Profile.getCurrentProfile().getId() + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.i(TAG, "graphUserFriendRequest " + response.getJSONObject().toString());
                    }
                }
        ).executeAsync();
    }

    private void graphUserFriendListRequest() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + Profile.getCurrentProfile().getId() + "/friendlists",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.i(TAG, "graphUserFriendListRequest " + response.getJSONObject().toString());
                    }
                }
        ).executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
