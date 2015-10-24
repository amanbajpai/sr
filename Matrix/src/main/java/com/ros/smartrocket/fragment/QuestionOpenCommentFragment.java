package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.question.QuestionOpenCommentBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

/**
 * Open comment question type
 */
public class QuestionOpenCommentFragment extends BaseQuestionFragment {
    public QuestionOpenCommentFragment() {
        super(new QuestionOpenCommentBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_open_comment;
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