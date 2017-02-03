package com.sao.mobile.saopro.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Order;
import com.sao.mobile.saopro.service.api.BarService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends BaseActivity {
    private static final String TAG = NotificationActivity.class.getSimpleName();

    public static final int CLIENT_PRESENT = 0;
    public static final int CLIENT_ALL = 0;

    private BarService mBarService;

    private Spinner mReceiverSpinner;
    private Button mSendNotification;
    private TextInputLayout mInputMessageLayout;

    protected void initServices() {
        mBarService = retrofit.create(BarService.class);
    }

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
                Snackbar.make(getView(), R.string.failure_data, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
            sendNotification();
            }
        });
    }

    private void sendNotification() {
        int id = mReceiverSpinner.getId();
        String message = mInputMessageLayout.getEditText().getText().toString();
        Call<Void> barServiceCall = mBarService.sendNotification();
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success send notification");
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail send notification. Message= " + t.getMessage());
                Snackbar.make(getView(), R.string.failure_data, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
