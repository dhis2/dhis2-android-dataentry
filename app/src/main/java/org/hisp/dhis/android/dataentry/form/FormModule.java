package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.rules.RuleExpressionEvaluator;

import dagger.Module;
import dagger.Provides;

@Module
@PerForm
public class FormModule {

    @NonNull
    private final FormViewArguments formViewArguments;

    FormModule(@NonNull FormViewArguments formViewArguments) {
        this.formViewArguments = formViewArguments;
    }

    @Provides
    @PerForm
    FormPresenter formPresenter(@NonNull SchedulerProvider schedulerProvider,
            @NonNull FormRepository formRepository) {
        return new FormPresenterImpl(formViewArguments, schedulerProvider, formRepository);
    }

    @Provides
    @PerForm
    RulesRepository rulesRepository(@NonNull BriteDatabase briteDatabase) {
        return new RulesRepository(briteDatabase);
    }

    @Provides
    @PerForm
    FormRepository formRepository(@NonNull BriteDatabase briteDatabase,
            @NonNull RuleExpressionEvaluator evaluator,
            @NonNull RulesRepository rulesRepository,
            @NonNull CodeGenerator codeGenerator,
            @NonNull CurrentDateProvider currentDateProvider) {
        if (formViewArguments.type().equals(FormViewArguments.Type.ENROLLMENT)) {
            return new EnrollmentFormRepository(briteDatabase, evaluator, rulesRepository,
                    codeGenerator, currentDateProvider, formViewArguments.uid());
        } else if (formViewArguments.type().equals(FormViewArguments.Type.EVENT)) {
            return new EventRepository(briteDatabase, evaluator,
                    rulesRepository, formViewArguments.uid());
        } else {
            throw new IllegalArgumentException("FormViewArguments of " +
                    "unexpected type: " + formViewArguments.type());
        }
    }
}