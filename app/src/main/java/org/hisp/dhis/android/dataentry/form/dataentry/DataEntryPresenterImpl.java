package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.Result;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextDoubleViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextIntegerViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.text.TextViewModel;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionDisplayKeyValuePair;
import org.hisp.dhis.rules.models.RuleActionDisplayText;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleActionShowError;
import org.hisp.dhis.rules.models.RuleActionShowWarning;
import org.hisp.dhis.rules.models.RuleEffect;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import rx.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

@SuppressWarnings("PMD")
final class DataEntryPresenterImpl implements DataEntryPresenter {

    @NonNull
    private final CodeGenerator codeGenerator;

    @NonNull
    private final DataEntryStore dataEntryStore;

    @NonNull
    private final DataEntryRepository dataEntryRepository;

    @NonNull
    private final RuleEngineRepository ruleEngineRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final CompositeDisposable disposable;

    DataEntryPresenterImpl(@NonNull CodeGenerator codeGenerator,
            @NonNull DataEntryStore dataEntryStore,
            @NonNull DataEntryRepository dataEntryRepository,
            @NonNull RuleEngineRepository ruleEngineRepository,
            @NonNull SchedulerProvider schedulerProvider) {
        this.codeGenerator = codeGenerator;
        this.dataEntryStore = dataEntryStore;
        this.dataEntryRepository = dataEntryRepository;
        this.ruleEngineRepository = ruleEngineRepository;
        this.schedulerProvider = schedulerProvider;
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull DataEntryView dataEntryView) {
        Flowable<List<FieldViewModel>> fieldsFlowable = dataEntryRepository.list();
        Flowable<Result<RuleEffect>> ruleEffectFlowable = ruleEngineRepository.calculate()
                .subscribeOn(schedulerProvider.computation());

        // Combining results of two repositories into a single stream.
        Flowable<List<FieldViewModel>> viewModelsFlowable = Flowable.zip(
                fieldsFlowable, ruleEffectFlowable, this::applyEffects);

        disposable.add(viewModelsFlowable
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(dataEntryView.showFields(), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));

        disposable.add(dataEntryView.rowActions()
                .subscribeOn(schedulerProvider.ui())
                .observeOn(schedulerProvider.io())
                .switchMap(action -> {
                    Timber.d("dataEntryRepository.save(uid=[%s], value=[%s])",
                            action.id(), action.value());
                    return dataEntryStore.save(action.id(), action.value());
                })
                .subscribe(result -> Timber.d(result.toString()), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                }));
    }

    @Override
    public void onDetach() {
        disposable.clear();
    }

    @NonNull
    private List<FieldViewModel> applyEffects(
            @NonNull List<FieldViewModel> viewModels,
            @NonNull Result<RuleEffect> calcResult) {
        if (calcResult.error() != null) {
            calcResult.error().printStackTrace();
            return viewModels;
        }

        Map<String, FieldViewModel> fieldViewModels = toMap(viewModels);
        for (RuleEffect ruleEffect : calcResult.items()) {
            RuleAction ruleAction = ruleEffect.ruleAction();
            if (ruleAction instanceof RuleActionShowWarning) {
                RuleActionShowWarning showWarning = (RuleActionShowWarning) ruleAction;
                FieldViewModel model = fieldViewModels.get(showWarning.field());

                if (model != null && model instanceof EditTextViewModel) {
                    fieldViewModels.put(showWarning.field(),
                            ((EditTextViewModel) model).withWarning(showWarning.content()));
                } else if (model != null && model instanceof EditTextDoubleViewModel) {
                    fieldViewModels.put(showWarning.field(),
                            ((EditTextDoubleViewModel) model).withWarning(showWarning.content()));
                } else if (model != null && model instanceof EditTextIntegerViewModel) {
                    fieldViewModels.put(showWarning.field(), ((EditTextIntegerViewModel) model)
                            .withWarning(showWarning.content()));
                }
            } else if (ruleAction instanceof RuleActionShowError) {
                RuleActionShowError showError = (RuleActionShowError) ruleAction;
                FieldViewModel model = fieldViewModels.get(showError.field());

                if (model != null && model instanceof EditTextViewModel) {
                    fieldViewModels.put(showError.field(),
                            ((EditTextViewModel) model).withWarning(showError.content()));
                } else if (model != null && model instanceof EditTextDoubleViewModel) {
                    fieldViewModels.put(showError.field(),
                            ((EditTextDoubleViewModel) model).withWarning(showError.content()));
                } else if (model != null && model instanceof EditTextIntegerViewModel) {
                    fieldViewModels.put(showError.field(), ((EditTextIntegerViewModel) model)
                            .withWarning(showError.content()));
                }
            } else if (ruleAction instanceof RuleActionHideField) {
                RuleActionHideField hideField = (RuleActionHideField) ruleAction;
                fieldViewModels.remove(hideField.field());
            } else if (ruleAction instanceof RuleActionDisplayText) {
                String uid = codeGenerator.generate();

                RuleActionDisplayText displayText = (RuleActionDisplayText) ruleAction;
                TextViewModel textViewModel = TextViewModel.create(uid,
                        displayText.content(), displayText.data());

                fieldViewModels.put(uid, textViewModel);
            } else if (ruleAction instanceof RuleActionDisplayKeyValuePair) {
                String uid = codeGenerator.generate();

                RuleActionDisplayKeyValuePair displayText =
                        (RuleActionDisplayKeyValuePair) ruleAction;
                TextViewModel textViewModel = TextViewModel.create(uid,
                        displayText.content(), displayText.data());

                fieldViewModels.put(uid, textViewModel);
            }
        }

        return new ArrayList<>(fieldViewModels.values());
    }

    @NonNull
    private static Map<String, FieldViewModel> toMap(@NonNull List<FieldViewModel> fieldViewModels) {
        Map<String, FieldViewModel> map = new LinkedHashMap<>();
        for (FieldViewModel fieldViewModel : fieldViewModels) {
            map.put(fieldViewModel.uid(), fieldViewModel);
        }
        return map;
    }
}
