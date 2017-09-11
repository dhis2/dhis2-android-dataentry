package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.commons.utils.OnErrorHandler;
import org.hisp.dhis.android.dataentry.selection.OrganisationUnitRepositoryImpl;
import org.hisp.dhis.android.dataentry.selection.SelectionViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.flowables.ConnectableFlowable;

@SuppressWarnings({
        "PMD.CyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.ExcessiveMethodLength"
})
class CreateItemsPresenterImpl implements CreateItemsPresenter {
    public static final int DEBOUNCE_TIME = 110;
    private static final int FIRST_SELECTION = 0;
    private static final int SECOND_SELECTION = 1;
    private final CreateItemsArgument argument;
    private final CreateItemsRepository repository;
    private final OrganisationUnitRepositoryImpl orgRepository;
    private final SchedulerProvider schedulerProvider;
    private final CompositeDisposable disposable;

    public CreateItemsPresenterImpl(@NonNull CreateItemsArgument argument,
                                    @NonNull CreateItemsRepository repository,
                                    @NonNull OrganisationUnitRepositoryImpl orgRepository,
                                    @NonNull SchedulerProvider schedulerProvider) {

        this.argument = argument;
        this.repository = repository;
        this.orgRepository = orgRepository;
        this.schedulerProvider = schedulerProvider;
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof CreateItemsView) {
            CreateItemsView createItemsView = (CreateItemsView) view;
            //pre-select and hide program if type is EVENT:
            disposable.add(Observable.just(argument.type() == CreateItemsArgument.Type.EVENT)
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .filter(isEvent -> isEvent)
                    .subscribe(value -> {
                        createItemsView.setSelection(SECOND_SELECTION, argument.uid(), "");
                        createItemsView.setVisibilitySelection1(false);
                    }, OnErrorHandler.create()));
            //clearing of selections:
            disposable.add(createItemsView.selection1ClearEvent()
                    .toFlowable(BackpressureStrategy.LATEST)
                    .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> {
                        createItemsView.setSelection(FIRST_SELECTION, "", "");
                        if (argument.type() != CreateItemsArgument.Type.EVENT) {
                            createItemsView.setSelection(SECOND_SELECTION, "", "");
                        }
                    }, OnErrorHandler.create()));

            ConnectableFlowable<List<SelectionViewModel>> connectableFlowable = orgRepository.search("")
                    .onBackpressureLatest()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .filter(list -> list.size() == 1)
                    .publish();

            disposable.add(connectableFlowable.subscribe(list ->
                            createItemsView.setSelection(FIRST_SELECTION,
                                    list.get(0).uid(), list.get(0).name()),
                    OnErrorHandler.create()));

            //Fast track pre-select
            disposable.add(connectableFlowable
                            .filter(eventUid -> argument.type() == CreateItemsArgument.Type.EVENT)
                    .switchMap(list ->
                            repository.save(list.get(0).uid(), argument.uid()).toFlowable(BackpressureStrategy.LATEST))
                    .subscribe(eventUid -> {
                        createItemsView.navigateNext(eventUid);
                        createItemsView.finish();
                    }, OnErrorHandler.create()));

            disposable.add(connectableFlowable.connect());

            //clear second selection if changes to first & not EVENT
            disposable.add(createItemsView.selectionChanges(FIRST_SELECTION)
                    .toFlowable(BackpressureStrategy.LATEST)
                    .filter(eventUid -> argument.type() != CreateItemsArgument.Type.EVENT)
                    .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .observeOn(schedulerProvider.ui())
                    .subscribeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.setSelection(SECOND_SELECTION, "", ""),
                            OnErrorHandler.create()));

            //clear second selector if clear event on it:
            disposable.add(createItemsView.selection2ClearEvent()
                    .toFlowable(BackpressureStrategy.LATEST)
                    .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.setSelection(SECOND_SELECTION, "", ""),
                            OnErrorHandler.create()));
            //show dialog if first selector clicked:
            disposable.add(createItemsView.selection1ClickEvents()
                    .toFlowable(BackpressureStrategy.LATEST)
                    .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.showDialog1(argument.uid()), OnErrorHandler.create()));
            //show dialog for second selector if clicked:
            disposable.add(createItemsView.selection2ClickEvents()
                    .toFlowable(BackpressureStrategy.LATEST)
                    .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .filter(event -> !createItemsView.getSelectionState(FIRST_SELECTION).uid().isEmpty())
                    .subscribe(event -> {
                        if (argument.type() == CreateItemsArgument.Type.EVENT) {
                            createItemsView.showDialog2(createItemsView.getSelectionState(FIRST_SELECTION).uid());
                        } else if (argument.type() == CreateItemsArgument.Type.ENROLLMENT_EVENT) {
                            createItemsView.showDialog2(argument.uid());
                        } else if (argument.type() == CreateItemsArgument.Type.TEI) {
                            createItemsView.showDialog2(createItemsView.getSelectionState(FIRST_SELECTION).uid());
                        } else if (argument.type() == CreateItemsArgument.Type.ENROLLMENT) {
                            createItemsView.showDialog2(createItemsView.getSelectionState(FIRST_SELECTION).uid());
                        }
                    }, OnErrorHandler.create()));
            //create if create button clicked and navigate to next
            disposable.add(createItemsView.createButtonClick()
                    .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .filter(event -> !event.val0().isEmpty() && !event.val1().isEmpty())
                    .observeOn(schedulerProvider.io())
                    .switchMap(event -> repository.save(event.val0(), event.val1()))
                    .observeOn(schedulerProvider.ui())
                    .subscribe(uid -> {
                        createItemsView.navigateNext(uid);
                    }, OnErrorHandler.create()));
        }
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }
}
