package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.question.QuestionMassAuditBL;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.Question;

import java.util.List;

/**
 * Mass Audit question type
 */
public class QuestionMassAuditFragment extends BaseQuestionFragment {
    public QuestionMassAuditFragment() {
        super(new QuestionMassAuditBL());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DbHandler handler = new DbHandler(getActivity().getContentResolver());

        Question question = questionBL.getQuestion();
        QuestionsBL.getChildQuestionsListFromDB(handler, question.getTaskId(), question.getId());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_mass_audit;
    }

    public class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case QuestionDbSchema.Query.TOKEN_QUERY:
                    List<Question> questions = QuestionsBL.convertCursorToQuestionList(cursor);
                    ((QuestionMassAuditBL) questionBL).subQuestionsLoaded(questions);
                    break;
                default:
                    break;
            }
        }
    }
}