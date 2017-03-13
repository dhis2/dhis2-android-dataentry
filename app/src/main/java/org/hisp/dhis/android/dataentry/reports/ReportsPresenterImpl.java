package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

final class ReportsPresenterImpl implements ReportsPresenter {

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final ReportsRepository reportsRepository;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    ReportsPresenterImpl(@NonNull SchedulerProvider provider,
            @NonNull ReportsRepository reportsRepository) {
        this.schedulerProvider = provider;
        this.reportsRepository = reportsRepository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof ReportsView) {
            ReportsView reportsView = (ReportsView) view;
            compositeDisposable.add(reportsRepository.reports()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(reportsView.renderReportViewModels(), Timber::e));
        }
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}
