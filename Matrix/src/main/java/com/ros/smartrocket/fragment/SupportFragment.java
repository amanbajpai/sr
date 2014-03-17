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
import android.widget.TextView;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.IntentUtils;

/**
 * Fragment for display About information
 */
public class SupportFragment extends Fragment implements OnClickListener {
    private static final String TAG = SupportFragment.class.getSimpleName();
    private ViewGroup view;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_support, null);

        view.findViewById(R.id.termAndConditionsButton).setOnClickListener(this);
        view.findViewById(R.id.knowledgeBaseButton).setOnClickListener(this);
        view.findViewById(R.id.sendMessageButton).setOnClickListener(this);

        ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.termAndConditionsButton:
                getActivity().startActivity(IntentUtils.getTermsAndConditionIntent(getActivity()));
                break;
            case R.id.knowledgeBaseButton:

                break;
            case R.id.sendMessageButton:
                getActivity().startActivity(IntentUtils.getEmailIntent("", Config.DEV_EMAIL, null));
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
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.support_title);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
