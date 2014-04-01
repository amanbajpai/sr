package com.ros.smartrocket.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

/**
 * Setting fragment with all application related settings
 */
public class SettingsFragment extends Fragment implements OnClickListener {
    //private static final String TAG = SettingsFragment.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    public static final String DEFAULT_LANG = java.util.Locale.getDefault().getLanguage();
    public static final String[] SUPPORTED_LANGS_CODE = new String[]{"en", "zh_CN", "zh_TW"};
    public static final String[] SUPPORTED_LANGUAGE = new String[]{"English", "Chinese (Simplified)",
            "Chinese (Traditional)"};
    public static final int[] APPOINTMENT_INTERVAL_CODE = new int[]{0, 1, 2};
    public static final String[] APPOINTMENT_INTERVAL = new String[]{"Never", "Always"};
    public static final int[] LIMIT_MB_CODE = new int[]{10000, 5, 10, 20, 50, 100, 200};
    public static final String[] LIMIT_MB = new String[]{"Unlimited", "5", "10", "20", "50", "100", "200"};
    private ViewGroup view;

    private Spinner languageSpinner;
    private Spinner appointmentIntervalSpinner;
    private Spinner taskLimitSpinner;
    private Spinner monthLimitSpinner;

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
        appointmentIntervalSpinner = (Spinner) view.findViewById(R.id.appointmentIntervalSpinner);
        taskLimitSpinner = (Spinner) view.findViewById(R.id.taskLimitSpinner);
        monthLimitSpinner = (Spinner) view.findViewById(R.id.monthLimitSpinner);

        locationServicesToggleButton = (ToggleButton) view.findViewById(R.id.locationServicesToggleButton);
        pushMessagesToggleButton = (ToggleButton) view.findViewById(R.id.pushMessagesToggleButton);
        socialSharingToggleButton = (ToggleButton) view.findViewById(R.id.socialSharingToggleButton);
        saveImageToggleButton = (ToggleButton) view.findViewById(R.id.saveImageToggleButton);
        useOnlyWifiToggleButton = (ToggleButton) view.findViewById(R.id.useOnlyWifiToggleButton);
        deadlineReminderToggleButton = (ToggleButton) view.findViewById(R.id.deadlineReminderToggleButton);

        view.findViewById(R.id.confirmAndSaveButton).setOnClickListener(this);
        view.findViewById(R.id.cancelButton).setOnClickListener(this);

        ((TextView) view.findViewById(R.id.currentVersion)).setText(UIUtils.getAppVersion(getActivity()));

        setData();
        return view;
    }

    public void setData() {
        setLanguageSpinner();
        setApppointmentIntervalSpinner();
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

    public void setApppointmentIntervalSpinner() {
        int currentIntervalCode = preferencesManager.getAppointmentInervalCode();

        ArrayAdapter languageAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_spinner, R.id.name,
                APPOINTMENT_INTERVAL);
        appointmentIntervalSpinner.setAdapter(languageAdapter);

        int selectedItemPosition = 0;
        for (int i = 0; i < APPOINTMENT_INTERVAL_CODE.length; i++) {
            if (APPOINTMENT_INTERVAL_CODE[i] == currentIntervalCode) {
                selectedItemPosition = i;
                break;
            }
        }
        appointmentIntervalSpinner.setSelection(selectedItemPosition);
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

                preferencesManager.setAppointmentInervalCode(APPOINTMENT_INTERVAL_CODE[appointmentIntervalSpinner
                        .getSelectedItemPosition()]);

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
            setDefaultLanguage(App.getInstance(), getLanguageCodeFromSupported());
        }
    }

    public static void setDefaultLanguage(Context context, String languageCode) {
        Configuration config = context.getResources().getConfiguration();
        config.locale = new Locale(languageCode);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.app_settings_title);

        super.onCreateOptionsMenu(menu, inflater);
    }
}