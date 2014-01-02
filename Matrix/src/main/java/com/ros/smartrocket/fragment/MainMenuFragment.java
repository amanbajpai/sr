package com.ros.smartrocket.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ros.smartrocket.*;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.UIUtils;

public class MainMenuFragment extends Fragment implements OnClickListener, NetworkOperationListenerInterface {
    //private static final String TAG = MainMenuFragment.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ViewGroup view;

    private ResponseReceiver localReceiver;
    private IntentFilter intentFilter;
    private TextView balanceTextView;
    private TextView levelTextView;
    private ProgressBar levelProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_main_menu, null);

        balanceTextView = (TextView) view.findViewById(R.id.balanceTextView);
        levelTextView = (TextView) view.findViewById(R.id.levelTextView);
        levelProgressBar = (ProgressBar) view.findViewById(R.id.levelProgressBar);

        view.findViewById(R.id.findTasksButton).setOnClickListener(this);
        view.findViewById(R.id.myTasksButton).setOnClickListener(this);
        view.findViewById(R.id.myAccountButton).setOnClickListener(this);
        view.findViewById(R.id.settingsButton).setOnClickListener(this);
        view.findViewById(R.id.aboutMatrixButton).setOnClickListener(this);
        view.findViewById(R.id.shareButton).setOnClickListener(this);

        localReceiver = new ResponseReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.REFRESH_MAIN_MENU);

        getActivity().registerReceiver(localReceiver, intentFilter);

        setData(App.getInstance().getMyAccount());
        apiFacade.getMyAccount(getActivity());

        return view;
    }

    public void setData(MyAccount myAccount) {
        balanceTextView.setText(myAccount.getBalance() + " $");
        levelTextView.setText(String.valueOf(myAccount.getLevel()));
        levelProgressBar.setMax(myAccount.getExperience() + myAccount.getToNextLevel());
        levelProgressBar.setProgress(myAccount.getExperience());
    }

    public class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Keys.REFRESH_MAIN_MENU)) {
                apiFacade.getMyAccount(getActivity());
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.GET_MY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                MyAccount myAccount = (MyAccount) operation.getResponseEntities().get(0);
                setData(myAccount);
            }
        } else {
            UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Fragment fragment;

        switch (v.getId()) {
            case R.id.findTasksButton:
                bundle.putString(Keys.CONTENT_TYPE, Keys.FIND_TASK);

                fragment = new AllTaskFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).startFragment(fragment);
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.myTasksButton:
                bundle.putString(Keys.CONTENT_TYPE, Keys.MY_TASK);

                fragment = new AllTaskFragment();
                fragment.setArguments(bundle);

                ((MainActivity) getActivity()).startFragment(fragment);
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.myAccountButton:
                ((MainActivity) getActivity()).startFragment(new MyAccountFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.settingsButton:
                ((MainActivity) getActivity()).startFragment(new SettingsFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.aboutMatrixButton:
                ((MainActivity) getActivity()).startFragment(new AboutMatrixFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.shareButton:
                ((MainActivity) getActivity()).startFragment(new ShareAndReferFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            default:
                break;
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(localReceiver);
        }
    }
}
