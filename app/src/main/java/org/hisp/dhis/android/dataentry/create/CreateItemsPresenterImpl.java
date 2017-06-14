package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;

import static org.hisp.dhis.android.dataentry.create.CreateItemsFragment.FIRST_CARDVIEW;
import static org.hisp.dhis.android.dataentry.create.CreateItemsFragment.SECOND_CARDVIEW;

class CreateItemsPresenterImpl implements CreateItemsPresenter {
    private final int DEBOUNCE = 153;
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
            disposable.add(createItemsView.cardViewClearEvent(FIRST_CARDVIEW)
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> {
                        createItemsView.setCardViewText(FIRST_CARDVIEW, "");
                        createItemsView.setCardViewText(SECOND_CARDVIEW, "");
                    }, err -> {
                                throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.cardViewClearEvent(SECOND_CARDVIEW)
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.setCardViewText(SECOND_CARDVIEW, ""), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.cardViewClickEvent(FIRST_CARDVIEW)
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.showDialog(FIRST_CARDVIEW), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.cardViewClickEvent(SECOND_CARDVIEW)
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.showDialog(SECOND_CARDVIEW), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.createButtonEvent()
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .observeOn(schedulerProvider.ui())
                    .subscribeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.createItem(), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
        }
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }
}
