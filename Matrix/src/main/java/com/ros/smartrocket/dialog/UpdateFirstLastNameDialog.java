package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Dialog for updating user's first and last name
 */
public class UpdateFirstLastNameDialog extends Dialog implements View.OnClickListener {
    private DialogButtonClickListener buttonClickListener;
    private EditText firstNameEditText;
    private EditText lastNameEditText;

    public UpdateFirstLastNameDialog(Activity activity, DialogButtonClickListener buttonClickListener) {
        super(activity);
        this.buttonClickListener = buttonClickListener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_update_first_last_name);
        setCancelable(false);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.updateButton).setOnClickListener(this);

        final MyAccount myAccount = App.getInstance().getMyAccount();
        firstNameEditText = (EditText) findViewById(R.id.dialogFirstNameEditText);
        firstNameEditText.setText(myAccount.getFirstName());
        lastNameEditText = (EditText) findViewById(R.id.dialogLastNameEditText);
        lastNameEditText.setText(myAccount.getLastName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                dismiss();
                buttonClickListener.onCancelButtonPressed();
                break;
            case R.id.updateButton:
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();

                if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
                    UIUtils.showSimpleToast(getContext(), R.string.fill_in_all_fields);
                } else {
                    dismiss();
                    buttonClickListener.onUpdateButtonPressed(firstName, lastName);
                }

                break;
            default:
                break;
        }
    }

    public interface DialogButtonClickListener {
        void onCancelButtonPressed();

        void onUpdateButtonPressed(String firstName, String lastName);
    }
}
