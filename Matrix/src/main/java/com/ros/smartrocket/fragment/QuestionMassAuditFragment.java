package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public static final String KEY_IS_REDO = "com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment.KEY_IS_REDO";
    public static final String KEY_IS_PREVIEW = "com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment.KEY_IS_PREVIEW";

    public QuestionMassAuditFragment() {
        super(new QuestionMassAuditBL());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((QuestionMassAuditBL) questionBL).setIsRedo(getArguments().getBoolean(KEY_IS_REDO));
        ((QuestionMassAuditBL) questionBL).setIsPreview(getArguments().getBoolean(KEY_IS_PREVIEW));

        DbHandler handler = new DbHandler(getActivity().getContentResolver());

        Question question = questionBL.getQuestion();
        QuestionsBL.getChildQuestionsListFromDB(handler, question.getTaskId(), question.getId(), question.getMissionId());
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