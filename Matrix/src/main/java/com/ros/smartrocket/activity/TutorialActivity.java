package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.rd.PageIndicatorView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.TutorialPageAdapter;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.views.TutorialView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TutorialActivity extends BaseActivity {
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.pageIndicatorView)
    PageIndicatorView pageIndicatorView;
    private List<View> tutorialViews = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);
        checkDeviceSettingsByOnResume(false);
        fillPager();
    }

    private void fillPager() {
        TutorialView tutorialView = new TutorialView(this);
        tutorialView.setTextAndImages(R.string.tut_title_1, R.string.tut_text1, R.drawable.character1);
        tutorialViews.add(tutorialView);

        tutorialView = new TutorialView(this);
        tutorialView.setTextAndImages(R.string.tut_title_2, R.string.tut_text2, R.drawable.character2);
        tutorialViews.add(tutorialView);

        tutorialView = new TutorialView(this);
        tutorialView.setTextAndImages(R.string.tut_title_3, R.string.tut_text3, R.drawable.character3);
        tutorialViews.add(tutorialView);

        tutorialView = new TutorialView(this);
        tutorialView.setTextAndImages(R.string.tut_title_4, R.string.tut_text4, R.drawable.character4);
        tutorialViews.add(tutorialView);

        tutorialView = new TutorialView(this);
        tutorialView.setTextAndImages(R.string.continue_register, R.string.tut_text5, R.drawable.character5);
        tutorialView.setLastSlide(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueRegistrationFlow();
            }
        });
        tutorialViews.add(tutorialView);

        TutorialPageAdapter mAdapter = new TutorialPageAdapter(tutorialViews);
        viewPager.setAdapter(mAdapter);
        pageIndicatorView.setViewPager(viewPager);
    }

    public void continueRegistrationFlow() {
        Intent intent;
        RegistrationPermissions registrationPermissions = PreferencesManager.getInstance().getRegPermissions();
        if (registrationPermissions.isTermsEnable()) {
            intent = new Intent(this, TermsAndConditionActivity.class);
        } else if (registrationPermissions.isReferralEnable()) {
            intent = new Intent(this, ReferralCasesActivity.class);
        } else if (registrationPermissions.isSrCodeEnable()) {
            intent = new Intent(this, PromoCodeActivity.class);
        } else {
            intent = new Intent(this, RegistrationActivity.class);
        }
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        finish();
    }


}