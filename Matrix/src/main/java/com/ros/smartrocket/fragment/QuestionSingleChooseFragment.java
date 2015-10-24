package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.question.QuestionSingleChooseBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

/**
 * Single choose question type
 */
public class QuestionSingleChooseFragment extends BaseQuestionFragment {
    public QuestionSingleChooseFragment() {
        super(new QuestionSingleChooseBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_single_choose;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new DbHandler(getActivity().getContentResolver());

        Question question = questionBL.getQuestion();
        AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId());
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case AnswerDbSchema.Query.TOKEN_QUERY:
                    Answer[] answers = AnswersBL.convertCursorToAnswersArray(cursor);
                    questionBL.fillViewWithAnswers(answers);
                    break;
                default:
                    break;
            }
        }
    }
}