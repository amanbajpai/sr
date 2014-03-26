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

    @TargetApi(11)
    private void initViews() {
        final Calendar maxDateCalendar = getMaxDateCalendar();
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            datePicker.setMaxDate(maxDateCalendar.getTimeInMillis());
        } else {
            final int maxYear = maxDateCalendar.get(Calendar.YEAR);
            final int maxMonth = maxDateCalendar.get(Calendar.MONTH);
            final int maxDay = maxDateCalendar.get(Calendar.DAY_OF_MONTH);

            datePicker.init(maxYear, maxMonth, maxDay,
                    new DatePicker.OnDateChangedListener() {

                        public void onDateChanged(DatePicker view, int year,
                                                  int month, int day) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, month, day);

                            if (newDate.after(maxDateCalendar)) {
                                view.init(maxYear, maxMonth, maxDay, this);
                            }
                        }
                    });
        }

        if (currentDate > 0) {
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