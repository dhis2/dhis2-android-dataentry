package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.selection.OrganisationUnitRepositoryImpl;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

@SuppressWarnings({
        "PMD.CyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.ExcessiveMethodLength"
})
class CreateItemsPresenterImpl implements CreateItemsPresenter {
    private static final int FIRST_SELECTION = 0;
    private static final int SECOND_SELECTION = 1;
    private final CreateItemsArgument argument;
    private final CreateItemsRepository repository;
    private final OrganisationUnitRepositoryImpl orgRepository;
    private final SchedulerProvider schedulerProvider;
    private final CompositeDisposable disposable;
//    private final String[] selectedUids;

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
        //TODO: resume state?
        if (view instanceof CreateItemsView) {
            CreateItemsView createItemsView = (CreateItemsView) view;

            ///check if single org unit & set it if .
            disposable.add(createItemsView.selection1ClearEvent()
                    .debounce(150, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> {
                        createItemsView.setSelection(FIRST_SELECTION, "", "");
                        createItemsView.setSelection(SECOND_SELECTION, "", "");
                    }, err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(orgRepository.search("")
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(list -> {
                        if (list.size() == 1) {

                            createItemsView.setSelection(FIRST_SELECTION, list.get(0).uid(), list.get(0).name());

                            goToDataEntryIfProgramIsPreselected(createItemsView, list.get(0).uid());
                        }
                    }, err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.selectionChanges(FIRST_SELECTION)
                    .debounce(150, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.setSelection(SECOND_SELECTION, "", ""), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.selection2ClearEvent()
                    .debounce(150, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.setSelection(SECOND_SELECTION, "", ""), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.selection1ClickEvents()
                    .debounce(150, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(event -> createItemsView.showDialog1(argument.uid()), err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.selection2ClickEvents()
                    .debounce(150, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    //TODO: test for this in the tests:
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
                    }, err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
            disposable.add(createItemsView.createButtonClick()
                    .debounce(150, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                    .subscribeOn(schedulerProvider.ui())
                    //TODO: test for this in the tests: testy testy test
                    .filter(event -> !event.val0().isEmpty() && !event.val1().isEmpty())
                    .observeOn(schedulerProvider.io())
                    .switchMap(event -> {
                                Timber.d("Selection for " + argument.type() + "= " + event.toString());
                                //val0 is always OrganisationUnit.
                                if (argument.type() == CreateItemsArgument.Type.ENROLLMENT) {
                                    //val1:Program. Maybe tei uid can be passed through argument.uid() ?
                                    return repository.save(event.val0(), event.val1()); //Foreign key exception.
                                } else if (argument.type() == CreateItemsArgument.Type.TEI) {
                                    //val1:Program
                                    return repository.save(event.val0(), event.val1()); //Foreign key exception.
                                } else if (argument.type() == CreateItemsArgument.Type.EVENT) {
                                    // val1:ProgramStage //? requires Program? I have ProgramStage. Maybe I
                                    // can have Program from argument.uid() depending on CreateArguments
                                    // creator.
                                    return repository.save(event.val0(), event.val1()); // Hangs. save never emits.
                                } else if (argument.type() == CreateItemsArgument.Type.ENROLLMENT_EVENT) {
                                    //val1:ProgramStage
                                    return repository.save(event.val0(), event.val1()); //Seems to work ok.
                                } else {
                                    throw new IllegalArgumentException("Unknown type. ");
                                }
                            }
                    )
                    .observeOn(schedulerProvider.ui())
                    .subscribe(uid -> {
                        Timber.d("Created Uid = " + uid);
                        createItemsView.navigateNext(uid);
                    }, err -> {
                        throw new OnErrorNotImplementedException(err);
                    }));
        }
    }

    private void goToDataEntryIfProgramIsPreselected(CreateItemsView createItemsView, String orgUnit) {
        if (argument.type() == CreateItemsArgument.Type.EVENT) {
            String program = argument.uid();
            disposable.add(repository.save(orgUnit, program)
                    .subscribe(eventUid -> {
                        createItemsView.navigateNext(eventUid);
                        createItemsView.finish();
                    }));
        }
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }
}
