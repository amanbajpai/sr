package com.ros.smartrocket.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;

public class DatePickerDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = DatePickerDialog.class.getSimpleName();
    private DatePicker datePicker;
    private Long currentDate;
    private DialogButtonClickListener dialogButtonClickListener;

    public DatePickerDialog(Activity activity, Long currentDate, DialogButtonClickListener dialogButtonClickListener) {
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
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
    }

    @TargetApi(11)
    private void initViews() {
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        if (currentDate != null) {
            Calendar calendar = getMaxDateCalendar();
            calendar.setTimeInMillis(currentDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datePicker.updateDate(year, month, day);
        } else {
            datePicker.updateDate(1990, 0, 1);
        }

        findViewById(R.id.okButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);
    }

    public Calendar getMaxDateCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 18);
        return cal;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.okButton:

                Calendar calendar = Calendar.getInstance();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                long selectedTimeInMillis = calendar.getTimeInMillis();

                if (selectedTimeInMillis <= getMaxDateCalendar().getTimeInMillis()) {
                    String previewDate = UIUtils.longToString(selectedTimeInMillis, 1);

                    dialogButtonClickListener.onOkButtonPressed(selectedTimeInMillis, previewDate);
                    dismiss();
                } else {
                    DialogUtils.showAgeVerificationDialog(getContext());
                }
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