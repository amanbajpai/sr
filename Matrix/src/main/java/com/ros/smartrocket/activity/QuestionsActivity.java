package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.fragment.*;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.*;

import java.util.List;

public class QuestionsActivity extends BaseActivity implements NetworkOperationListenerInterface,
        View.OnClickListener, OnAnswerSelectedListener, OnAnswerPageLoadingFinishedListener {
    private static final String TAG = QuestionsActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    private Integer taskId;
    private Task task = new Task();

    private ProgressBar mainProgressBar;
    private TextView questionOf;
    private LinearLayout questionOfLayout;

    private LinearLayout buttonsLayout;
    private Button previousButton;
    private Button nextButton;

    private AsyncQueryHandler handler;
    private List<Question> questions;
    private BaseQuestionFragment currentFragment;

    private int questionsToAnswerCount = 0;
    private boolean isRedo = false;

    public QuestionsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_questions);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
        }

        handler = new DbHandler(getContentResolver());

        mainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
        questionOfLayout = (LinearLayout) findViewById(R.id.questionOfLayout);
        questionOf = (TextView) findViewById(R.id.questionOf);

        buttonsLayout = (LinearLayout) findViewById(R.id.buttonsLayout);

        previousButton = (Button) findViewById(R.id.previousButton);
        previousButton.setOnClickListener(this);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        TasksBL.getTaskFromDBbyID(handler, taskId);
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
                        setTitle(task.getName());

                        UIUtils.setActionBarBackground(QuestionsActivity.this, task.getStatusId(), TasksBL.isPreClaimTask(task));

                        isRedo = TasksBL.getTaskStatusType(task.getStatusId()) == Task.TaskStatusId.RE_DO_TASK;

                        if (isRedo) {
                            nextButton.setBackgroundResource(R.drawable.button_red_selector);
                            previousButton.setBackgroundResource(R.drawable.button_red_selector);

                            int padding = UIUtils.getPxFromDp(QuestionsActivity.this, 10);
                            nextButton.setPadding(padding, padding, padding, padding);
                            previousButton.setPadding(padding, padding, padding, padding);

                            apiFacade.getReDoQuestions(QuestionsActivity.this, task.getWaveId(), taskId);
                        } else {
                            QuestionsBL.getQuestionsListFromDB(handler, task.getWaveId(), taskId);
                        }
                    } else {
                        finish();
                    }
                    break;
                case QuestionDbSchema.Query.TOKEN_QUERY:
                    questions = QuestionsBL.convertCursorToQuestionList(cursor);

                    if (!questions.isEmpty()) {
                        questionsToAnswerCount = QuestionsBL.getQuestionsToAnswerCount(questions);
                        int lastQuestionOrderId = preferencesManager.getLastNotAnsweredQuestionOrderId(task.getWaveId(),
                                taskId);

                        Question question = QuestionsBL.getQuestionWithCheckConditionByOrderId(questions, lastQuestionOrderId);
                        startFragment(question);
                    } else {
                        setSupportProgressBarIndeterminateVisibility(true);
                        apiFacade.getQuestions(QuestionsActivity.this, task.getWaveId(), taskId);
                    }

                    break;
                default:
                    break;
            }
        }
    }

    public void refreshMainProgress(int questionType, int currentQuestionOrderId) {
        mainProgressBar.setProgress((int) (((float) (currentQuestionOrderId - 1) / questionsToAnswerCount * 100)));
        if (questionType != Question.QuestionType.PHOTO.getTypeId()
                && questionType != Question.QuestionType.VIDEO.getTypeId()) {
            questionOfLayout.setVisibility(View.VISIBLE);
            questionOf.setText(getString(R.string.question_of, currentQuestionOrderId, questionsToAnswerCount));
        } else {
            questionOfLayout.setVisibility(View.GONE);
        }

    }

    public void startNextQuestionFragment() {
        if (currentFragment != null) {
            Question currentQuestion = currentFragment.getQuestion();
            if (currentQuestion != null) {
                L.i(TAG, "startNextQuestionFragment. currentQuestionOrderId:" + currentQuestion.getOrderId());

                int nextQuestionOrderId = AnswersBL.getNextQuestionOrderId(currentQuestion);

                Question question = QuestionsBL.getQuestionWithCheckConditionByOrderId(questions, nextQuestionOrderId);
                if (question != null && question.getType() != Question.QuestionType.VALIDATION.getTypeId()) {
                    preferencesManager.setLastNotAnsweredQuestionOrderId(task.getWaveId(), taskId, question.getOrderId());
                    question.setPreviousQuestionOrderId(currentQuestion.getOrderId());

                    QuestionsBL.updatePreviousQuestionOrderId(question.getId(), question.getPreviousQuestionOrderId());

                    if (currentQuestion.getNextAnsweredQuestionId() != null &&
                            !question.getId().equals(currentQuestion.getNextAnsweredQuestionId())) {
                        for (Question tempQuestion : questions) {
                            if (tempQuestion.getOrderId() > currentQuestion.getOrderId()) {
                                AnswersBL.clearAnswersInDB(taskId, tempQuestion.getId());
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

    public void startPreviousQuestionFragment() {
        if (currentFragment != null) {
            Question currentQuestion = currentFragment.getQuestion();
            if (currentQuestion != null) {
                L.i(TAG, "startNextQuestionFragment. currentQuestionOrderId:" + currentQuestion.getOrderId());

                int previousQuestionOrderId = currentQuestion.getPreviousQuestionOrderId() != 0 ? currentQuestion
                        .getPreviousQuestionOrderId() : 1;
                preferencesManager.setLastNotAnsweredQuestionOrderId(task.getWaveId(), taskId, previousQuestionOrderId);

                Question question = QuestionsBL.getQuestionWithCheckConditionByOrderId(questions, previousQuestionOrderId);
                startFragment(question);
            }
        }
    }

    public void startValidationActivity() {
        TasksBL.updateTaskStatusId(taskId, Task.TaskStatusId.SCHEDULED.getStatusId());

        startActivity(IntentUtils.getTaskValidationIntent(this, taskId, true, isRedo));
        finish();
    }

    public void startFragment(Question question) {
        if (question != null) {
            L.i(TAG, "startFragment. orderId:" + question.getOrderId());
            buttonsLayout.setVisibility(View.INVISIBLE);
            refreshMainProgress(question.getType(), question.getOrderId());

            //int nextQuestionOrderId = AnswersBL.getNextQuestionOrderId(question);

            if (question.getType() == Question.QuestionType.VALIDATION.getTypeId()) {
                startValidationActivity();
                return;

            } else if (question.getType() == Question.QuestionType.REJECT.getTypeId()) {
                if (UIUtils.isOnline(this)) {
                    setSupportProgressBarIndeterminateVisibility(true);
                    apiFacade.rejectTask(this, question.getTaskId());
                } else {
                    UIUtils.showSimpleToast(this, getString(R.string.no_internet));
                }
                return;
            }

            if (question.getShowBackButton() && question.getPreviousQuestionOrderId() != 0) {
                previousButton.setVisibility(View.VISIBLE);
            } else {
                previousButton.setVisibility(View.INVISIBLE);
            }

            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putSerializable(Keys.QUESTION, question);

            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            switch (QuestionsBL.getQuestionType(question.getType())) {
                case MULTIPLE_CHOICE:
                    currentFragment = new QuestionType1Fragment();
                    break;
                case SINGLE_CHOICE:
                    currentFragment = new QuestionType2Fragment();
                    break;
                case PHOTO:
                    currentFragment = new QuestionType7Fragment();
                    break;
                case OPEN_COMMENT:
                    currentFragment = new QuestionType4Fragment();
                    break;
                case VIDEO:
                    currentFragment = new QuestionType5Fragment();
                    break;
                case NUMBER:
                    currentFragment = new QuestionType6Fragment();
                    break;
                case INSTRUCTION:
                    currentFragment = new QuestionType8Fragment();
                    break;
                default:
                    break;
            }

            BaseQuestionFragment prevFragment = (BaseQuestionFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.contentLayout);

            if (currentFragment != null) {
                if (prevFragment == null || currentFragment.getClass() != prevFragment.getClass()) {
                    currentFragment.setAnswerPageLoadingFinishedListener(this);
                    currentFragment.setAnswerSelectedListener(this);
                    currentFragment.setArguments(fragmentBundle);

                    try {
                        t.replace(R.id.contentLayout, currentFragment).commit();
                    } catch (Exception e) {
                        L.e(TAG, "Error replace question type fragment", e);
                        finish();
                    }
                } else {
                    currentFragment = prevFragment;
                    currentFragment.setAnswerPageLoadingFinishedListener(this);
                    currentFragment.setAnswerSelectedListener(this);
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

                QuestionsBL.getQuestionsListFromDB(handler, task.getWaveId(), taskId);
            } else if (Keys.REJECT_TASK_OPERATION_TAG.equals(operation.getTag())) {
                int lastQuestionOrderId = preferencesManager.getLastNotAnsweredQuestionOrderId(task.getWaveId(),
                        taskId);
                Question question = QuestionsBL.getQuestionWithCheckConditionByOrderId(questions, lastQuestionOrderId);

                startActivity(IntentUtils.getQuitQuestionIntent(this, question));
                finish();
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
        }
        setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previousButton:
                //currentFragment.clearAnswer();
                startPreviousQuestionFragment();
                break;
            case R.id.nextButton:
                if (currentFragment.saveQuestion()) {
                    startNextQuestionFragment();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAnswerSelected(Boolean selected) {
        nextButton.setEnabled(selected);
    }

    @Override
    public void onAnswerPageLoadingFinished() {
        buttonsLayout.setVisibility(View.VISIBLE);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (currentFragment != null) {
//            currentFragment.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.quiteTask:
                if (task.getWaveId() != null && task.getId() != null) {
                    DialogUtils.showQuiteTaskDialog(this, task.getWaveId(), task.getId());
                }
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

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.question_title);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    public void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }
}