package com.matrix.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import com.matrix.R;
import com.matrix.utils.L;

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
        view = (ViewGroup) inflater.inflate(R.layout.fragment_share_and_refer, null);

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

        super.onCreateOptionsMenu(menu, inflater);
    }
}