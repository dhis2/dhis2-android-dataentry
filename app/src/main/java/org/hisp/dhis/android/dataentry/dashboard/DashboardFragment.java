package org.hisp.dhis.android.dataentry.dashboard;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public class DashboardFragment extends BaseFragment implements DashboardView {

    private static final String ARG_ENROLLMENT_UID = "enrollmentUid";

    @Inject
    DashboardPresenter dashboardPresenter;

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

    @NonNull
    @Override
    public Consumer<List<EventViewModel>> renderEvents() {
        return events -> Log.d("Events", "Size: " + events.size());
    }

    @NonNull
    @Override
    public Consumer<List<String>> renderAttributes() {
        return attributes -> {
            for (String attribute : attributes) {
                Log.d("Attribute", attribute);
            }
        };
    }
}
