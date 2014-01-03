package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.ros.smartrocket.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.fragment.BaseQuestionFragment;
import com.ros.smartrocket.fragment.QuestionType1Fragment;
import com.ros.smartrocket.fragment.QuestionType3Fragment;
import com.ros.smartrocket.fragment.QuestionType4Fragment;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;

public class QuestionsActivity extends BaseActivity implements NetworkOperationListenerInterface,
        View.OnClickListener, OnAnswerSelectedListener {
    private static final String TAG = QuestionsActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    private Integer surveyId;
    private Integer taskId;
    private Task task = new Task();
    private ProgressBar mainProgressBar;
    private Button previousButton;
    private Button nextButton;
    private Button validationButton;

    private AsyncQueryHandler handler;
    private ArrayList<Question> questions;
    private BaseQuestionFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_questions);

        if (getIntent() != null) {
            surveyId = getIntent().getIntExtra(Keys.SURVEY_ID, 0);
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
        }

        handler = new DbHandler(getContentResolver());

        mainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);

        previousButton = (Button) findViewById(R.id.previousButton);
        previousButton.setOnClickListener(this);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        validationButton = (Button) findViewById(R.id.validationButton);
        validationButton.setOnClickListener(this);

        TasksBL.getTaskFromDBbyID(handler, taskId);
        QuestionsBL.getQuestionsListFromDB(handler, surveyId);
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    task = TasksBL.convertCursorToTask(cursor);

                    setTitle(task.getName());
                    break;
                case QuestionDbSchema.Query.TOKEN_QUERY:
                    questions = QuestionsBL.convertCursorToQuestionList(cursor);

                    if (questions.size() > 0) {
                        //int previousQuestionOrderId = preferencesManager.getPreviousQuestionOrderId(taskId);
                        int lastQuestionOrderId = preferencesManager.getLastNotAnsweredQuestionOrderId(taskId);

                        Question question = QuestionsBL.getQuestionByOrderId(questions, lastQuestionOrderId);
                        startFragment(question);
                    } else {
                        setSupportProgressBarIndeterminateVisibility(true);
                        apiFacade.getQuestions(QuestionsActivity.this, surveyId, taskId);
                    }

                    break;
                default:
                    break;
            }
        }
    }

    public void refreshMainProgress(int currentQuestionOrderId) {
        int questionCount = questions.size() - 2; //-2 because question list contain two system questions

        mainProgressBar.setProgress((int) (((float) currentQuestionOrderId / questionCount * 100)));

    }

    public void startNextQuestionFragment() {
        if (currentFragment != null) {
            Question currentQuestion = currentFragment.getQuestion();
            L.i(TAG, "startNextQuestionFragment. currentQuestionOrderId:" + currentQuestion.getOrderId());

            int nextQuestionOrderId = AnswersBL.getNextQuestionOrderId(currentQuestion);

            Question question = QuestionsBL.getQuestionByOrderId(questions, nextQuestionOrderId);
            if (question != null) {
                preferencesManager.setLastNotAnsweredQuestionOrderId(taskId, nextQuestionOrderId);
                question.setPreviousQuestionOrderId(currentQuestion.getOrderId());

                QuestionsBL.updatePreviousQuestionOrderId(question.getId(), question.getPreviousQuestionOrderId());

                startFragment(question);
            }
        }
    }

    public void startPreviousQuestionFragment() {
        if (currentFragment != null) {
            Question currentQuestion = currentFragment.getQuestion();
            L.i(TAG, "startNextQuestionFragment. currentQuestionOrderId:" + currentQuestion.getOrderId());

            int previousQuestionOrderId = currentQuestion.getPreviousQuestionOrderId() != 0 ? currentQuestion.getPreviousQuestionOrderId() : 1;
            preferencesManager.setLastNotAnsweredQuestionOrderId(taskId, previousQuestionOrderId);

            Question question = QuestionsBL.getQuestionByOrderId(questions, previousQuestionOrderId);

            startFragment(question);
        }
    }

    public void startFragment(Question question) {
        L.i(TAG, "startFragment. orderId:" + question.getOrderId());
        if (question != null) {

            refreshMainProgress(question.getOrderId());

            int nextQuestionOrderId = AnswersBL.getNextQuestionOrderId(question);
            Question nextQuestion = QuestionsBL.getQuestionByOrderId(questions, nextQuestionOrderId);

            if (nextQuestion.getType() == 3) {
                nextButton.setVisibility(View.GONE);
                validationButton.setVisibility(View.VISIBLE);
            } else {
                nextButton.setVisibility(View.VISIBLE);
                validationButton.setVisibility(View.GONE);
            }

            if (question.getShowBackButton()) {
                previousButton.setVisibility(View.VISIBLE);
            } else {
                previousButton.setVisibility(View.INVISIBLE);
            }

            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putSerializable(Keys.QUESTION, question);

            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            switch (question.getType()) {
                case 1:
                    currentFragment = new QuestionType1Fragment();
                    break;
                /*case 3:
                    currentFragment = new QuestionType2Fragment();
                    break;*/
                case 2:
                    currentFragment = new QuestionType3Fragment();
                    break;
                case 5:
                    currentFragment = new QuestionType4Fragment();
                    break;
                default:
                    break;
            }

            if (currentFragment != null) {
                currentFragment.setAnswerSelectedListener(this);
                currentFragment.setArguments(fragmentBundle);
                t.replace(R.id.contentLayout, currentFragment).commit();

            }
        } else {
            ((FrameLayout) findViewById(R.id.contentLayout)).removeAllViews();
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.GET_QUESTIONS_OPERATION_TAG.equals(operation.getTag())) {
                QuestionsBL.getQuestionsListFromDB(handler, surveyId);
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
                currentFragment.saveQuestion();
                startPreviousQuestionFragment();
                break;
            case R.id.nextButton:
                currentFragment.saveQuestion();
                startNextQuestionFragment();
                break;
            case R.id.validationButton:
                //TODO Start validationActivity. Update task status
                //TasksBL.updateTaskStatusId(taskId, Task.TaskStatusId.validation.getStatusId());
                startActivity(IntentUtils.getTaskValidationIntent(this, task.getId()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onAnswerSelected(Boolean selected) {
        nextButton.setEnabled(selected);
        validationButton.setEnabled(selected);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.quiteTask:
                DialogUtils.showQuiteTaskDialog(this, task.getId());
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