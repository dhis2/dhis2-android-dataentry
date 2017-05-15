package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Predicate;
import timber.log.Timber;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

class FormPresenterImpl implements FormPresenter {

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final FormRepository formRepository;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    FormPresenterImpl(@NonNull SchedulerProvider schedulerProvider, @NonNull FormRepository formRepository) {
        this.formRepository = formRepository;
        this.schedulerProvider = schedulerProvider;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        isNull(view, "FormView must not be null");

        if (view instanceof FormView) {
            FormView formView = (FormView) view;

            String reportUid = formView.formViewArguments().uid();

            compositeDisposable.add(formRepository.title(reportUid)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            formView.renderTitle(),
                            Timber::e));

            compositeDisposable.add(formRepository.reportDate(reportUid)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            formView.renderReportDate(),
                            Timber::e));

            compositeDisposable.add(formRepository.sections(reportUid)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            formView.renderSectionViewModels(),
                            Timber::e));

            compositeDisposable.add(formView.reportDateChanged()
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.io())
                    .subscribe(
                            formRepository.storeReportDate(reportUid),
                            Timber::e));
            
            compositeDisposable.add(formRepository.reportStatus(reportUid)
                    .subscribeOn(schedulerProvider.io())
                    .distinctUntilChanged()
                    .filter(eventStatus -> formView.formViewArguments().type() == FormViewArguments.Type.EVENT)
                    .zipWith(Flowable.range(0, Integer.MAX_VALUE),
                            (eventStatus, integer) -> Pair.create(integer, eventStatus))
                    .subscribeOn(schedulerProvider.ui())
                    .map(pair -> {
                        if (pair.val0() != 0) {
                            formView.renderStatusChangeSnackBar(pair.val1());
                        }
                        return pair.val1();
                    })
                    .observeOn(schedulerProvider.ui())
                    .subscribe(formView.renderStatus(), Timber::e));

            compositeDisposable.add(formView.eventStatusChanged()
                    .filter(handleEnrollmentFabClick(formView))
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.io())
                    .subscribe(
                            formRepository.storeEventStatus(reportUid),
                            Timber::e));
        }
    }

    @NonNull
    private Predicate<EventStatus> handleEnrollmentFabClick(FormView formView) {
        return eventStatus -> {
            if (formView.formViewArguments().type() == FormViewArguments.Type.ENROLLMENT) {
                // go to TEI dashboard screen
                return false;
            }
            return true;
        };
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}