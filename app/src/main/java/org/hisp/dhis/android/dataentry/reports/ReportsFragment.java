package org.hisp.dhis.android.dataentry.reports;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public final class ReportsFragment extends BaseFragment
        implements ReportsView, ReportsAdapter.OnReportViewModelClickListener {
    private static final String ARG_ARGUMENTS = "arg:arguments";

    @BindView(R.id.recyclerview_reports)
    RecyclerView recyclerViewReports;

    @BindView(R.id.fab_create)
    FloatingActionButton buttonCreateReport;

    @Inject
    ReportsPresenter presenter;

    @Inject
    ReportsNavigator reportsNavigator;

    ReportsAdapter reportsAdapter;

    @NonNull
    public static ReportsFragment create(@NonNull ReportsArguments reportsArguments) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_ARGUMENTS, reportsArguments);

        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(arguments);

        return reportsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ReportsArguments reportsArguments = getArguments().getParcelable(ARG_ARGUMENTS);
        if (reportsArguments == null) {
            throw new IllegalStateException("ReportsArguments must be supplied");
        }

        ((DhisApp) context.getApplicationContext()).userComponent()
                .plus(new ReportsModule(getActivity(), reportsArguments))
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);
        setUpRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onAttach(this);
    }

    @NonNull
    @Override
    public Consumer<List<ReportViewModel>> renderReportViewModels() {
        return reportViewModels -> reportsAdapter.swapData(reportViewModels);
    }

    @NonNull
    @Override
    public Observable<Object> createReportsActions() {
        return RxView.clicks(buttonCreateReport);
    }

    @NonNull
    @Override
    public Consumer<String> createReport() {
        return action -> reportsNavigator.createFor(action);
    }

    @Override
    public void onClick(@NonNull ReportViewModel reportViewModel) {
        reportsNavigator.navigateTo(reportViewModel.id());
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        reportsAdapter = new ReportsAdapter(getContext(), this);
        recyclerViewReports.setLayoutManager(layoutManager);
        recyclerViewReports.setAdapter(reportsAdapter);
    }
}
