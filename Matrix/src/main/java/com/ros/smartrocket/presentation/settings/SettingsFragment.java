package com.ros.smartrocket.presentation.settings;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.helpshift.Core;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.account.MyAccount;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.interfaces.SwitchCheckedChangeListener;
import com.ros.smartrocket.net.TaskReminderService;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.ui.dialog.DefaultInfoDialog;
import com.ros.smartrocket.ui.views.CustomSwitch;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.helpers.WriteDataHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingsFragment extends BaseFragment implements SwitchCheckedChangeListener,
        AdapterView.OnItemSelectedListener, SettingsMvpView {
    private static final int[] MONTHLY_LIMIT_MB_CODE = new int[]{0, 50, 100, 250, 500};
    private static final String[] MONTHLY_LIMIT_MB = new String[]{"Unlimited", "50", "100", "250", "500"};
    private static final int[] MISSION_LIMIT_MB_CODE = new int[]{0, 5, 10, 25, 50};
    private static final String[] MISSION_LIMIT_MB = new String[]{"Unlimited", "5", "10", "25", "50"};
    private static long[] TIME_IN_MILLIS = new long[]{DateUtils.MINUTE_IN_MILLIS * 5, DateUtils.MINUTE_IN_MILLIS * 10,
            DateUtils.MINUTE_IN_MILLIS * 30, DateUtils.HOUR_IN_MILLIS, DateUtils.HOUR_IN_MILLIS * 2};

    @BindView(R.id.languageSpinner)
    Spinner languageSpinner;
    @BindView(R.id.locationServicesToggleButton)
    CustomSwitch locationServicesToggleButton;
    @BindView(R.id.socialSharingToggleButton)
    CustomSwitch socialSharingToggleButton;
    @BindView(R.id.useOnlyWifiToggleButton)
    CustomSwitch useOnlyWifiToggleButton;
    @BindView(R.id.saveImageToggleButton)
    CustomSwitch saveImageToggleButton;
    @BindView(R.id.pushMessagesToggleButton)
    CustomSwitch pushMessagesToggleButton;
    @BindView(R.id.taskLimitSpinner)
    Spinner taskLimitSpinner;
    @BindView(R.id.monthLimitSpinner)
    Spinner monthLimitSpinner;
    @BindView(R.id.deadlineReminderToggleButton)
    CustomSwitch deadlineReminderToggleButton;
    @BindView(R.id.deadlineReminderSpinner)
    Spinner deadlineReminderSpinner;
    @BindView(R.id.deadlineReminderLayout)
    LinearLayout deadlineReminderLayout;
    @BindView(R.id.currentVersion)
    CustomTextView currentVersion;
    @BindView(R.id.closeAccount)
    CustomTextView closeAccount;

    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private SettingsMvpPresenter<SettingsMvpView> presenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_settings, null);
        ButterKnife.bind(this, view);
        initUI(view);
        presenter = new SettingsPresenter<>();
        setData();
        return view;
    }

    private void initUI(ViewGroup view) {
        MONTHLY_LIMIT_MB[0] = getString(R.string.unlimited);
        MISSION_LIMIT_MB[0] = getString(R.string.unlimited);
        closeAccount.setPaintFlags(closeAccount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ((TextView) view.findViewById(R.id.currentVersion)).setText(BuildConfig.VERSION_NAME + " (" +
                BuildConfig.JENKINS_BUILD_VERSION + ")");
        final MyAccount myAccount = App.getInstance().getMyAccount();
        currentVersion.setText(BuildConfig.VERSION_NAME + " (" + BuildConfig.JENKINS_BUILD_VERSION + ")");
        currentVersion.findViewById(R.id.currentVersion).setOnLongClickListener(v -> {
            startActivity(IntentUtils.getLogEmailIntent("Agent Log - " + myAccount.getId(), myAccount.getSupportEmail(), UIUtils.getLogs()));
            return false;
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.detachView();
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
            currentLanguageCode = LocaleUtils.DEFAULT_LANG;
        }

        ArrayAdapter languageAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_spinner, R.id.name, LocaleUtils.VISIBLE_LANGUAGE);
        languageSpinner.setAdapter(languageAdapter);

        int selectedItemPosition = 0;
        for (int i = 0; i < LocaleUtils.VISIBLE_LANGS_CODE.length; i++) {
            if (LocaleUtils.VISIBLE_LANGS_CODE[i].equals(currentLanguageCode)) {
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

        ArrayAdapter taskLimitAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_spinner, R.id.name,
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
                String selectedLanguageCode = LocaleUtils.VISIBLE_LANGS_CODE[languageSpinner.getSelectedItemPosition()];
                boolean languageChanged = LocaleUtils.setDefaultLanguage(selectedLanguageCode);
                if (languageChanged) {
                    UIUtils.showSimpleToast(getActivity(), R.string.success);
                    App.getInstance().initLocaleSettings();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_settings, menu);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View view = actionBar.getCustomView();
            ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.app_settings_title);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    private void logOut() {
        WriteDataHelper.prepareLogout(getActivity());
        Core.logout();
        getActivity().startActivity(IntentUtils.getLoginIntentForLogout(getActivity()));
        getActivity().finish();
        getActivity().sendBroadcast(new Intent().setAction(Keys.FINISH_MAIN_ACTIVITY));
    }

    @OnClick(R.id.closeAccount)
    public void onClick() {
        DefaultInfoDialog dialog = new DefaultInfoDialog(getActivity(), R.color.red, 0,
                getText(R.string.close_account_title),
                getText(R.string.close_account_text),
                R.string.cancel_big, R.string.close_account);
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                presenter.closeAccount();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onCheckedChange(CustomSwitch v, boolean isChecked) {
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
                presenter.allowPushNotifications(isChecked);
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

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(getActivity(), networkError.getErrorMessageRes());
    }

    @Override
    public void onAccountClosed() {
        logOut();
    }

    @Override
    public void onPushStatusChanged() {
        changeTaskReminderServiceStatus();
    }
}