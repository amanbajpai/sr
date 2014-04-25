package com.ros.smartrocket.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.helpers.WriteDataHelper;
import com.ros.smartrocket.net.TaskReminderService;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

/**
 * Setting fragment with all application related settings
 */
public class SettingsFragment extends Fragment implements OnClickListener, CompoundButton.OnCheckedChangeListener {
    //private static final String TAG = SettingsFragment.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    public static final String DEFAULT_LANG = java.util.Locale.getDefault().toString();
    public static final String[] SUPPORTED_LANGS_CODE = new String[]{"en", "zh_CN", "zh_TW"};
    public static final String[] SUPPORTED_LANGUAGE = new String[]{"English", "Chinese (Simplified)",
            "Chinese (Traditional)"};
    public static final int[] LIMIT_MB_CODE = new int[]{10000, 5, 10, 20, 50, 100, 200};
    public static final String[] LIMIT_MB = new String[]{"Unlimited", "5", "10", "20", "50", "100", "200"};
    public static long[] TIME_IN_MILLIS = new long[]{DateUtils.MINUTE_IN_MILLIS, DateUtils.MINUTE_IN_MILLIS * 2,
            DateUtils.MINUTE_IN_MILLIS * 5, DateUtils.MINUTE_IN_MILLIS * 10, DateUtils.MINUTE_IN_MILLIS * 30,
            DateUtils.HOUR_IN_MILLIS, DateUtils.HOUR_IN_MILLIS * 2};
    private ViewGroup view;

    private Spinner languageSpinner;
    private Spinner deadlineReminderSpinner;
    private Spinner taskLimitSpinner;
    private Spinner monthLimitSpinner;

    private LinearLayout deadlineReminderLayout;

