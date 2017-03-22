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
    private static final String CITY = "City";
    private static final String JOINING_DATE = "Joining_Date";
    private static final String HIDE_NAME_AND_EMAIL = "hideNameAndEmail";
    private final MyAccount account;
    private HashMap<String, String> countryMap;

    public HelpShiftUtils() {
        account = App.getInstance().getMyAccount();
    }

    public void showFAQ(Activity activity) {
        initHS();
        Support.showFAQs(activity, getConfig());
    }

    private void initHS() {
        Core.login(String.valueOf(account.getId()), account.getSingleName(), PreferencesManager.getInstance().getLastEmail());
        Campaigns.addProperty(AGENT_ID, account.getId());
        Campaigns.addProperty(AGENT_RANK_LEVEL, account.getLevelNumber());
        Campaigns.addProperty(ROCKET_POINTS, account.getExperience());
        Campaigns.addProperty(COUNTRY, account.getCountryName());
        Campaigns.addProperty(CITY, account.getCityName());
        Campaigns.addProperty(JOINING_DATE, account.getJoined());
    }

    @NonNull
    private HashMap getConfig() {
        HashMap config = new HashMap();
        config.put(HIDE_NAME_AND_EMAIL, true);
        config.put(Support.CustomMetadataKey, getMetaData());
        return config;
    }

    @NonNull
    private HashMap getMetaData() {
        HashMap customMetadata = new HashMap();
        customMetadata.put(AGENT_ID, account.getId());
        customMetadata.put(AGENT_RANK_LEVEL, account.getLevelNumber());
        customMetadata.put(ROCKET_POINTS, account.getExperience());
        customMetadata.put(COUNTRY, account.getCountryName());
        customMetadata.put(CITY, account.getCityName());
        customMetadata.put(JOINING_DATE, account.getJoined());
        if (getCountryName(account.getCountryCode()) != null) {
            customMetadata.put("country", getCountryName(account.getCountryCode()));
            customMetadata.put(Support.TagsKey, new String[]{"country"});
        }
        return customMetadata;
    }

    private String getCountryName(String countryCode) {
        if (countryMap == null) {
            fillCountryMap();
        }
        return countryMap.get(countryCode);
    }

    private void fillCountryMap() {
        countryMap = new HashMap<>();
        countryMap.put("HKG", "Hong Kong");
    }
}