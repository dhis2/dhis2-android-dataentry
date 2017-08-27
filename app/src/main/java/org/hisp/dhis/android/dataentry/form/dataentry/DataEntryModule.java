package org.hisp.dhis.android.dataentry.form.dataentry;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.form.FormRepository;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactory;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactoryImpl;

import dagger.Module;
import dagger.Provides;

import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

@PerFragment
@Module(includes = DataEntryStoreModule.class)
public class DataEntryModule {

    @NonNull
    private final FieldViewModelFactory modelFactory;

    @NonNull
    private final DataEntryArguments arguments;

    DataEntryModule(@NonNull Context context, @NonNull DataEntryArguments arguments) {
        this.arguments = arguments;
        this.modelFactory = new FieldViewModelFactoryImpl(
                context.getString(R.string.enter_text),
                context.getString(R.string.enter_long_text),
                context.getString(R.string.enter_number),
                context.getString(R.string.enter_integer),
                context.getString(R.string.enter_positive_integer),
                context.getString(R.string.enter_negative_integer),
                context.getString(R.string.enter_positive_integer_or_zero),
                context.getString(R.string.filter_options),
                context.getString(R.string.choose_date));
    }

    @Provides
    @PerFragment
    DataEntryPresenter dataEntryPresenter(@NonNull SchedulerProvider schedulerProvider,
            @NonNull DataEntryStore dataEntryStore,
            @NonNull DataEntryRepository dataEntryRepository,
            @NonNull FormRepository formRepository) {
        return new DataEntryPresenterImpl(dataEntryStore,
                dataEntryRepository, formRepository, schedulerProvider);
    }

    @Provides
    @PerFragment
    DataEntryRepository dataEntryRepository(@NonNull BriteDatabase briteDatabase) {
        if (!isEmpty(arguments.event())) { // NOPMD
            return new ProgramStageRepository(briteDatabase, modelFactory,
                    arguments.event(), arguments.section());
        } else if (!isEmpty(arguments.enrollment())) { //NOPMD
            return new EnrollmentRepository(briteDatabase, modelFactory, arguments.enrollment());
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }
    }
}
