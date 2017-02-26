package com.sao.mobile.sao.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.User;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.service.api.LoginService;
import com.sao.mobile.sao.ui.MainActivity;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.LocalStore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private UserManager mUserManager = UserManager.getInstance();

    private EditText mInputMail;
    private EditText mInputPassword;

    private TextInputLayout mInputLayoutMail;
    private TextInputLayout mInputLayoutPassword;

    private TextView mTextForgetPassword;

    private LoginService mLoginService;

    protected void initServices() {
        mLoginService = retrofit.create(LoginService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mInputMail = (EditText) findViewById(R.id.input_mail);
        mInputPassword = (EditText) findViewById(R.id.input_password);

        mInputLayoutMail = (TextInputLayout) findViewById(R.id.input_layout_mail);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        mInputMail.addTextChangedListener(new LoginTextWatcher(mInputMail));
        mInputPassword.addTextChangedListener(new LoginTextWatcher(mInputPassword));

        mTextForgetPassword = (TextView) findViewById(R.id.forgetPassword);
        mTextForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "New activity for forget password", Toast.LENGTH_SHORT).show();
            }
        });

        Button signUpButton = (Button) findViewById(R.id.btn_signup);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        initServices();
    }

    private void submitForm() {
        if (!validateMail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        showProgressDialog(getString(R.string.connect_progress));

        Call<Void> loginCall = mLoginService.login();
        loginCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success login");
                LocalStore.writePreferences(mContext, LocalStore.SESSION_ID, "LOCAL_SESSION_ID");
                mUserManager.currentUser = new User( "Seb", "http://i.imgur.com/CqmBjo5.jpg");
                startActivity(MainActivity.class);
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail login. Message= " + t.getMessage());
                Snackbar.make(getView(), R.string.failure_data, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                hideProgressDialog();
            }
        });
    }

    private boolean validateMail() {
        String mail = mInputMail.getText().toString().trim();

        if (mail.isEmpty() || !isValidMail(mail)) {
            mInputLayoutMail.setError(getString(R.string.err_msg_mail));
            requestFocus(mInputMail);
            return false;
        } else {
            mInputLayoutMail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (mInputPassword.getText().toString().trim().isEmpty()) {
            mInputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(mInputPassword);
            return false;
        } else {
            mInputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidMail(String mail) {
        return !TextUtils.isEmpty(mail) && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class LoginTextWatcher implements TextWatcher {

        private View view;

        private LoginTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_mail:
                    validateMail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
}
