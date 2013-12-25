package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.ros.smartrocket.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.fragment.BaseQuestionFragment;
import com.ros.smartrocket.fragment.QuestionType1Fragment;
import com.ros.smartrocket.fragment.QuestionType3Fragment;
import com.ros.smartrocket.fragment.QuestionType4Fragment;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;

public class QuestionsActivity extends BaseActivity implements NetworkOperationListenerInterface, View.OnClickListener {
    private static final String TAG = QuestionsActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();

    private Integer surveyId;
    private Button previousButton;
    private Button nextButton;

    private AsyncQueryHandler handler;
    private ArrayList<Question> questions;
    private BaseQuestionFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_questions);
        setTitle(R.string.question_title);

        if (getIntent() != null) {
            surveyId = getIntent().getIntExtra(Keys.SURVEY_ID, 0);
        }

        handler = new DbHandler(getContentResolver());

        previousButton = (Button) findViewById(R.id.previousButton);
        previousButton.setOnClickListener(this);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        QuestionsBL.getQuestionsListFromDB(handler, surveyId);
        apiFacade.getQuestions(this, surveyId);

        setSupportProgressBarIndeterminateVisibility(true);
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case QuestionDbSchema.Query.TOKEN_QUERY:
                    questions = QuestionsBL.convertCursorToQuestionList(cursor);

                    if (questions.size() > 0) {
                        setSupportProgressBarIndeterminateVisibility(false);
                        setNextQuestionFragment(1, 1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setNextQuestionFragment(int currentQuestionOrderId, int nextQuestionOrderId) {
        Question question = QuestionsBL.getQuestionByOrderId(questions, nextQuestionOrderId);
        if (question != null) {
            question.setPreviousQuestionOrderId(currentQuestionOrderId);

            if(nextQuestionOrderId==1){
                previousButton.setVisibility(View.INVISIBLE);
            } else {
                previousButton.setVisibility(View.VISIBLE);
            }

            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            switch (question.getType()) {
                case 1:
                    currentFragment = new QuestionType1Fragment();
                    currentFragment.setQuestion(question);
                    break;
                case 2:
                    currentFragment = new QuestionType3Fragment();
                    currentFragment.setQuestion(question);
                    break;
                case 5:
                    currentFragment = new QuestionType4Fragment();
                    currentFragment.setQuestion(question);
                    break;
                default:
                    break;
            }

            if (currentFragment != null) {
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
    }

    @Override
    public void onClick(View v) {
        Question currentQuestion;
        switch (v.getId()) {
            case R.id.previousButton:
                currentFragment.saveQuestion();
                currentQuestion = currentFragment.getQuestion();

                setNextQuestionFragment(currentQuestion.getOrderId(), currentQuestion.getPreviousQuestionOrderId());
                break;
            case R.id.nextButton:
                currentFragment.saveQuestion();
                currentQuestion = currentFragment.getQuestion();

                int nextQuestionOrderId = AnswersBL.getNextQuestionOrderId(currentQuestion);
                setNextQuestionFragment(currentQuestion.getOrderId(), nextQuestionOrderId);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
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