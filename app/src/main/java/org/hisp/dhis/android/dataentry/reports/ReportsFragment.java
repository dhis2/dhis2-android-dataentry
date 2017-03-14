package org.hisp.dhis.android.dataentry.reports;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

public final class ReportsFragment extends BaseFragment implements ReportsView {
    private static final String ARG_FORM_UID = "arg:formUid";

    @BindView(R.id.recyclerview_reports)
    RecyclerView recyclerViewReports;

    @Inject
    ReportsPresenter presenter;
    ReportsAdapter reportsAdapter;

    public static ReportsFragment create(@NonNull String formUid) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_FORM_UID, formUid);

        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(arguments);

        return reportsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // inject dependencies
        ((DhisApp) context.getApplicationContext()).userComponent()
                .plus(new ReportsModule(getFormUid()))
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

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        reportsAdapter = new ReportsAdapter(getContext());
        recyclerViewReports.setLayoutManager(layoutManager);
        recyclerViewReports.setAdapter(reportsAdapter);
    }

    private String getFormUid() {
        return getArguments().getString(ARG_FORM_UID, "");
    }
}
