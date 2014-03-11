package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;

public class DatePickerDialog extends Dialog implements View.OnClickListener {

    private DatePicker datePicker;
    private long currentDate;
    private DialogButtonClickListener dialogButtonClickListener;

    public DatePickerDialog(Activity activity, long currentDate, DialogButtonClickListener dialogButtonClickListener) {
        super(activity);
        this.currentDate = currentDate;
        this.dialogButtonClickListener = dialogButtonClickListener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_date_picker);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        initViews();

        try {
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        findViewById(R.id.okButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);

        if (currentDate > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(currentDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            datePicker.updateDate(year, month, day);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.okButton:

                Calendar calendar = Calendar.getInstance();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                String previewDate = UIUtils.longToString(calendar.getTimeInMillis(), 4);

                dialogButtonClickListener.onOkButtonPressed(calendar.getTimeInMillis(), previewDate);
                dismiss();
                break;

            case R.id.cancelButton:
                dialogButtonClickListener.onCancelButtonPressed();
                dismiss();
                break;
        }
    }

    public interface DialogButtonClickListener {
        void onOkButtonPressed(long selectedDate, String selectedDateForPreview);

        void onCancelButtonPressed();
    }
}