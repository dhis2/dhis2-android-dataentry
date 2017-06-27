package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;

import dagger.Module;
import dagger.Provides;

@Module
@PerFragment
public class FormModule {

    @NonNull
    private final FormViewArguments formViewArguments;

    public FormModule(@NonNull FormViewArguments formViewArguments) {
        this.formViewArguments = formViewArguments;
    }

    @Provides
    @PerFragment
    FormPresenter formPresenter(@NonNull SchedulerProvider schedulerProvider,
                                @NonNull FormRepository formRepository) {
        return new FormPresenterImpl(formViewArguments,
                schedulerProvider, formRepository);
    }

    @Provides
    @PerFragment
    FormRepository formRepository(@NonNull BriteDatabase briteDatabase, @NonNull CodeGenerator codeGenerator,
                                  @NonNull CurrentDateProvider currentDateProvider) {
        if (formViewArguments.type() == FormViewArguments.Type.ENROLLMENT) {
            return new EnrollmentFormRepository(briteDatabase, codeGenerator, currentDateProvider);
        } else {
            return new EventRepository(briteDatabase);
        }
    }
}