package org.hisp.dhis.android.dataentry.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.commons.ui.DividerDecoration;
import org.hisp.dhis.android.dataentry.commons.ui.FontTextView;
import org.hisp.dhis.android.dataentry.create.CreateItemsActivity;
import org.hisp.dhis.android.dataentry.create.CreateItemsArgument;
import org.hisp.dhis.android.dataentry.commons.utils.Preconditions;
import org.hisp.dhis.android.dataentry.form.FormActivity;
import org.hisp.dhis.android.dataentry.form.FormViewArguments;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

@SuppressWarnings("PMD.ExcessiveImports")
public class DashboardFragment extends BaseFragment implements DashboardView {
    private static final String ARG_ENROLLMENT_UID = "enrollmentUid";

    @Inject
    DashboardPresenter dashboardPresenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.first_attribute)
    FontTextView firstAttribute;

    @BindView(R.id.second_attribute)
    FontTextView secondAttribute;

    @BindView(R.id.fab)
    FloatingActionButton createEventsButton;

    @BindView(R.id.event_list)
    RecyclerView recyclerView;

    private DashboardAdapter dashboardAdapter;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind(this, view);
        setupRecyclerView();
        setupActionBar();
    }

    private void setupActionBar() {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setHomeButtonEnabled(true);
            }
        }
    }

    private void setupRecyclerView() {
        dashboardAdapter = new DashboardAdapter(eventViewModel -> {
            Intent intent = FormActivity.create(getActivity(), FormViewArguments.createForEvent(eventViewModel.uid()));
            startActivity(intent);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dashboardAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(
                ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        String enrollmentUid = Preconditions.isNull(getArguments()
                .getString(ARG_ENROLLMENT_UID), "enrollmentUid == null");
        getUserComponent()
                .plus(new DashboardModule(enrollmentUid))
                .inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        dashboardPresenter.onAttach(this);
    }

    @OnClick({R.id.appbar_layout, R.id.edit_profile_button})
    void showProfile() {
        startActivity(FormActivity.create(getActivity(), FormViewArguments
                .createForEnrollment(getArguments().getString(ARG_ENROLLMENT_UID))));
    }

    @Override
    public void onPause() {
        super.onPause();
        dashboardPresenter.onDetach();
    }

    @NonNull
    @Override
    public Observable<Object> createEventActions() {
        return RxView.clicks(createEventsButton);
    }

    @NonNull
    @Override
    public Consumer<String> navigateToCreateScreen() {
        return program -> {
            Intent createItemsIntent = CreateItemsActivity.createIntent(getActivity(),
                    CreateItemsArgument.forEnrollmentEvent(program, "",
                            getArguments().getString(ARG_ENROLLMENT_UID)));
            getActivity().startActivity(createItemsIntent);
        };
    }

    @NonNull
    @Override
    public Consumer<List<EventViewModel>> renderEvents() {
        return events -> dashboardAdapter.swap(events);
    }

    @NonNull
    @Override
    public Consumer<Pair<String, String>> renderAttributes() {
        return attributes -> {
            firstAttribute.setText(attributes.val0());
            secondAttribute.setText(attributes.val1());
        };
    }
}