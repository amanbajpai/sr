package com.ros.smartrocket.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;

public class DeadlineReminderDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = WithdrawTaskDialog.class.getSimpleName();
    private Context activity;
    private int taskId;

    public DeadlineReminderDialog(Context context, long deadlineTimeInMillis, String taskName, int surveyId,
                                  int taskId) {
        super(context);
        this.activity = context;
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

        /*Calendar calendar = Calendar.getInstance();
        long timeInMillis = deadlineTimeInMillis - calendar.getTimeInMillis();

        ((TextView) findViewById(R.id.dateTimeToDate)).setText(UIUtils.getTimeInDayHoursMinutes(activity, timeInMillis));*/

        String atDateTime = UIUtils.longToString(deadlineTimeInMillis, 3);
        ((TextView) findViewById(R.id.text)).setText(activity.getString(R.string.deadline_reminder_dialog_text,
                taskName, String.valueOf(surveyId), atDateTime));

        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.goToTaskButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
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
