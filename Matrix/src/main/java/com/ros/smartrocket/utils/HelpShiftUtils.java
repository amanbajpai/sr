package com.ros.smartrocket.utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.helpshift.Core;
import com.helpshift.campaigns.Campaigns;
import com.helpshift.support.Support;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.MyAccount;

import java.util.HashMap;

public class HelpShiftUtils {
    private static final String AGENT_ID = "Agent_Id";
    private static final String AGENT_RANK_LEVEL = "Agent_Rank_Level";
    private static final String ROCKET_POINTS = "Rocket_Points";
    private static final String COUNTRY = "Country";
    private static final String COMPANY_NAME = "Company_Name";
    private static final String CITY = "City";
    private static final String JOINING_DATE = "Joining_Date";
    private static final String HIDE_NAME_AND_EMAIL = "hideNameAndEmail";
    private static MyAccount account;

    public static void showFAQ(Activity activity) {
        account = App.getInstance().getMyAccount();
        initHS();
        Support.showFAQs(activity, getConfig());
    }

    private static void initHS() {
        Core.login(String.valueOf(account.getId()), account.getSingleName(), PreferencesManager.getInstance().getLastEmail());
        Campaigns.addProperty(AGENT_ID, account.getId());
        Campaigns.addProperty(AGENT_RANK_LEVEL, account.getLevelNumber());
        Campaigns.addProperty(ROCKET_POINTS, account.getExperience());
        Campaigns.addProperty(COUNTRY, account.getCountryName());
        Campaigns.addProperty(CITY, account.getCityName());
        Campaigns.addProperty(COMPANY_NAME, account.getCompanyName());
        Campaigns.addProperty(JOINING_DATE, account.getJoined());
    }

    @NonNull
    private static HashMap getConfig() {
        HashMap config = new HashMap();
        config.put(HIDE_NAME_AND_EMAIL, true);
        config.put(Support.CustomMetadataKey, getMetaData());
        return config;
    }

    @NonNull
    private static HashMap getMetaData() {
        HashMap customMetadata = new HashMap();
        customMetadata.put(AGENT_ID, account.getId());
        customMetadata.put(AGENT_RANK_LEVEL, account.getLevelNumber());
        customMetadata.put(ROCKET_POINTS, account.getExperience());
        customMetadata.put(COUNTRY, account.getCountryName());
        customMetadata.put(CITY, account.getCityName());
        customMetadata.put(COMPANY_NAME, account.getCompanyName());
        customMetadata.put(JOINING_DATE, account.getJoined());
        customMetadata.put(Support.TagsKey, new String[]{account.getCountryName(), account.getCompanyName()});
        return customMetadata;
    }
}