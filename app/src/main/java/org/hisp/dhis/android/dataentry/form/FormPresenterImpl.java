package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.utils.DateUtils;

import java.text.ParseException;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.observables.ConnectableObservable;
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
                .map(date -> {
                    try {
                        return DateUtils.uiDateFormat().format(DateUtils.databaseDateFormat().parse(date));
                    } catch (ParseException e) {
                        Timber.e(e, "DashboardRepository: Unable to parse date. Expected format: " +
                                DateUtils.databaseDateFormat().toPattern() + ". Input: " + date);
                        return date;
                    }
                })
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

        ConnectableObservable<ReportStatus> statusChangeObservable = view.eventStatusChanged()
                .distinctUntilChanged()
                .publish();

        compositeDisposable.add(statusChangeObservable
                .filter(eventStatus -> formViewArguments.type() != FormViewArguments.Type.ENROLLMENT)
                .subscribeOn(schedulerProvider.ui())
                .observeOn(schedulerProvider.io())
                .subscribe(formRepository.storeReportStatus(reportUid), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        Observable<String> enrollmentDoneStream = statusChangeObservable
                .filter(eventStatus -> formViewArguments.type() == FormViewArguments.Type.ENROLLMENT)
                .map(reportStatus -> formViewArguments.uid())
                .observeOn(schedulerProvider.io()).share();

        compositeDisposable.add(enrollmentDoneStream
                .subscribeOn(schedulerProvider.io())
                .subscribe(formRepository.autoGenerateEvent(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        compositeDisposable.add(enrollmentDoneStream
                .subscribeOn(schedulerProvider.ui())
                .subscribe(view.finishEnrollment(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        compositeDisposable.add(statusChangeObservable.connect());
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}