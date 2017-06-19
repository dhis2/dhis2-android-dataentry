package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.flowables.ConnectableFlowable;
import rx.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

class FormPresenterImpl implements FormPresenter {

    @NonNull
    private final FormViewArguments formViewArguments;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final FormRepository formRepository;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    FormPresenterImpl(@NonNull FormViewArguments formViewArguments,
                      @NonNull SchedulerProvider schedulerProvider,
                      @NonNull FormRepository formRepository) {
        this.formViewArguments = formViewArguments;
        this.formRepository = formRepository;
        this.schedulerProvider = schedulerProvider;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull FormView view) {
        isNull(view, "FormView must not be null");

        String reportUid = formViewArguments.uid();

        compositeDisposable.add(formRepository.title(reportUid)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(view.renderTitle(), Timber::e));

        compositeDisposable.add(formRepository.reportDate(reportUid)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(view.renderReportDate(), Timber::e));

        compositeDisposable.add(formRepository.sections(reportUid)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(view.renderSectionViewModels(), Timber::e));

        compositeDisposable.add(view.reportDateChanged()
                .subscribeOn(schedulerProvider.ui())
                .observeOn(schedulerProvider.io())
                .subscribe(formRepository.storeReportDate(reportUid), Timber::e));

        ConnectableFlowable<ReportStatus> statusObservable = formRepository.reportStatus(reportUid)
                .distinctUntilChanged()
                .publish();

        compositeDisposable.add(statusObservable
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .skip(1)
                .subscribe(status -> view.renderStatusChangeSnackBar(status), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        compositeDisposable.add(statusObservable
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(view.renderStatus(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        compositeDisposable.add(statusObservable.connect());

        compositeDisposable.add(view.eventStatusChanged()
                .filter(eventStatus -> formViewArguments.type() != FormViewArguments.Type.ENROLLMENT)
                .subscribeOn(schedulerProvider.ui())
                .observeOn(schedulerProvider.io())
                .subscribe(formRepository.storeReportStatus(reportUid), Timber::e));
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}