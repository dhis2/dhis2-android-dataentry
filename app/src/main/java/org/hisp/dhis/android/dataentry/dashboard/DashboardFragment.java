package org.hisp.dhis.android.dataentry.dashboard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;

public class DashboardFragment extends Fragment {

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

}
