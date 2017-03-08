package com.sao.mobile.saopro.ui.activity;

import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sao.mobile.saolib.entities.Trader;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.LocalStore;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.TraderManager;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private CarouselView mCarouselView;
    private int[] mTutoImage = {R.drawable.ic_font_login_dark_1, R.drawable.ic_font_login_dark_2, R.drawable.ic_font_login_dark_3, R.drawable.ic_font_login_dark_4};

    private EditText mInputMail;
    private EditText mInputPassword;

    private TextInputLayout mInputLayoutMail;
    private TextInputLayout mInputLayoutPassword;

    private TextView mTextForgetPassword;

    private ApiManager mApiManager = ApiManager.getInstance();
    private TraderManager mTraderManager = TraderManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mCarouselView = (CarouselView) findViewById(R.id.carouselView1);
        mCarouselView.setPageCount(mTutoImage.length);
        mCarouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(mTutoImage[position]);
            }
        });

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
    }

    private void submitForm() {
        if (!validateMail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        showProgressDialog(getString(R.string.connect_progress));

        Call<Trader> loginCall = mApiManager.traderService.login(mInputMail.getText().toString().trim(), mInputPassword.getText().toString().trim());
        loginCall.enqueue(new Callback<Trader>() {
            @Override
            public void onResponse(Call<Trader> call, Response<Trader> response) {
                hideProgressDialog();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail sao pro login");
                    SnackBarUtils.showSnackError(getView());
                    return;
                }

                Log.i(TAG, "Success login");

                LocalStore.writePreferences(mContext, LocalStore.TRADER_ID, response.body().getTraderId().toString());
                mTraderManager.trader = response.body();

                startActivity(BarSelectActivity.class);
            }

            @Override
            public void onFailure(Call<Trader> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail login.", t);
                SnackBarUtils.showSnackError(getView());
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
