package com.ros.smartrocket.presentation.question.main;

import android.content.Intent;
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

import com.annimon.stream.Stream;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.db.entity.question.QuestionType;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.Wave;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.media.IdCardActivity;
import com.ros.smartrocket.presentation.question.audio.QuestionAudioFragment;
import com.ros.smartrocket.presentation.question.audit.QuestionMassAuditFragment;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;
import com.ros.smartrocket.presentation.question.choose.multiple.QuestionMultipleChooseFragment;
import com.ros.smartrocket.presentation.question.choose.single.QuestionSingleChooseFragment;
import com.ros.smartrocket.presentation.question.comment.QuestionCommentFragment;
import com.ros.smartrocket.presentation.question.instruction.QuestionInstructionFragment;
import com.ros.smartrocket.presentation.question.number.QuestionNumberFragment;
import com.ros.smartrocket.presentation.question.photo.QuestionPhotoFragment;
import com.ros.smartrocket.presentation.question.quit.QuestionQuitStatementFragment;
import com.ros.smartrocket.presentation.question.video.QuestionVideoFragment;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.UserActionsLogger;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestionsActivity extends BaseActivity implements OnAnswerSelectedListener,
        OnAnswerPageLoadingFinishedListener, QuestionMvpView {

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

    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private int taskId;
    private int missionId;
    private Task task = new Task();
    private Wave wave;
    private List<Question> questions;
    private BaseQuestionFragment currentFragment;
    private int questionsToAnswerCount = 0;
    private boolean isRedo = false;
    private boolean isPreview = false;
    private boolean isAlreadyStarted;
    private boolean isDestroyed;
    private MenuItem idCardMenuItem;
    private QuestionMvpPresenter<QuestionMvpView> presenter;

    public boolean isPreview() {
        return isPreview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDestroyed = false;
        setContentView(R.layout.activity_questions);
        setHomeAsUp();
        ButterKnife.bind(this);
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        presenter = new QuestionPresenter<>();
        presenter.attachView(this);
        handleArgs();
        presenter.getTaskFromDBbyID(taskId, missionId);
    }

    private void handleArgs() {
        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            missionId = getIntent().getIntExtra(Keys.MISSION_ID, 0);
            isPreview = getIntent().getBooleanExtra(Keys.KEY_IS_PREVIEW, false);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isAlreadyStarted = savedInstanceState.getBoolean(KEY_IS_STARTED, false);
        if (isAlreadyStarted)
            restoreFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_STARTED, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BaseQuestionFragment restoreFragment() {
        BaseQuestionFragment restoredCurrentFragment =
                (BaseQuestionFragment) getSupportFragmentManager().findFragmentById(R.id.contentLayout);
        if (restoredCurrentFragment != null) {
            currentFragment = restoredCurrentFragment;
            currentFragment.setAnswerPageLoadingFinishedListener(this);
            currentFragment.setAnswerSelectedListener(this);
        }
        return restoredCurrentFragment;
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
    }

    @Override
    public void onTaskLoadedFromDb(Task taskFromDb) {
        task = taskFromDb;
        if (task.getId() != null && task.getId() != 0) {
            UserActionsLogger.logTaskStarted(task, isPreview);
            setTitle(task.getName());
            presenter.getWaveFromDB(task.getWaveId());
            UIUtils.setActionBarBackground(QuestionsActivity.this, task.getStatusId(), TasksBL.isPreClaimTask(task));
            isRedo = TasksBL.getTaskStatusType(task.getStatusId()) == Task.TaskStatusId.RE_DO_TASK;
            if (isRedo) {
                nextButton.setBackgroundResource(R.drawable.button_red_selector);
                previousButton.setBackgroundResource(R.drawable.button_red_selector);
                int padding = UIUtils.getPxFromDp(QuestionsActivity.this, 10);
                nextButton.setPadding(padding, padding, padding, padding);
                previousButton.setPadding(padding, padding, padding, padding);

                if (getIntent().getBooleanExtra(Keys.IS_REDO_REOPEN, false)) {
                    presenter.getQuestionsListFromDB(task);
                } else {
                    presenter.loadReDoQuestions(task);
                }
            } else {
                presenter.getQuestionsListFromDB(task);
            }
        } else {
            finishQuestionsActivity();
        }
    }

    @Override
    public void onWaveLoadingComplete(Wave waveFromDb) {
        wave = waveFromDb;
        if (idCardMenuItem != null) idCardMenuItem.setVisible(wave.getIdCardStatus() == 1);
    }

    @Override
    public void onQuestionsLoadingComplete(List<Question> questionsFromDb) {
        questions = questionsFromDb;
        if (!questions.isEmpty()) {
            Collections.sort(questions);
            questionsToAnswerCount = QuestionsBL.getQuestionsToAnswerCount(questions);
            int lastQuestionOrderId = preferencesManager.getLastNotAnsweredQuestionOrderId(task.getWaveId(),
                    taskId, task.getMissionId());
            if (lastQuestionOrderId == 1)
                lastQuestionOrderId = QuestionsBL.getFirstOrderId(questions);
            Question question = QuestionsBL.getQuestionWithCheckConditionByOrderId(questions, lastQuestionOrderId, isRedo);
            startFragment(question);
        } else {
            presenter.loadQuestions(task);
        }
    }

    @Override
    public void onQuestionsLoaded() {
        presenter.getQuestionsListFromDB(task);
    }

    private void finishQuestionsActivity() {
        finish();
    }

    public void refreshMainProgress(int questionType, int currentQuestionOrderId) {
        mainProgressBar.setProgress((int) (((float) (currentQuestionOrderId - 1) / questionsToAnswerCount * 100)));
        if (questionType != QuestionType.PHOTO.getTypeId() && questionType != QuestionType.VIDEO.getTypeId()) {
            questionOfLayout.setVisibility(View.VISIBLE);
            questionOf.setText(getString(R.string.question_of, String.valueOf(getQuestionPos(currentQuestionOrderId)), String.valueOf(questionsToAnswerCount)));
        } else {
            questionOfLayout.setVisibility(View.GONE);
        }
    }

    private int getQuestionPos(int currentQuestionOrderId) {
        int position = 1;
        for (Question question : questions) {
            if (question.getOrderId() == currentQuestionOrderId)
                return position;
            position++;
        }
        return position;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.nextButton)
    public void startNextQuestionFragment() {
        isAlreadyStarted = false;
        if (currentFragment != null) {
            boolean shouldStart = isPreview || currentFragment.saveQuestion();
            if (shouldStart) {
                Question currentQuestion = currentFragment.getQuestion();
                if (currentQuestion != null) {
                    if (currentQuestion.getType() == QuestionType.REJECT.getTypeId()) {
                        startValidationActivity();
                    } else {
                        Question question = getQuestion(currentQuestion);
                        if (question != null && question.getType() != QuestionType.VALIDATION.getTypeId()) {
                            if (!isPreview) {
                                preferencesManager.setLastNotAnsweredQuestionOrderId(task.getWaveId(), task.getId(),
                                        task.getMissionId(), question.getOrderId());
                            }
                            question.setPreviousQuestionOrderId(currentQuestion.getOrderId());
                            QuestionsBL.updatePreviousQuestionOrderId(question.getId(), question.getPreviousQuestionOrderId());

                            if (currentQuestion.getNextAnsweredQuestionId() != null &&
                                    currentQuestion.getNextAnsweredQuestionId() != 0 &&
                                    !question.getId().equals(currentQuestion.getNextAnsweredQuestionId())) {
                                Stream.of(questions)
                                        .filter(q -> q.getOrderId() > currentQuestion.getOrderId())
                                        .forEach(q ->
                                                AnswersBL.clearAnswersInDB(task.getId(), task.getMissionId(), q.getId()));
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
        if (task != null)
            UserActionsLogger.logTaskOnValidation(task);
        startActivity(IntentUtils.getTaskValidationIntent(this, taskId, missionId, true, isRedo));
        finishQuestionsActivity();
    }

    public void startFragment(Question question) {
        if (isDestroyed) return;

        if (question != null) {
            UserActionsLogger.logQuestionOpened(question, isPreview);
            L.v(TAG, "startFragment. orderId:" + question.getOrderId());
            buttonsLayout.setVisibility(View.INVISIBLE);
            refreshMainProgress(question.getType(), question.getOrderId());

            if (question.getType() == QuestionType.VALIDATION.getTypeId()) {
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
            fragmentBundle.putSerializable(Keys.IS_REDO, isRedo);
            fragmentBundle.putSerializable(Keys.IS_PREVIEW, isPreview);

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
                    currentFragment = new QuestionCommentFragment();
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
                    if (!isPreview())
                        AnswersBL.updateQuitStatmentAnswer(question);
                    currentFragment = new QuestionQuitStatementFragment();
                    break;
                case MASS_AUDIT:
                    currentFragment = new QuestionMassAuditFragment();
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
    public void onAnswerSelected(Boolean selected, int questionId) {
        if (isPreview) {
            Question nextQuestion = currentFragment == null ? null : getQuestion(currentFragment.getQuestion());
            if (nextQuestion == null || nextQuestion.getType() == QuestionType.VALIDATION.getTypeId() || currentFragment.getQuestion().getType() == QuestionType.REJECT.getTypeId()) {
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
        if (wave != null) idCardMenuItem.setVisible(wave.getIdCardStatus() == 1);
        setUpActionBar();
        return true;
    }

    private void setUpActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
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
        }
    }
}