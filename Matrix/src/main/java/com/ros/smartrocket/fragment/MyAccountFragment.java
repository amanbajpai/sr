package com.ros.smartrocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.helpers.WriteDataHelper;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Profile fragment for current user
 */
public class MyAccountFragment extends Fragment implements NetworkOperationListenerInterface {
    private static final String TAG = MyAccountFragment.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ViewGroup view;

    private TextView totalEarnings;
    private TextView balance;
    private EditText payPalEditText;
    private TextView agentLevel;
    private TextView agentExperience;
    private TextView toNextLevel;
    private Button btnTransfer;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_my_account, null);

        totalEarnings = (TextView) view.findViewById(R.id.totalEarnings);
        balance = (TextView) view.findViewById(R.id.balance);
        payPalEditText = (EditText) view.findViewById(R.id.payPalEditText);
        agentLevel = (TextView) view.findViewById(R.id.agentLevel);
        agentExperience = (TextView) view.findViewById(R.id.agentExperience);
        toNextLevel = (TextView) view.findViewById(R.id.toNextLevel);
        btnTransfer = (Button) view.findViewById(R.id.transferFundsButton);

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesManager pm = PreferencesManager.getInstance();
                if (pm.isGCMIdRegisteredOnServer()) {
                    L.i(TAG, "Send GCM to server");
                    String regId = pm.getGCMRegistrationId();
                    JSONObject obj = new JSONObject();
                    JSONObject obj2 = new JSONObject();
                    try {
                        obj2.put("title", "SmartRocket");
                        obj2.put("subtitle", "Welcome to SmartRocket world!");
                        obj.put("message", obj2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String test = obj.toString();
                    APIFacade.getInstance().testGCMPushNotification(getActivity(), 6, 22);
                } else {
                    L.i(TAG, "NOT registerted GCM at server");
                }
            }
        });

        setData(App.getInstance().getMyAccount());
        apiFacade.getMyAccount(getActivity());

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            setData(App.getInstance().getMyAccount());
            apiFacade.getMyAccount(getActivity());
        }
    }

    public void setData(MyAccount myAccount) {
        totalEarnings.setText(myAccount.getTotalEarnings() + " $");
        balance.setText(myAccount.getBalance() + " $");
        //payPalEditText.setText(String.valueOf(10));
        agentLevel.setText(String.valueOf(myAccount.getLevel()));
        agentExperience.setText(Html.fromHtml(String.format(getActivity().getString(R.string.x_points),
                myAccount.getExperience())));
        toNextLevel.setText(Html.fromHtml(String.format(getActivity().getString(R.string.x_points),
                myAccount.getToNextLevel())));
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_MY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                MyAccount myAccount = (MyAccount) operation.getResponseEntities().get(0);
                setData(myAccount);
                UIUtils.showSimpleToast(getActivity(), "Success");
            }
        } else {
            UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().setTitle(R.string.my_account_title);

        inflater.inflate(R.menu.menu_my_account, menu);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);

        super.onCreateOptionsMenu(menu, inflater);
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
}
