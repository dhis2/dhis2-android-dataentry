package org.hisp.dhis.android.dataentry.form.dataentry;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactory;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactoryImpl;

import dagger.Module;
import dagger.Provides;

@Module
@PerFragment
public class DataEntryModule {

    @NonNull
    private final FieldViewModelFactory modelFactory;

    public DataEntryModule(@NonNull Context context) {
        this.modelFactory = new FieldViewModelFactoryImpl(
                context.getString(R.string.enter_text),
                context.getString(R.string.enter_long_text),
                context.getString(R.string.enter_number),
                context.getString(R.string.enter_integer),
                context.getString(R.string.enter_positive_integer),
                context.getString(R.string.enter_negative_integer),
                context.getString(R.string.enter_positive_integer_or_zero));
    }

    @Provides
    @PerFragment
    DataEntryPresenter dataEntryPresenter(@NonNull SchedulerProvider schedulerProvider,
            @NonNull DataEntryRepository dataEntryRepository) {
        return new DataEntryPresenterImpl(dataEntryRepository, schedulerProvider);
    }

    @Provides
    @PerFragment
    DataEntryRepository dataEntryRepository(@NonNull BriteDatabase briteDatabase) {
        return new ProgramStageRepositoryImpl(briteDatabase, modelFactory);
    }
}
