package com.sao.mobile.sao.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.FacebookResult;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.MainActivity;
import com.sao.mobile.saolib.entities.User;
import com.sao.mobile.saolib.ui.base.BaseActivity;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private UserManager mUserManager = UserManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupFacebook();
    }

    private void setupFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        Button facebookButton = (Button) findViewById(R.id.fb_button);

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        showProgressDialog(getString(R.string.connect_progress));
                        if (Profile.getCurrentProfile() == null) {
                            new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    loginSuccess(loginResult, profile2);
                                }
                            };
                        } else {
                            loginSuccess(loginResult, Profile.getCurrentProfile());
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "On facebook cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e(TAG, "On facebook error cause= " + exception.getCause() + " message= " + exception.getMessage());
                    }
                });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(mContext, Arrays.asList("email", "public_profile", "user_friends"));
            }
        });

    }

    private void loginSuccess(LoginResult loginResult, final Profile profile) {
        if (profile == null) {
            hideProgressDialog();
            return;
        }

        Log.i(TAG, "On facebook login success");

        String deviceId = FirebaseInstanceId.getInstance().getId();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        FacebookResult facebookResult = new FacebookResult(loginResult, profile);
        Call<Void> barCall = mApiManager.userService.login(facebookResult, deviceId, deviceToken);
        barCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                hideProgressDialog();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail    sao login");
                    return;
                }

                Log.i(TAG, "On Sao login success");

                mUserManager.currentUser = new User(profile.getName(), profile.getProfilePictureUri(100, 100).toString(), AccessToken.getCurrentAccessToken().getUserId());
                startActivity(MainActivity.class);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail sao login. Message= " + t.getMessage());
                hideProgressDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
