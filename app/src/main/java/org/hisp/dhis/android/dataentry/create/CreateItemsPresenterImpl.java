package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import io.reactivex.disposables.CompositeDisposable;

class CreateItemsPresenterImpl implements CreateItemsPresenter {

    private final CreateItemsArgument argument;
    private final CreateItemsRepository repository;
    private final SchedulerProvider schedulerProvider;
    private final CompositeDisposable disposable;

    public CreateItemsPresenterImpl(@NonNull CreateItemsArgument argument,
                                    @NonNull CreateItemsRepository repository,
                                    @NonNull SchedulerProvider schedulerProvider) {
        this.argument = argument;
        this.repository = repository;
        this.schedulerProvider = schedulerProvider;
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof CreateItemsView) {
            CreateItemsView createItemsView = (CreateItemsView) view;
            if(argument.type() == CreateItemsArgument.Type.ENROLMENT_EVENT) {
                createItemsView.setCardViewsHintsEnrollment();
            }
/*
            disposable.add(createItemsView.cardViewOneEvent().mergeWith(createItemsView.cardViewTwoEvent())
                    .observeOn(schedulerProvider.io())
                    .subscribeOn(schedulerProvider.computation())
                    .subscribe(
                            event -> {
                                if (event.clear()) {
                                    createItemsView.
                                    if (event.id() == 0) {
                                        createItemsView.setCardViewTwoState("Some hint?", "");
                                    }
                                } else if (event.click()) {
                                    createItemsView.showDialog("ParentUid", "Hint?")
                                })
                            },
                            err -> {
                                throw new OnErrorNotImplementedException(err);
                            }

                    ));*/

        }
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }
}
