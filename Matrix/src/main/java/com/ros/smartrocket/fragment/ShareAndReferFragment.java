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
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;

/**
 * Share app info fragment
 */
public class ShareAndReferFragment extends Fragment implements OnClickListener {
    private static final String TAG = ShareAndReferFragment.class.getSimpleName();
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

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_share_and_refer, null);

        view.findViewById(R.id.emailButton).setOnClickListener(this);
        view.findViewById(R.id.messageButton).setOnClickListener(this);
        view.findViewById(R.id.twitterButton).setOnClickListener(this);
        view.findViewById(R.id.facebookButton).setOnClickListener(this);

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            //TODO Move to fragment second time
            L.i(TAG, "TODO Move to fragment second time");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailButton:
                getActivity().startActivity(IntentUtils.getEmailIntent(Config.DEV_EMAIL, null));
                break;
            case R.id.messageButton:

                break;
            case R.id.twitterButton:

                break;
            case R.id.facebookButton:

                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().setTitle(R.string.share_and_refer_title);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
