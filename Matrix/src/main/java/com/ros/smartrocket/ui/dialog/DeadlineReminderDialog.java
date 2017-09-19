package com.ros.smartrocket.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

public class DeadlineReminderDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = WithdrawTaskDialog.class.getSimpleName();
    private Context activity;
    private int taskId;
    private int missionId;

    public DeadlineReminderDialog(Context context, long deadlineTimeInMillis, String taskName, int waveId,
                                  int taskId, int missionId) {
        super(context);
        this.activity = context;
        this.taskId = taskId;
        this.missionId = missionId;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_deadline_reminder);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        String atDateTime = UIUtils.longToString(deadlineTimeInMillis, 3);
        ((TextView) findViewById(R.id.text)).setText(activity.getString(R.string.deadline_reminder_dialog_text,
                taskName, String.valueOf(waveId), atDateTime));

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
                Task task = TasksBL.convertCursorToTaskOrNull(TasksBL.getTaskCursorFromDBbyID(taskId, missionId));
                if (task != null) {
                    activity.startActivity(IntentUtils.getTaskDetailIntent(activity, taskId, missionId,
                            task.getStatusId(), TasksBL.isPreClaimTask(task)));
                }
                break;
            default:
                break;
        }
    }

}
