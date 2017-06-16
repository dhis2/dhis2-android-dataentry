package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

class CreateItemsPresenterImpl implements CreateItemsPresenter {
    private final int SELECTIONS = 2; // number of CardViews in the view.
    private final int DEBOUNCE = 153;
    public static final int FIRST_SELECTION = 0;
    public static final int SECOND_SELECTION = 1;
    private final CreateItemsArgument argument;
    private final CreateItemsRepository repository;
    private final SchedulerProvider schedulerProvider;
    private final CompositeDisposable disposable;

    private final String[] selectedUids;

    public CreateItemsPresenterImpl(@NonNull CreateItemsArgument argument,
                                    @NonNull CreateItemsRepository repository,
                                    @NonNull SchedulerProvider schedulerProvider) {
        this.argument = argument;
        this.repository = repository;
        this.schedulerProvider = schedulerProvider;
        this.disposable = new CompositeDisposable();
        this.selectedUids = new String[SELECTIONS];
    }

    @Override
    public void onAttach(@NonNull View view) {
        //TODO: resume state?
        if (view instanceof CreateItemsView) {
            CreateItemsView createItemsView = (CreateItemsView) view;


            disposable.add(createItemsView.selection1ClearEvent()
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> {
                        createItemsView.setSelection(FIRST_SELECTION, "", "");
                        createItemsView.setSelection(SECOND_SELECTION, "", "");
                    }, err -> {
                                throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.selectionChanges(FIRST_SELECTION)
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.setSelection(SECOND_SELECTION, "", ""), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.selection2ClearEvent()
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.setSelection(SECOND_SELECTION, "", ""), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.selection1ClickEvents()
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.showDialog1(argument.uid()), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.selection2ClickEvents()
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    //TODO: test for this in the tests:
                    .filter(event -> !createItemsView.getSelectionState(FIRST_SELECTION).uid().isEmpty())
                    .subscribe(event -> {
                                if (argument.type() == CreateItemsArgument.Type.EVENT ||
                                        argument.type() == CreateItemsArgument.Type.ENROLMENT_EVENT) {
                                    createItemsView.showDialog2(argument.uid());
                                } else if (argument.type() == CreateItemsArgument.Type.TEI ||
                                        argument.type() == CreateItemsArgument.Type.ENROLLMENT) {
                                    createItemsView.showDialog2(
                                            createItemsView.getSelectionState(FIRST_SELECTION).uid());
                                }
                            },
                            err -> {
                                throw new OnErrorNotImplementedException(err);
                            }));
            disposable.add(createItemsView.createButtonClick()
                    .debounce(DEBOUNCE, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    //TODO: test for this in the tests:
                    .filter(event -> (!event.val0().isEmpty()) && (!event.val1().isEmpty()))
                    .observeOn(schedulerProvider.io())
                    .switchMap(event -> {
                        Timber.d("Event = " + event.toString());
                      /*  if (argument.type() == CreateItemsArgument.Type.EVENT ||
                                argument.type() == CreateItemsArgument.Type.ENROLMENT_EVENT) {
                            return repository.save(event.val0(), event.val1());
                        }  else if (argument.type() == CreateItemsArgument.Type.TEI ||
                                argument.type() == CreateItemsArgument.Type.ENROLLMENT) {
                        }*/
                        return repository.save(event.val0(), event.val1());
                            }
                    )
                    .observeOn(schedulerProvider.ui())
                    .subscribe(uid -> {
                        Timber.d("Uid = " + uid);
                        createItemsView.navigateNext(uid);
                    }, err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
        }
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }
}
