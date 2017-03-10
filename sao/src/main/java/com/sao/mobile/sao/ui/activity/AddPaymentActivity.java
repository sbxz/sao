package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.LocalStore;

import java.util.HashSet;
import java.util.Set;

import static com.sao.mobile.saolib.utils.LocalStore.CARD_NUMBER_KEY;

public class AddPaymentActivity extends BaseActivity {
    private static final String TAG = AddPaymentActivity.class.getSimpleName();

    private EditText mInputName;
    private EditText mInputCardNumber;
    private EditText mInputDate;
    private EditText mInputCvc;
    private Button mOkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payement);

        setupHeader();
        setupBody();
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle("Enregistrer la carte de crédit");
    }

    private void setupBody() {
        mInputName = (EditText) findViewById(R.id.input_name);
        mInputCardNumber = (EditText) findViewById(R.id.input_card_number);
        mInputDate = (EditText) findViewById(R.id.input_date);
        mInputCvc = (EditText) findViewById(R.id.input_cvc);
        mOkButton = (Button) findViewById(R.id.okButton);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValid()) {
                    return;
                }

                addCardPayment();
                finish();
            }
        });

        mInputCardNumber.addTextChangedListener(new CardTextWatcher(mInputCardNumber));
        mInputDate.addTextChangedListener(new CardTextWatcher(mInputDate));
    }

    private void addCardPayment() {
        Set<String> keySets = LocalStore.readSetPreferences(mContext, CARD_NUMBER_KEY);

        if (keySets == null) {
            keySets = new HashSet<>();
        }

        String key = CARD_NUMBER_KEY + keySets.size();
        keySets.add(key);
        LocalStore.writeSetPreferences(mContext, CARD_NUMBER_KEY, keySets);

        Set<String> cardSets = new HashSet<>();
        cardSets.add(mInputName.getText().toString());
        cardSets.add(mInputCardNumber.getText().toString());
        cardSets.add(mInputDate.getText().toString());
        cardSets.add(mInputCvc.getText().toString());

        LocalStore.writeSetPreferences(mContext, key, cardSets);
    }

    private boolean isValid() {
        return mInputName.getText().length() > 0 &&
                mInputCardNumber.getText().length() == 19 &&
                mInputDate.getText().length() == 5 &&
                mInputCvc.getText().length() == 3;

    }

    private void updateCardNumber() {
        String text = mInputCardNumber.getText().toString();

        if (text.length() == 4 || text.length() == 9 || text.length() == 14) {
            mInputCardNumber.setText(mInputCardNumber.getText() + " ");
            mInputCardNumber.setSelection(mInputCardNumber.getText().length());
        }
    }

    private void updateDate() {
        String text = mInputDate.getText().toString();

        if (text.length() == 2) {
            mInputDate.setText(mInputDate.getText() + "/");
            mInputDate.setSelection(mInputDate.getText().length());
        }
    }

    private class CardTextWatcher implements TextWatcher {

        private View view;

        private CardTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_card_number:
                    updateCardNumber();
                    break;
                case R.id.input_date:
                    updateDate();
                    break;
            }
        }
    }
}
