package com.ros.smartrocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Share app info fragment
 */
public class CashingOutFragment extends Fragment implements OnClickListener {
    //private static final String TAG = CashingOutFragment.class.getSimpleName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_cashing_out, null);

        MyAccount myAccount = App.getInstance().getMyAccount();

        Button cashOutButton = (Button) view.findViewById(R.id.cashOutButton);
        TextView currentBalance = (TextView) view.findViewById(R.id.currentBalance);
        TextView minBalance = (TextView) view.findViewById(R.id.minBalance);

        /*if (myAccount.getBalance() > myAccount.getMinBalance()) {
            cashOutButton.setEnabled(false);*/
        minBalance.setVisibility(View.VISIBLE);

        //TODO Set min balance
        minBalance.setText(getActivity().getString(R.string.cashing_out_minimum_balance/*,
                minBalance.getLevelName()*/));
        //} else {
        cashOutButton.setOnClickListener(this);
        // }

        currentBalance.setText(UIUtils.getBalanceOrPrice(getActivity(), myAccount.getBalance(),
                myAccount.getCurrencySign()));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cashOutButton:
                getActivity().startActivity(IntentUtils.getCashOutConfirmationIntent(getActivity()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_title);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
