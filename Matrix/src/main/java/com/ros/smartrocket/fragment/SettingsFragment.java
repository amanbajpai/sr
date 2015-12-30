package com.ros.smartrocket.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.*;
import android.widget.*;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.db.entity.AllowPushNotification;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.helpers.WriteDataHelper;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.net.TaskReminderService;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

/**
 * Setting fragment with all application related settings
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener, NetworkOperationListenerInterface {
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();

    private static final String DEFAULT_LANG = java.util.Locale.getDefault().toString();
    private static final String[] SUPPORTED_LANGS_CODE = new String[]{"en", "zh", "zh_CN", "zh_TW", "en_SG", "zh_HK"};
    private static final String[] VISIBLE_LANGS_CODE = new String[]{"en", "zh_CN", "zh_TW"};
    private static String[] VISIBLE_LANGUAGE = new String[]{"English", "中文（简体）", "中文（繁體）"};
    private static final int[] MONTHLY_LIMIT_MB_CODE = new int[]{0, 50, 100, 250, 500};
    private static final String[] MONTHLY_LIMIT_MB = new String[]{"Unlimited", "50", "100", "250", "500"};
    private static final int[] MISSION_LIMIT_MB_CODE = new int[]{0, 5, 10, 25, 50};
    private static final String[] MISSION_LIMIT_MB = new String[]{"Unlimited", "5", "10", "25", "50"};
    private static long[] TIME_IN_MILLIS = new long[]{DateUtils.MINUTE_IN_MILLIS * 5, DateUtils.MINUTE_IN_MILLIS * 10,
            DateUtils.MINUTE_IN_MILLIS * 30, DateUtils.HOUR_IN_MILLIS, DateUtils.HOUR_IN_MILLIS * 2};

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

    private ProgressDialog progressDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_settings, null);

        MONTHLY_LIMIT_MB[0] = getString(R.string.unlimited);
        MISSION_LIMIT_MB[0] = getString(R.string.unlimited);

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

        ((TextView) view.findViewById(R.id.currentVersion)).setText(BuildConfig.VERSION_NAME + " (" +
                BuildConfig.JENKINS_BUILD_VERSION + ")");
        view.findViewById(R.id.currentVersion).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(IntentUtils.getEmailIntent("Log", "kuar@ciklum.com", UIUtils.getLogs()));
                return false;
            }
        });

        setData();

        progressDialog = new ProgressDialog(getActivity());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((BaseActivity) getActivity()).addNetworkOperationListener(this);
    }

    @Override
    public void onStop() {
        ((BaseActivity) getActivity()).removeNetworkOperationListener(this);
        super.onStop();
    }

    public void setData() {
        setLanguageSpinner();
        setDeadlineReminderSpinner();
        setTaskLimitSpinner();
        setMonthLimitSpinner();

        locationServicesToggleButton.setBackgroundResource(R.drawable.btn_toggle);
        socialSharingToggleButton.setBackgroundResource(R.drawable.btn_toggle);
        useOnlyWifiToggleButton.setBackgroundResource(R.drawable.btn_toggle);
        saveImageToggleButton.setBackgroundResource(R.drawable.btn_toggle);
        pushMessagesToggleButton.setBackgroundResource(R.drawable.btn_toggle);
        deadlineReminderToggleButton.setBackgroundResource(R.drawable.btn_toggle);

        locationServicesToggleButton.setChecked(preferencesManager.getUseLocationServices());
        socialSharingToggleButton.setChecked(preferencesManager.getUseSocialSharing());
        useOnlyWifiToggleButton.setChecked(preferencesManager.getUseOnlyWiFiConnaction());
        saveImageToggleButton.setChecked(preferencesManager.getUseSaveImageToCameraRoll());
        pushMessagesToggleButton.setChecked(preferencesManager.getUsePushMessages());
        deadlineReminderToggleButton.setChecked(preferencesManager.getUseDeadlineReminder());

        languageSpinner.setOnItemSelectedListener(this);
        deadlineReminderSpinner.setOnItemSelectedListener(this);
        taskLimitSpinner.setOnItemSelectedListener(this);
        monthLimitSpinner.setOnItemSelectedListener(this);

        locationServicesToggleButton.setOnCheckedChangeListener(this);
        pushMessagesToggleButton.setOnCheckedChangeListener(this);
        socialSharingToggleButton.setOnCheckedChangeListener(this);
        saveImageToggleButton.setOnCheckedChangeListener(this);
        useOnlyWifiToggleButton.setOnCheckedChangeListener(this);
        deadlineReminderToggleButton.setOnCheckedChangeListener(this);
    }

    public void setLanguageSpinner() {
        String currentLanguageCode = preferencesManager.getLanguageCode();
        if (TextUtils.isEmpty(currentLanguageCode)) {
            currentLanguageCode = DEFAULT_LANG;
        }

        ArrayAdapter languageAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_single_line_spinner, R.id.name, VISIBLE_LANGUAGE);
        languageSpinner.setAdapter(languageAdapter);

        int selectedItemPosition = 0;
        for (int i = 0; i < VISIBLE_LANGS_CODE.length; i++) {
            if (VISIBLE_LANGS_CODE[i].equals(currentLanguageCode)) {
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
            int hours = (int) ((TIME_IN_MILLIS[i] / (DateUtils.HOUR_IN_MILLIS)) % 24);
            int minutes = (int) ((TIME_IN_MILLIS[i] / (DateUtils.MINUTE_IN_MILLIS)) % 60);

            if (hours != 0) {
                refreshTimeArray[i] = hours + " " + getResources().getQuantityString(R.plurals.hour, hours);
            } else if (minutes != 0) {
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
                MISSION_LIMIT_MB);
        taskLimitSpinner.setAdapter(taskLimitAdapter);

        int selectedItemPosition = 0;
        for (int i = 0; i < MISSION_LIMIT_MB_CODE.length; i++) {
            if (MISSION_LIMIT_MB_CODE[i] == limit) {
                selectedItemPosition = i;
                break;
            }
        }
        taskLimitSpinner.setSelection(selectedItemPosition);
    }

    public void setMonthLimitSpinner() {
        int limit = preferencesManager.get3GUploadMonthLimit();

        ArrayAdapter monthLimitAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_spinner, R.id.name,
                MONTHLY_LIMIT_MB);
        monthLimitSpinner.setAdapter(monthLimitAdapter);

        int selectedItemPosition = 0;
        for (int i = 0; i < MONTHLY_LIMIT_MB_CODE.length; i++) {
            if (MONTHLY_LIMIT_MB_CODE[i] == limit) {
                selectedItemPosition = i;
                break;
            }
        }
        monthLimitSpinner.setSelection(selectedItemPosition);
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.ALLOW_PUSH_NOTIFICATION_OPERATION_TAG.equals(operation.getTag())) {
                MyAccount myAccount = App.getInstance().getMyAccount();
                AllowPushNotification allowPushNotification = (AllowPushNotification) operation.getEntities().get(0);
                myAccount.setAllowPushNotification(allowPushNotification.getAllow());
                App.getInstance().setMyAccount(myAccount);
                progressDialog.dismiss();
            }
        } else {
            progressDialog.dismiss();
            UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        switch (v.getId()) {
            case R.id.deadlineReminderToggleButton:
                deadlineReminderLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                preferencesManager.setUseDeadlineReminder(isChecked);

                changeTaskReminderServiceStatus();
                break;

            case R.id.locationServicesToggleButton:
                preferencesManager.setUseLocationServices(isChecked);

                if (!UIUtils.isAllLocationSourceEnabled(getActivity()) && preferencesManager.getUseLocationServices()) {
                    DialogUtils.showLocationDialog(getActivity(), false);
                }
                break;
            case R.id.pushMessagesToggleButton:
                apiFacade.allowPushNotification(getActivity(), isChecked);
                progressDialog.show();
                preferencesManager.setUsePushMessages(isChecked);
                changeTaskReminderServiceStatus();
                break;
            case R.id.socialSharingToggleButton:
                preferencesManager.setUseSocialSharing(isChecked);
                break;
            case R.id.saveImageToggleButton:
                preferencesManager.setUseSaveImageToCameraRoll(isChecked);
                break;
            case R.id.useOnlyWifiToggleButton:
                preferencesManager.setUseOnlyWiFiConnaction(isChecked);
                break;
        }
    }

    public void changeTaskReminderServiceStatus() {
        if (preferencesManager.getUsePushMessages() || preferencesManager.getUseDeadlineReminder()) {
            getActivity().startService(new Intent(getActivity(), TaskReminderService.class).setAction(Keys
                    .ACTION_START_REMINDER_TIMER));
        } else {
            getActivity().stopService(new Intent(getActivity(), TaskReminderService.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.languageSpinner:
                String selectedLanguageCode = VISIBLE_LANGS_CODE[languageSpinner.getSelectedItemPosition()];
                boolean languageChanged = !preferencesManager.getLanguageCode().equals(selectedLanguageCode);

                preferencesManager.setLanguageCode(selectedLanguageCode);
                setDefaultLanguage(getActivity(), preferencesManager.getLanguageCode());

                if (languageChanged) {
                    UIUtils.showSimpleToast(getActivity(), R.string.success);
                    getActivity().finish();
                    getActivity().sendBroadcast(new Intent().setAction(Keys.FINISH_MAIN_ACTIVITY));
                }
                break;
            case R.id.deadlineReminderSpinner:
                preferencesManager.setDeadlineReminderMillisecond(TIME_IN_MILLIS[deadlineReminderSpinner
                        .getSelectedItemPosition()]);
                break;
            case R.id.taskLimitSpinner:
                preferencesManager.set3GUploadTaskLimit(
                        MISSION_LIMIT_MB_CODE[taskLimitSpinner.getSelectedItemPosition()]);
                break;
            case R.id.monthLimitSpinner:
                preferencesManager.set3GUploadMonthLimit(
                        MONTHLY_LIMIT_MB_CODE[monthLimitSpinner.getSelectedItemPosition()]);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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

        if ("zh_CN".equals(languageCode) || "en_SG".equals(languageCode)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if ("zh".equals(languageCode) || "zh_TW".equals(languageCode) || "zh_HK".equals(languageCode)) {
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
        boolean result;
        switch (item.getItemId()) {
            case R.id.logout:
                WriteDataHelper.prepareLogout(getActivity());

                getActivity().startActivity(IntentUtils.getLoginIntentForLogout(getActivity()));
                getActivity().finish();
                getActivity().sendBroadcast(new Intent().setAction(Keys.FINISH_MAIN_ACTIVITY));

                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }
}