    private ToggleButton locationServicesToggleButton;
    private ToggleButton pushMessagesToggleButton;
    private ToggleButton socialSharingToggleButton;
    private ToggleButton saveImageToggleButton;
    private ToggleButton useOnlyWifiToggleButton;
    private ToggleButton deadlineReminderToggleButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);*/

        view = (ViewGroup) inflater.inflate(R.layout.fragment_settings, null);

        languageSpinner = (Spinner) view.findViewById(R.id.languageSpinner);
        deadlineReminderSpinner = (Spinner) view.findViewById(R.id.deadlineReminderSpinner);
        taskLimitSpinner = (Spinner) view.findViewById(R.id.taskLimitSpinner);
        monthLimitSpinner = (Spinner) view.findViewById(R.id.monthLimitSpinner);

        deadlineReminderLayout = (LinearLayout) view.findViewById(R.id.deadlineReminderLayout);

        locationServicesToggleButton = (ToggleButton) view.findViewById(R.id.locationServicesToggleButton);
        pushMessagesToggleButton = (ToggleButton) view.findViewById(R.id.pushMessagesToggleButton);
        socialSharingToggleButton = (ToggleButton) view.findViewById(R.id.socialSharingToggleButton);
        saveImageToggleButton = (ToggleButton) view.findViewById(R.id.saveImageToggleButton);
        useOnlyWifiToggleButton = (ToggleButton) view.findViewById(R.id.useOnlyWifiToggleButton);
        deadlineReminderToggleButton = (ToggleButton) view.findViewById(R.id.deadlineReminderToggleButton);
        deadlineReminderToggleButton.setOnCheckedChangeListener(this);

        view.findViewById(R.id.confirmAndSaveButton).setOnClickListener(this);
        view.findViewById(R.id.cancelButton).setOnClickListener(this);

        ((TextView) view.findViewById(R.id.currentVersion)).setText(UIUtils.getAppVersion(getActivity()));

        setData();
        return view;
    }

    public void setData() {
        setLanguageSpinner();
        setDeadlineReminderSpinner();
        setTaskLimitSpinner();
        setMonthLimitSpinner();

        locationServicesToggleButton.setChecked(preferencesManager.getUseLocationServices());
        socialSharingToggleButton.setChecked(preferencesManager.getUseSocialSharing());
        useOnlyWifiToggleButton.setChecked(preferencesManager.getUseOnlyWiFiConnaction());
        saveImageToggleButton.setChecked(preferencesManager.getUseSaveImageToCameraRoll());
        pushMessagesToggleButton.setChecked(preferencesManager.getUsePushMessages());
        deadlineReminderToggleButton.setChecked(preferencesManager.getUseDeadlineReminder());
    }

    public void setLanguageSpinner() {
        String currentLanguageCode = preferencesManager.getLanguageCode();
        if (TextUtils.isEmpty(currentLanguageCode)) {
            currentLanguageCode = DEFAULT_LANG;
        }

        ArrayAdapter languageAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_single_line_spinner, R.id.name, SUPPORTED_LANGUAGE);
        languageSpinner.setAdapter(languageAdapter);

        int selectedItemPosition = 0;
        for (int i = 0; i < SUPPORTED_LANGS_CODE.length; i++) {
            if (SUPPORTED_LANGS_CODE[i].equals(currentLanguageCode)) {
                selectedItemPosition = i;
                break;
            }
        }
        languageSpinner.setSelection(selectedItemPosition);
    }

    public void setDeadlineReminderSpinner() {
        long currentRefreshTime = preferencesManager.getDeadlineReminderMillisecond();

        String[] refreshTimeArray = new String[TIME_IN_MILLIS.length];
        for (int i = 0; i < TIME_IN_MILLIS.length; i++) {
            int hours = (int) ((TIME_IN_MILLIS[i] / (1000 * 60 * 60)) % 24);
            int minutes = (int) ((TIME_IN_MILLIS[i] / (1000 * 60)) % 60);

            if (hours != 0) {
                refreshTimeArray[i] = hours + " " + getResources().getQuantityString(R.plurals.hour, hours);
            } else if (hours == 0 && minutes != 0) {
                refreshTimeArray[i] = minutes + " " + getResources().getQuantityString(R.plurals.minute, minutes);
            }
        }

        ArrayAdapter<String> refreshTimeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_spinner,
                R.id.name, refreshTimeArray);

        deadlineReminderSpinner.setAdapter(refreshTimeAdapter);

        int selectedItemPosition = 0;
        for (int i = 0; i < TIME_IN_MILLIS.length; i++) {
            if (TIME_IN_MILLIS[i] == currentRefreshTime) {
                selectedItemPosition = i;
                break;
            }
        }

        deadlineReminderSpinner.setSelection(selectedItemPosition);

        deadlineReminderLayout.setVisibility(preferencesManager.getUseDeadlineReminder() ? View.VISIBLE : View.GONE);
    }

    public void setTaskLimitSpinner() {
        int limit = preferencesManager.get3GUploadTaskLimit();

        ArrayAdapter taskLimitAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_spinner, R.id.name,
                LIMIT_MB);
        taskLimitSpinner.setAdapter(taskLimitAdapter);

        int selectedItemPosition = 0;
        for (int i = 0; i < LIMIT_MB_CODE.length; i++) {
            if (LIMIT_MB_CODE[i] == limit) {
                selectedItemPosition = i;
                break;
            }
        }
        taskLimitSpinner.setSelection(selectedItemPosition);
    }

    public void setMonthLimitSpinner() {
        int limit = preferencesManager.get3GUploadMonthLimit();

        ArrayAdapter monthLimitAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_spinner, R.id.name,
                LIMIT_MB);
        monthLimitSpinner.setAdapter(monthLimitAdapter);

        int selectedItemPosition = 0;
        for (int i = 0; i < LIMIT_MB_CODE.length; i++) {
            if (LIMIT_MB_CODE[i] == limit) {
                selectedItemPosition = i;
                break;
            }
        }
        monthLimitSpinner.setSelection(selectedItemPosition);
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        switch (v.getId()) {
            case R.id.deadlineReminderToggleButton:
                deadlineReminderLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmAndSaveButton:
                preferencesManager.setLanguageCode(SUPPORTED_LANGS_CODE[languageSpinner.getSelectedItemPosition()]);
                setDefaultLanguage(getActivity(), preferencesManager.getLanguageCode());

                preferencesManager.setUseLocationServices(locationServicesToggleButton.isChecked());
                preferencesManager.setUseSocialSharing(socialSharingToggleButton.isChecked());
                preferencesManager.setUseOnlyWiFiConnaction(useOnlyWifiToggleButton.isChecked());
                preferencesManager.setUseSaveImageToCameraRoll(saveImageToggleButton.isChecked());
                preferencesManager.setUsePushMessages(pushMessagesToggleButton.isChecked());
                preferencesManager.setUseDeadlineReminder(deadlineReminderToggleButton.isChecked());

                preferencesManager.set3GUploadTaskLimit(LIMIT_MB_CODE[taskLimitSpinner.getSelectedItemPosition()]);
                preferencesManager.set3GUploadMonthLimit(LIMIT_MB_CODE[monthLimitSpinner.getSelectedItemPosition()]);

                preferencesManager.setDeadlineReminderMillisecond(TIME_IN_MILLIS[deadlineReminderSpinner
                        .getSelectedItemPosition()]);

                if (deadlineReminderToggleButton.isChecked()) {
                    getActivity().startService(new Intent(getActivity(), TaskReminderService.class).setAction(Keys
                            .ACTION_START_REMINDER_TIMER));
                } else {
                    getActivity().stopService(new Intent(getActivity(), TaskReminderService.class));
                }

                if (!UIUtils.isGpsEnabled(getActivity()) && preferencesManager.getUseLocationServices()) {
                    DialogUtils.showLocationDialog(getActivity(), false);
                }

                UIUtils.showSimpleToast(getActivity(), R.string.success);
                break;
            case R.id.cancelButton:
                setData();
                break;
            default:
                break;
        }
    }

    public static String getLanguageCodeFromSupported() {
        for (int i = 0; i < SUPPORTED_LANGS_CODE.length; i++) {
            if (DEFAULT_LANG.equals(SUPPORTED_LANGS_CODE[i])) {
                return DEFAULT_LANG;
            }
        }
        return "en";
    }

    public static void setCurrentLanguage() {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();

        if (!TextUtils.isEmpty(preferencesManager.getLanguageCode())) {
            setDefaultLanguage(App.getInstance(), preferencesManager.getLanguageCode());
        } else {
            String supportedLanguage = getLanguageCodeFromSupported();
            preferencesManager.setLanguageCode(supportedLanguage);
            setDefaultLanguage(App.getInstance(), supportedLanguage);
        }
    }

    public static void setDefaultLanguage(Context context, String languageCode) {
        Configuration config = context.getResources().getConfiguration();

        if (languageCode.equals("zh_CN")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (languageCode.equals("zh_TW")) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        } else {
            config.locale = new Locale(languageCode);
        }

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_settings, menu);

        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.app_settings_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                WriteDataHelper.prepareLogout(getActivity());

                getActivity().startActivity(IntentUtils.getLoginIntentForLogout(getActivity()));
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}