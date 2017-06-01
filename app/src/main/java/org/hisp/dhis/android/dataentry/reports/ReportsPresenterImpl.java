package org.hisp.dhis.android.dataentry.reports;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import io.reactivex.disposables.CompositeDisposable;
import rx.exceptions.OnErrorNotImplementedException;

final class ReportsPresenterImpl implements ReportsPresenter {

    @NonNull
    private final ReportsArguments reportsArguments;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final ReportsRepository reportsRepository;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    ReportsPresenterImpl(@NonNull ReportsArguments reportsArguments,
            @NonNull SchedulerProvider provider,
            @NonNull ReportsRepository reportsRepository) {
        this.reportsArguments = reportsArguments;
        this.schedulerProvider = provider;
        this.reportsRepository = reportsRepository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof ReportsView) {
            ReportsView reportsView = (ReportsView) view;
            compositeDisposable.add(reportsView.createReportsActions()
                    .map(action -> reportsArguments.entityUid())
                    .subscribe(reportsView.createReport(), throwable -> {
                        throw new OnErrorNotImplementedException(throwable);
                    }));
            compositeDisposable.add(reportsRepository.reports(reportsArguments.entityUid())
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(reportsView.renderReportViewModels(), throwable -> {
                        throw new OnErrorNotImplementedException(throwable);
                    }));
        }
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}
