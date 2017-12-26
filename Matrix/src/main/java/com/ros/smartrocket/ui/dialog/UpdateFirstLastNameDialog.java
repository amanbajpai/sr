package com.ros.smartrocket.ui.dialog;

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
import com.ros.smartrocket.db.entity.account.MyAccount;
import com.ros.smartrocket.utils.UIUtils;

public class UpdateFirstLastNameDialog extends Dialog implements View.OnClickListener {
    private DialogButtonClickListener buttonClickListener;
    private EditText nameEditText;

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
        nameEditText = (EditText) findViewById(R.id.dialogFirstNameEditText);
        nameEditText.setText(myAccount.getSingleName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                dismiss();
                buttonClickListener.onCancelButtonPressed();
                break;
            case R.id.updateButton:
                String name = nameEditText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    UIUtils.showSimpleToast(getContext(), R.string.fill_in_all_fields);
                } else {
                    dismiss();
                    buttonClickListener.onUpdateButtonPressed(name);
                }

                break;
            default:
                break;
        }
    }

    public interface DialogButtonClickListener {
        void onCancelButtonPressed();

        void onUpdateButtonPressed(String name);
    }
}
