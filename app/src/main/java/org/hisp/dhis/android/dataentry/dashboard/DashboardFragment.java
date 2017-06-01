package org.hisp.dhis.android.dataentry.dashboard;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public class DashboardFragment extends BaseFragment implements DashboardView {

    private static final String ARG_ENROLLMENT_UID = "enrollmentUid";

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String enrollmentUid) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ENROLLMENT_UID, enrollmentUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        String enrollmentUid = isNull(getArguments()
                .getString(ARG_ENROLLMENT_UID), "enrollmentUid == null");

        getUserComponent()
                .plus(new DashboardModule(enrollmentUid))
                .inject(this);
    }
}
