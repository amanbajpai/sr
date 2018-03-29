package com.ros.smartrocket.presentation.notification;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.MatrixContextWrapper;
import com.ros.smartrocket.utils.eventbus.CloseNotificationAction;
import com.ros.smartrocket.utils.eventbus.PhotoEvent;
import com.ros.smartrocket.utils.eventbus.QuitQuestionFlowAction;

import java.util.Locale;

import de.greenrobot.event.EventBus;

import static com.ros.smartrocket.presentation.notification.NotificationActivity.NotificationType.mission_deadline;
import static com.ros.smartrocket.presentation.notification.NotificationActivity.NotificationType.mission_expired;

public class NotificationActivity extends Activity implements OnClickListener {

    private CharSequence title;
    private CharSequence text;

    private int taskId;
    private int missionId;
    private int notificationTypeId;
    private int taskStatusId;
    private int titleBackgroundColorResId;
    private int titleIconResId;

    private int leftButtonResId;
    private int rightButtonResId;
    private boolean showLeftButton;

    public enum NotificationType {
        none(0), mission_expired(1), mission_approved(2), mission_redo(3), mission_rejected(4), mission_deadline(5);

        private int id;

        NotificationType(int typeId) {
            this.id = typeId;
        }

        public int getId() {
            return id;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            missionId = getIntent().getIntExtra(Keys.MISSION_ID, 0);
            notificationTypeId = getIntent().getIntExtra(Keys.NOTIFICATION_TYPE_ID, 0);
            taskStatusId = getIntent().getIntExtra(Keys.TASK_STATUS_ID, 0);
            titleBackgroundColorResId = getIntent().getIntExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, 0);
            titleIconResId = getIntent().getIntExtra(Keys.TITLE_ICON_RES_ID, 0);
            title = getIntent().getStringExtra(Keys.NOTIFICATION_TITLE);
            text = getIntent().getCharSequenceExtra(Keys.NOTIFICATION_TEXT);
            leftButtonResId = getIntent().getIntExtra(Keys.LEFT_BUTTON_RES_ID, 0);
            rightButtonResId = getIntent().getIntExtra(Keys.RIGHT_BUTTON_RES_ID, 0);
            showLeftButton = getIntent().getBooleanExtra(Keys.SHOW_LEFT_BUTTON, false);
        }

        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(title);

        if (titleBackgroundColorResId != 0) {
            titleTextView.setBackgroundColor(getResources().getColor(titleBackgroundColorResId));
        }

        if (titleIconResId != 0) {
            if (LocaleUtils.isRtL()) {
                titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, titleIconResId, 0);
            } else {
                titleTextView.setCompoundDrawablesWithIntrinsicBounds(titleIconResId, 0, 0, 0);
            }
        }

        TextView message = findViewById(R.id.text);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setText(text);

        TextView leftButton = findViewById(R.id.leftButton);
        TextView rightButton = findViewById(R.id.rightButton);

        if (showLeftButton && leftButtonResId != 0) {
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
        if (getNotificationType(notificationTypeId) == mission_expired)
            EventBus.getDefault().post(new CloseNotificationAction(mission_deadline, taskId));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
                Log.e("Expiry", "Event sent");
                EventBus.getDefault().post(new QuitQuestionFlowAction(taskId, missionId));
                finish();
                break;
            case mission_approved:
            case mission_rejected:
                finish();
                break;
            case mission_redo:
                Task task = TasksBL.convertCursorToTaskOrNull(TasksBL.getTaskCursorFromDBbyID(taskId, missionId));
                if (task != null) {
                    if (TasksBL.getTaskStatusType(task.getStatusId()) == Task.TaskStatusId.RE_DO_TASK) {
                        startActivity(IntentUtils.getQuestionsIntent(this, taskId, missionId));
                    } else {
                        startActivity(IntentUtils.getTaskDetailIntent(this, taskId, missionId, task.getStatusId(),
                                TasksBL.isPreClaimTask(task)));
                    }
                }
                finish();
                break;
            case mission_deadline:
                switch (TasksBL.getTaskStatusType(taskStatusId)) {
                    case SCHEDULED:
                        startActivity(IntentUtils.getTaskValidationIntent(this, taskId, missionId, false, false));
                        break;
                    case RE_DO_TASK:
                        startActivity(IntentUtils.getQuestionsIntent(this, taskId, missionId));
                        break;
                    default:
                        Task taskToOpen = TasksBL.convertCursorToTaskOrNull(TasksBL.getTaskCursorFromDBbyID(taskId, missionId));
                        if (taskToOpen != null) {
                            startActivity(IntentUtils.getTaskDetailIntent(this, taskId, missionId,
                                    taskToOpen.getStatusId(), TasksBL.isPreClaimTask(taskToOpen)));
                        }
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

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleUtils.setCurrentLanguage();
        Locale newLocale = LocaleUtils.getCurrentLocale();
        Context context = MatrixContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(CloseNotificationAction event) {
        if (getNotificationType(notificationTypeId) == event.getType()
                && event.getTaskId() != null
                && event.getTaskId().equals(taskId))
            finish();

    }
}