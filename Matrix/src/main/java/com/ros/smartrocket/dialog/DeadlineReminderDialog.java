package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;

public class DeadlineReminderDialog extends Dialog implements View.OnClickListener {
    //private static final String TAG = WithdrawTaskDialog.class.getSimpleName();
    private Activity activity;
    private int taskId;

    public DeadlineReminderDialog(Activity activity, long deadlineTimeInMillis, int taskId) {
        super(activity);
        this.activity = activity;
        this.taskId = taskId;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.dialog_deadline_reminder);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Calendar calendar = Calendar.getInstance();

        long timeInMillis = deadlineTimeInMillis - calendar.getTimeInMillis();
        calendar.setTimeInMillis(timeInMillis);

        int days = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        String daysText = days + " " + activity.getResources().getQuantityString(R.plurals.day, days);
        String hoursText = hours + " " + activity.getResources().getQuantityString(R.plurals.hour, hours);
        String minutesText = minutes + " " + activity.getResources().getQuantityString(R.plurals.minute, minutes);

        String dateTimeToDate = daysText + " " + hoursText + " " + minutesText;
        ((TextView) findViewById(R.id.dateTimeToDate)).setText(dateTimeToDate);

        String atDateTime = UIUtils.longToString(deadlineTimeInMillis, 3);
        ((TextView) findViewById(R.id.atDateTime)).setText(atDateTime);

        findViewById(R.id.closeButton).setOnClickListener(this);
        findViewById(R.id.goToTaskButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeButton:
                dismiss();
                break;
            case R.id.goToTaskButton:
                dismiss();
                activity.startActivity(IntentUtils.getTaskDetailIntent(activity, taskId));
                break;
            default:
                break;
        }
    }

}
