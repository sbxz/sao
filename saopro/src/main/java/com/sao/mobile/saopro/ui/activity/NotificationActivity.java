package com.sao.mobile.saopro.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.manager.ApiManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends BaseActivity {
    private static final String TAG = NotificationActivity.class.getSimpleName();

    public static final int CLIENT_PRESENT = 0;
    public static final int CLIENT_ALL = 0;

    private Spinner mReceiverSpinner;
    private Button mSendNotification;
    private TextInputLayout mInputMessageLayout;

    private ApiManager mApiManager = ApiManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        setupHeader();
        setupSpinner();
        setupEditText();
        setupSendButton();
    }

    private void setupEditText() {
        mInputMessageLayout = (TextInputLayout) findViewById(R.id.inputMessageLayout);
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.notification_title));
    }

    private void setupSpinner() {
        mReceiverSpinner = (Spinner) findViewById(R.id.receiverSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.receiver_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReceiverSpinner.setAdapter(adapter);
        mReceiverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupSendButton() {
        mSendNotification = (Button) findViewById(R.id.sendNotification);
        mSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(mInputMessageLayout.getEditText().length() >  mInputMessageLayout.getCounterMaxLength()) {
                SnackBarUtils.showSnackError(getView());
                return;
            }
            sendNotification();
            }
        });
    }

    private void sendNotification() {
        int id = mReceiverSpinner.getId();
        String message = mInputMessageLayout.getEditText().getText().toString();
        Call<Void> barServiceCall = mApiManager.barService.sendNotification();
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success send notification");
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail send notification.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }
}
