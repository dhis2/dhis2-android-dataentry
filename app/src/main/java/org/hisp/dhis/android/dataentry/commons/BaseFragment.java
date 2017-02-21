package org.hisp.dhis.android.dataentry.commons;

import android.support.v4.app.Fragment;

import org.hisp.dhis.android.dataentry.DhisApp;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((DhisApp) getActivity().getApplicationContext())
                .refWatcher().watch(this);
    }
}
