package com.ros.smartrocket.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.utils.IntentUtils;

public class NotificationActivity extends Activity implements OnClickListener {
    //private final static String TAG = NotificationActivity.class.getSimpleName();

    private CharSequence title;
    private CharSequence text;

    private int taskId;
    private int notificationTypeId;
    private int taskStatusId;
    private int titleBackgroundColorResId;
    private int titleIconResId;

    private int leftButtonResId;
    private int rightButtonResId;

    public enum NotificationType {
        none(0), mission_expired(1), mission_approved(2), mission_redo(3), mission_rejected(4), mission_deadline(5);

        private int id;

        private NotificationType(int typeId) {
            this.id = typeId;
        }

        public int getId() {
            return id;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //UIUtils.setMaxActivityWidth(this, Config.MAX_ACTIVITY_WIDTH);

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            notificationTypeId = getIntent().getIntExtra(Keys.NOTIFICATION_TYPE_ID, 0);
            taskStatusId = getIntent().getIntExtra(Keys.TASK_STATUS_ID, 0);
            titleBackgroundColorResId = getIntent().getIntExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, 0);
            titleIconResId = getIntent().getIntExtra(Keys.TITLE_ICON_RES_ID, 0);
            title = getIntent().getStringExtra(Keys.NOTIFICATION_TITLE);
            text = getIntent().getCharSequenceExtra(Keys.NOTIFICATION_TEXT);
            leftButtonResId = getIntent().getIntExtra(Keys.LEFT_BUTTON_RES_ID, 0);
            rightButtonResId = getIntent().getIntExtra(Keys.RIGHT_BUTTON_RES_ID, 0);
        }

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);

        if (titleBackgroundColorResId != 0) {
            titleTextView.setBackgroundColor(getResources().getColor(titleBackgroundColorResId));
        }

        if (titleIconResId != 0) {
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(titleIconResId, 0, 0, 0);
        }

        ((TextView) findViewById(R.id.text)).setText(text);

        TextView leftButton = (TextView) findViewById(R.id.leftButton);
        TextView rightButton = (TextView) findViewById(R.id.rightButton);

        if (leftButtonResId != 0) {
            leftButton.setText(leftButtonResId);
            leftButton.setOnClickListener(this);
        } else {
            leftButton.setVisibility(View.GONE);
        }

        if (rightButtonResId != 0) {
            rightButton.setText(rightButtonResId);
            rightButton.setOnClickListener(this);
        } else {
            rightButton.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton:
                finish();
                break;
            case R.id.rightButton:
                clickByRightButton();
                break;
            default:
                break;
        }
    }

    public void clickByRightButton() {
        switch (getNotificationType(notificationTypeId)) {
            case mission_expired:
            case mission_approved:
            case mission_rejected:
                finish();
                break;
            case mission_redo:
                startActivity(IntentUtils.getQuestionsIntent(this, taskId));
                finish();
                break;
            case mission_deadline:
                switch (TasksBL.getTaskStatusType(taskStatusId)) {
                    case SCHEDULED:
                        startActivity(IntentUtils.getTaskValidationIntent(this, taskId, false, false));
                        break;
                    case RE_DO_TASK:
                        startActivity(IntentUtils.getQuestionsIntent(this, taskId));
                        break;
                    default:
                        startActivity(IntentUtils.getTaskDetailIntent(this, taskId));
                        break;
                }
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    public static NotificationType getNotificationType(int typeId) {
        NotificationType result = NotificationType.none;
        for (NotificationType type : NotificationType.values()) {
            if (type.getId() == typeId) {
                result = type;
                break;
            }
        }
        return result;
    }
}