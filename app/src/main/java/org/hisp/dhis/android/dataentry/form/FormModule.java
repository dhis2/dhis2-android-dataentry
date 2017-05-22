package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;

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
    FormRepository formRepository(@NonNull BriteDatabase briteDatabase) {
        if (formViewArguments.type() == FormViewArguments.Type.EVENT) {
            return new EventRepository(briteDatabase);
        } else {
            return new EnrollmentFormRepository(briteDatabase);
        }
    }
}