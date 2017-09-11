package org.hisp.dhis.android.dataentry.dashboard.navigation;

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
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.commons.ui.DividerDecoration;
import org.hisp.dhis.android.dataentry.commons.ui.FontTextView;
import org.hisp.dhis.android.dataentry.create.CreateItemsActivity;
import org.hisp.dhis.android.dataentry.create.CreateItemsArgument;
import org.hisp.dhis.android.dataentry.dashboard.DashboardNavigator;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

@SuppressWarnings({
        "PMD.ExcessiveImports"
})
public class NavigationFragment extends BaseFragment implements NavigationView {
    private static final String ARG_ENROLLMENT_UID = "enrollmentUid";
    private static final String ARG_TWO_PANE_LAYOUT = "twoPaneLayout";

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

    @BindView(R.id.linear_layout_empty_state)
    LinearLayout emptyState;

    @Inject
    NavigationPresenter navigationPresenter;

    @Inject
    DashboardNavigator dashboardNavigator;

    private NavigationAdapter navigationAdapter;
    private Boolean twoPaneLayout;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static NavigationFragment newInstance(
            @NonNull NavigationViewArguments navigationViewArguments) {
        Bundle args = new Bundle();
        args.putString(ARG_ENROLLMENT_UID, navigationViewArguments.enrollmentUid());
        args.putBoolean(ARG_TWO_PANE_LAYOUT, navigationViewArguments.twoPaneLayout());

        NavigationFragment fragment = new NavigationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind(this, view);

        setupRecyclerView();
        setupActionBar();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        String enrollmentUid = isNull(getArguments()
                .getString(ARG_ENROLLMENT_UID), "enrollmentUid == null");
        twoPaneLayout = isNull(getArguments()
                .getBoolean(ARG_TWO_PANE_LAYOUT), "enrollmentUid == null");
        getUserComponent()
                .plus(new NavigationModule(getActivity(), enrollmentUid, twoPaneLayout))
                .inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationPresenter.onAttach(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationPresenter.onDetach();
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
                    CreateItemsArgument.forEnrollmentEvent(program, "", isNull(getArguments()
                            .getString(ARG_ENROLLMENT_UID), "enrollmentUid == null")));
            getActivity().startActivity(createItemsIntent);
        };
    }

    @NonNull
    @Override
    public Consumer<List<EventViewModel>> renderEvents() {
        return events -> {
            emptyState.setVisibility(events.isEmpty() ? View.VISIBLE : View.GONE);
            navigationAdapter.swap(events);
        };
    }

    @NonNull
    @Override
    public Consumer<String> renderTitle() {
        return title -> toolbar.setTitle(title);
    }

    @NonNull
    @Override
    public Consumer<Pair<String, String>> renderAttributes() {
        return attributes -> {
            firstAttribute.setText(attributes.val0());
            secondAttribute.setText(attributes.val1());
        };
    }

    @OnClick({
            R.id.appbar_layout
    })
    void showProfile() {
        dashboardNavigator.navigateToEnrollment(
                getArguments().getString(ARG_ENROLLMENT_UID));
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
        // hide empty state initially
        emptyState.setVisibility(View.GONE);

        navigationAdapter = new NavigationAdapter(eventViewModel -> {
            dashboardNavigator.navigateToEvent(eventViewModel.uid());
            if (twoPaneLayout) {
                markEventAsSelected(eventViewModel);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(navigationAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(
                ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
    }

    private void markEventAsSelected(EventViewModel eventViewModel) {
        navigationAdapter.setSelectedEvent(eventViewModel);
        navigationAdapter.notifyDataSetChanged();
    }
}