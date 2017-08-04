package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.fragment.BaseQuestionFragment;
import com.ros.smartrocket.fragment.QuestionAudioFragment;
import com.ros.smartrocket.fragment.QuestionInstructionFragment;
import com.ros.smartrocket.fragment.QuestionMassAuditFragment;
import com.ros.smartrocket.fragment.QuestionMultipleChooseFragment;
import com.ros.smartrocket.fragment.QuestionNumberFragment;
import com.ros.smartrocket.fragment.QuestionOpenCommentFragment;
import com.ros.smartrocket.fragment.QuestionPhotoFragment;
import com.ros.smartrocket.fragment.QuestionQuitStatementFragment;
import com.ros.smartrocket.fragment.QuestionSingleChooseFragment;
import com.ros.smartrocket.fragment.QuestionVideoFragment;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.UserActionsLogger;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomTextView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestionsActivity extends BaseActivity implements NetworkOperationListenerInterface,
        OnAnswerSelectedListener, OnAnswerPageLoadingFinishedListener {

    private static final String TAG = QuestionsActivity.class.getSimpleName();
    private static final String KEY_IS_STARTED = "started";

    @BindView(R.id.mainProgressBar)
    ProgressBar mainProgressBar;
    @BindView(R.id.questionOf)
    CustomTextView questionOf;
    @BindView(R.id.questionOfLayout)
    LinearLayout questionOfLayout;
    @BindView(R.id.previousButton)
    CustomButton previousButton;
    @BindView(R.id.nextButton)
    CustomButton nextButton;
    @BindView(R.id.buttonsLayout)
    LinearLayout buttonsLayout;

    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    private Integer taskId;
    private Integer missionId;
    private Task task = new Task();
    private Wave wave;


    private AsyncQueryHandler handler;
    private List<Question> questions;
    private BaseQuestionFragment currentFragment;

    private int questionsToAnswerCount = 0;
    private boolean isRedo = false;

    private boolean isPreview = false;
    private boolean isAlreadyStarted;
    private boolean isDestroyed;

    private MenuItem idCardMenuItem;

    public boolean isPreview() {
        return isPreview;
    }

    public QuestionsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.v(TAG, "onCreate " + this);
        isDestroyed = false;
        setContentView(R.layout.activity_questions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            missionId = getIntent().getIntExtra(Keys.MISSION_ID, 0);
            isPreview = getIntent().getBooleanExtra(Keys.KEY_IS_PREVIEW, false);
        }

        handler = new DbHandler(getContentResolver());
        TasksBL.getTaskFromDBbyID(handler, taskId, missionId);
    }

    @Override
    public void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        L.v(TAG, "onRestoreInstanceState");
        isAlreadyStarted = savedInstanceState.getBoolean(KEY_IS_STARTED, false);
        if (isAlreadyStarted) {
            restoreFragment();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        L.v(TAG, "onSaveInstanceState");
        outState.putBoolean(KEY_IS_STARTED, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        L.v(TAG, "onDestroy " + this);
        isDestroyed = true;
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BaseQuestionFragment restoreFragment() {
        L.v(TAG, "restoreFragment");
        BaseQuestionFragment restoredCurrentFragment = (BaseQuestionFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentLayout);
        if (restoredCurrentFragment != null) {
            L.v(TAG, "restoreFragment not null " + restoredCurrentFragment);
            currentFragment = restoredCurrentFragment;
            currentFragment.setAnswerPageLoadingFinishedListener(this);
            currentFragment.setAnswerSelectedListener(this);
        }
        return restoredCurrentFragment;
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    task = TasksBL.convertCursorToTaskOrNull(cursor);

                    if (task != null) {
                        UserActionsLogger.logTaskStarted(task, isPreview);
                        setTitle(task.getName());
                        WavesBL.getWaveFromDB(handler, task.getWaveId());
                        UIUtils.setActionBarBackground(QuestionsActivity.this, task.getStatusId(), TasksBL.isPreClaimTask(task));

                        isRedo = TasksBL.getTaskStatusType(task.getStatusId()) == Task.TaskStatusId.RE_DO_TASK;

                        if (isRedo) {
                            nextButton.setBackgroundResource(R.drawable.button_red_selector);
                            previousButton.setBackgroundResource(R.drawable.button_red_selector);

                            int padding = UIUtils.getPxFromDp(QuestionsActivity.this, 10);
                            nextButton.setPadding(padding, padding, padding, padding);
                            previousButton.setPadding(padding, padding, padding, padding);

                            if (getIntent().getBooleanExtra(Keys.IS_REDO_REOPEN, false)) {
                                QuestionsBL.getQuestionsListFromDB(handler, task.getWaveId(), taskId, task.getMissionId(), false);
                            } else {
                                showProgressDialog(false);
                                apiFacade.getReDoQuestions(QuestionsActivity.this, task);
                            }
                        } else {
                            QuestionsBL.getQuestionsListFromDB(handler, task.getWaveId(), taskId, task.getMissionId(), false);
                        }
                    } else {
                        finishQuestionsActivity();
                    }
                    break;
                case WaveDbSchema.Query.TOKEN_QUERY:
                    wave = WavesBL.convertCursorToWave(cursor);
                    if (idCardMenuItem != null) {
                        idCardMenuItem.setVisible(wave.getIdCardStatus() == 1);
                    }
                    break;
                case QuestionDbSchema.Query.TOKEN_QUERY:
                    questions = QuestionsBL.convertCursorToQuestionList(cursor);

                    if (!questions.isEmpty()) {
                        Collections.sort(questions);
                        questionsToAnswerCount = QuestionsBL.getQuestionsToAnswerCount(questions);
                        int lastQuestionOrderId = preferencesManager.getLastNotAnsweredQuestionOrderId(task.getWaveId(),
                                taskId, task.getMissionId());
                        if (lastQuestionOrderId == 1) {
                            lastQuestionOrderId = QuestionsBL.getFirstOrderId(questions);
                        }
                        Question question = QuestionsBL.getQuestionWithCheckConditionByOrderId(questions, lastQuestionOrderId, isRedo);
                        startFragment(question);
                    } else {
                        showProgressDialog(false);
                        apiFacade.getQuestions(QuestionsActivity.this, task);
                    }

                    break;
                default:
                    break;
            }
        }
    }

    private void finishQuestionsActivity() {
        L.v(TAG, "!!!!! ===== FINISH ===== !!!!!");
        finish();
    }

    public void refreshMainProgress(int questionType, int currentQuestionOrderId) {
        mainProgressBar.setProgress((int) (((float) (currentQuestionOrderId - 1) / questionsToAnswerCount * 100)));
        if (questionType != Question.QuestionType.PHOTO.getTypeId()
                && questionType != Question.QuestionType.VIDEO.getTypeId()) {
            questionOfLayout.setVisibility(View.VISIBLE);
            questionOf.setText(getString(R.string.question_of, String.valueOf(getQuestionPos(currentQuestionOrderId)), String.valueOf(questionsToAnswerCount)));
        } else {
            questionOfLayout.setVisibility(View.GONE);
        }
    }

    private int getQuestionPos(int currentQuestionOrderId) {
        int position = 1;
        for (Question question : questions) {
            if (question.getOrderId() == currentQuestionOrderId) {
                return position;
            }
            position++;
        }
        return position;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.nextButton)
    public void startNextQuestionFragment() {
        isAlreadyStarted = false;
        if (currentFragment != null) {
            boolean shouldStart = isPreview ? isPreview : currentFragment.saveQuestion();
            if (shouldStart) {
                Question currentQuestion = currentFragment.getQuestion();
                if (currentQuestion != null) {
                    if (currentQuestion.getType() == Question.QuestionType.REJECT.getTypeId()) {
                        startValidationActivity();
                    } else {
                        L.v(TAG, "startNextQuestionFragment. currentQuestionOrderId:" + currentQuestion.getOrderId());
                        Question question = getQuestion(currentQuestion);

                        if (question != null && question.getType() != Question.QuestionType.VALIDATION.getTypeId()) {
                            if (!isPreview) {
                                preferencesManager.setLastNotAnsweredQuestionOrderId(task.getWaveId(), task.getId(),
                                        task.getMissionId(), question.getOrderId());
                            }
                            question.setPreviousQuestionOrderId(currentQuestion.getOrderId());

                            QuestionsBL.updatePreviousQuestionOrderId(question.getId(), question.getPreviousQuestionOrderId());

                            if (currentQuestion.getNextAnsweredQuestionId() != null &&
                                    currentQuestion.getNextAnsweredQuestionId() != 0 &&
                                    !question.getId().equals(currentQuestion.getNextAnsweredQuestionId())) {
                                for (Question tempQuestion : questions) {
                                    if (tempQuestion.getOrderId() > currentQuestion.getOrderId()) {
                                        AnswersBL.clearAnswersInDB(task.getId(), task.getMissionId(), tempQuestion.getId());
                                    }
                                }
                            }
                            currentQuestion.setNextAnsweredQuestionId(question.getId());
                            QuestionsBL.updateNextAnsweredQuestionId(currentQuestion.getId(), question.getId());
                            startFragment(question);
                        } else {
                            startValidationActivity();
                        }
                    }
                }
            }
        }
    }

    private Question getQuestion(Question currentQuestion) {
        int nextQuestionOrderId = AnswersBL.getNextQuestionOrderId(currentQuestion, questions);
        return QuestionsBL.getQuestionWithCheckConditionByOrderId(questions, nextQuestionOrderId, isRedo);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.previousButton)
    public void startPreviousQuestionFragment() {
        if (currentFragment != null) {
            Question currentQuestion = currentFragment.getQuestion();
            if (currentQuestion != null) {
                L.v(TAG, "startPreviousQuestionFragment. currentQuestionOrderId:" + currentQuestion.getOrderId());
                UserActionsLogger.logPrevQuestionOpened(currentQuestion, isPreview);
                int previousQuestionOrderId = (currentQuestion.getPreviousQuestionOrderId() != null &&
                        currentQuestion.getPreviousQuestionOrderId() != 0) ? currentQuestion.getPreviousQuestionOrderId() : 1;
                if (!isPreview) {
                    preferencesManager.setLastNotAnsweredQuestionOrderId(task.getWaveId(), task.getId(),
                            task.getMissionId(), previousQuestionOrderId);
                }
                Question question = QuestionsBL.getQuestionWithCheckConditionByOrderId(questions, previousQuestionOrderId, isRedo);
                startFragment(question);
            }
        }
        isAlreadyStarted = false;
    }

    public void startValidationActivity() {
        TasksBL.updateTaskStatusId(taskId, missionId, Task.TaskStatusId.SCHEDULED.getStatusId());
        if (task != null) {
            UserActionsLogger.logTaskOnValidation(task);
        }
        startActivity(IntentUtils.getTaskValidationIntent(this, taskId, missionId, true, isRedo));
        finishQuestionsActivity();
    }

    public void startFragment(Question question) {
        L.v(TAG, "startFragment." + this + " Destroyed " + isDestroyed);
        if (isDestroyed) {
            return;
        }

        if (question != null) {
            UserActionsLogger.logQuestionOpened(question, isPreview);
            L.v(TAG, "startFragment. orderId:" + question.getOrderId());
            buttonsLayout.setVisibility(View.INVISIBLE);
            refreshMainProgress(question.getType(), question.getOrderId());

            //int nextQuestionOrderId = AnswersBL.getNextQuestionOrderId(question);

            if (question.getType() == Question.QuestionType.VALIDATION.getTypeId()) {
                startValidationActivity();
                return;
            }

            if (question.getShowBackButton() && question.getPreviousQuestionOrderId() != 0) {
                previousButton.setVisibility(View.VISIBLE);
            } else {
                previousButton.setVisibility(View.INVISIBLE);
            }

            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putSerializable(Keys.QUESTION, question);

            switch (QuestionsBL.getQuestionType(question.getType())) {
                case MULTIPLE_CHOICE:
                    currentFragment = new QuestionMultipleChooseFragment();
                    break;
                case SINGLE_CHOICE:
                    currentFragment = new QuestionSingleChooseFragment();
                    break;
                case PHOTO:
                    currentFragment = new QuestionPhotoFragment();
                    break;
                case OPEN_COMMENT:
                    currentFragment = new QuestionOpenCommentFragment();
                    break;
                case VIDEO:
                    currentFragment = new QuestionVideoFragment();
                    break;
                case NUMBER:
                    currentFragment = new QuestionNumberFragment();
                    break;
                case INSTRUCTION:
                    currentFragment = new QuestionInstructionFragment();
                    break;
                case REJECT:
                    AnswersBL.updateQuitStatmentAnswer(question);
                    currentFragment = new QuestionQuitStatementFragment();
                    break;
                case MASS_AUDIT:
                    currentFragment = new QuestionMassAuditFragment();
                    fragmentBundle.putBoolean(QuestionMassAuditFragment.KEY_IS_REDO, isRedo);
                    fragmentBundle.putBoolean(QuestionMassAuditFragment.KEY_IS_PREVIEW, isPreview);
                    break;
                case AUDIO:
                    currentFragment = new QuestionAudioFragment();
                    break;
                default:
                    break;
            }

            if (currentFragment != null) {
                currentFragment.setAnswerPageLoadingFinishedListener(this);
                currentFragment.setAnswerSelectedListener(this);
                currentFragment.setArguments(fragmentBundle);

                try {
                    FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
                    if (!isAlreadyStarted) {
                        if (!isFinishing()) {
                            t.replace(R.id.contentLayout, currentFragment).commitAllowingStateLoss();
                        }
                    } else {
                        BaseQuestionFragment restoredCurrentFragment = restoreFragment();
                        if (restoredCurrentFragment != null) {
                            onAnswerPageLoadingFinished();
                        } else {
                            if (!isFinishing()) {
                                t.replace(R.id.contentLayout, currentFragment).commitAllowingStateLoss();
                            }

                        }
                    }
                } catch (Exception e) {
                    L.e(TAG, "Error replace question type fragment", e);
                    finishQuestionsActivity();
                }
            }
        } else {
            startValidationActivity();
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_QUESTIONS_OPERATION_TAG.equals(operation.getTag())
                    || Keys.GET_REDO_QUESTION_OPERATION_TAG.equals(operation.getTag())) {

                QuestionsBL.getQuestionsListFromDB(handler, task.getWaveId(), taskId, task.getMissionId(), false);
            } else if (Keys.REJECT_TASK_OPERATION_TAG.equals(operation.getTag())) {
                int lastQuestionOrderId = preferencesManager.getLastNotAnsweredQuestionOrderId(task.getWaveId(),
                        taskId, task.getMissionId());
                Question question = QuestionsBL.getQuestionWithCheckConditionByOrderId(questions, lastQuestionOrderId, isRedo);

                startActivity(IntentUtils.getQuitQuestionIntent(this, question));
                finishQuestionsActivity();
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
        }
        dismissProgressDialog();
    }

    @Override
    public void onAnswerSelected(Boolean selected, int questionId) {
        if (isPreview) {
            Question nextQuestion = currentFragment == null ? null : getQuestion(currentFragment.getQuestion());
            if (nextQuestion == null || nextQuestion.getType() == Question.QuestionType.VALIDATION.getTypeId() || currentFragment.getQuestion().getType() == Question.QuestionType.REJECT.getTypeId()) {
                nextButton.setEnabled(false);
            } else {
                nextButton.setEnabled(true);
            }
        } else {
            nextButton.setEnabled(selected);
        }
    }

    @Override
    public void onAnswerPageLoadingFinished() {
        buttonsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishQuestionsActivity();
                return true;
            case R.id.quiteTask:
                if (isPreview) {
                    finishQuestionsActivity();
                } else if (task.getWaveId() != null && task.getId() != null) {
                    DialogUtils.showQuiteTaskDialog(this, task.getWaveId(), task.getId(), task.getMissionId());
                }
                return true;
            case R.id.idCardMenuItem:
                LocaleUtils.setCurrentLanguage();
                IdCardActivity.launch(this, wave);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_questions, menu);
        idCardMenuItem = menu.findItem(R.id.idCardMenuItem);
        if (wave != null) {
            idCardMenuItem.setVisible(wave.getIdCardStatus() == 1);
        }

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        TextView title = (TextView) view.findViewById(R.id.titleTextView);
        if (isPreview) {
            title.setTextColor(getResources().getColor(R.color.red));
            title.setText(getString(R.string.preview_mode));
        } else {
            title.setText(R.string.question_title);
        }
        return true;
    }
}