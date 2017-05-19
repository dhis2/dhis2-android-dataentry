package org.hisp.dhis.android.dataentry.dashboard;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;

public class DashboardFragment extends BaseFragment implements DashboardView {

    private static final String ARG_TEI_UID = "teiUid";

    private String teiUid;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String teiUid) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEI_UID, teiUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teiUid = getArguments().getString(ARG_TEI_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getUserComponent().plus(new DashboardModule()).inject(this);
    }
}
