package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Question;

public class QuitQuestionActivity extends BaseActivity implements
        View.OnClickListener {
    private Question question;

    public QuitQuestionActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_quit_question);

        if (getIntent() != null) {
            question = (Question) getIntent().getSerializableExtra(Keys.QUESTION);
        }
        ((TextView) findViewById(R.id.quitQuestionText)).setText(question.getQuestion());
        findViewById(R.id.okButton).setOnClickListener(this);

        TasksBL.removeTasksByWaveId(getContentResolver(), question.getWaveId());
        QuestionsBL.removeQuestionsByWaveId(this, question.getWaveId());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton:
                finish();
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
}
