package com.ros.smartrocket.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.ros.smartrocket.R;

/**
 * Check registration fields and return status
 */

public class RegistrationFieldTextWatcher implements TextWatcher {

    private Context context;
    private View view;
    private TextView validationText;

    public RegistrationFieldTextWatcher(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public RegistrationFieldTextWatcher(Context context, View view, TextView validationText) {
        this.context = context;
        this.view = view;
        this.validationText = validationText;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * Change UI elements by entered data
     */
    public void afterTextChanged(Editable s) {
        switch (view.getId()) {
            case R.id.firstNameEditText:
            case R.id.lastNameEditText:
            case R.id.birthdayEditText:
                EditText editText = (EditText) view;
                String name = editText.getText().toString().trim();
                UIUtils.setEditTextColorByState(context, editText, !TextUtils.isEmpty(name));
                break;
            case R.id.emailEditText:
                EditText emailEditText = (EditText) view;
                String email = emailEditText.getText().toString().trim();

                UIUtils.setEditTextColorByState(context, emailEditText, UIUtils.isEmailValid(email));
                UIUtils.setEmailEditTextImageByState(emailEditText, UIUtils.isEmailValid(email));
                break;
            case R.id.passwordEditText:
                EditText passwordEditText = (EditText) view;
                String password = passwordEditText.getText().toString().trim();

                boolean isPasswordValid = UIUtils.isPasswordValid(password);
                UIUtils.setEditTextColorByState(context, passwordEditText, isPasswordValid);
                UIUtils.setPasswordEditTextImageByState(passwordEditText, isPasswordValid);

                if (validationText != null) {
                    validationText.setVisibility(isPasswordValid ? View.GONE : View.VISIBLE);
                }
                break;
        }
    }

}