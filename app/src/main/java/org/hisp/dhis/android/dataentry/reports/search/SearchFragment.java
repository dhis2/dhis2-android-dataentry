package org.hisp.dhis.android.dataentry.reports.search;

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

import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;
import com.jakewharton.rxbinding2.view.RxView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.reports.ReportViewModel;
import org.hisp.dhis.android.dataentry.reports.ReportsAdapter;
import org.hisp.dhis.android.dataentry.reports.ReportsNavigator;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public final class SearchFragment extends BaseFragment
        implements SearchView, ReportsAdapter.OnReportViewModelClickListener {
    private static final String ARG_ARGUMENTS = "arg:arguments";

    @BindView(R.id.recyclerview_search_results)
    RecyclerView recyclerViewResults;

    @BindView(R.id.fab_create)
    FloatingActionButton buttonCreateReport;

    @BindView(R.id.edittext_search)
    android.support.v7.widget.SearchView searchReports;

    @Inject
    ReportsNavigator reportsNavigator;

    @Inject
    SearchPresenter presenter;

    ReportsAdapter reportsAdapter;

    @NonNull
    public static SearchFragment create(@NonNull SearchArguments searchArguments) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_ARGUMENTS, searchArguments);

        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(arguments);

        return searchFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getUserComponent()
                .plus(new SearchModule(getActivity(), getSearchArguments()))
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
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
    public Observable<SearchViewQueryTextEvent> searchBoxActions() {
        return RxSearchView.queryTextChangeEvents(searchReports);
    }

    @NonNull
    @Override
    public Consumer<List<ReportViewModel>> renderSearchResults() {
        return reportViewModels -> reportsAdapter.swapData(reportViewModels);
    }

    @NonNull
    @Override
    public Consumer<Boolean> renderCreateButton() {
        return isVisible -> {
            if (isVisible) {
                buttonCreateReport.show();
            } else {
                buttonCreateReport.hide();
            }
        };
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

    @NonNull
    private SearchArguments getSearchArguments() {
        return isNull(getArguments().getParcelable(ARG_ARGUMENTS),
                "ReportsArguments must be supplied");
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        reportsAdapter = new ReportsAdapter(getContext(), this);
        recyclerViewResults.setLayoutManager(layoutManager);
        recyclerViewResults.setAdapter(reportsAdapter);
    }
}
