package org.hisp.dhis.android.dataentry.form.dataentry;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactory;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactoryImpl;
import org.hisp.dhis.android.dataentry.user.UserRepository;

import dagger.Module;
import dagger.Provides;

import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

@Module
@PerFragment
public class DataEntryModule {

    @NonNull
    private final FieldViewModelFactory modelFactory;

    @NonNull
    private final DataEntryArguments arguments;

    public DataEntryModule(@NonNull Context context, @NonNull DataEntryArguments arguments) {
        this.arguments = arguments;
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
    DataEntryRepository dataEntryRepository(@NonNull BriteDatabase briteDatabase,
            @NonNull UserRepository userRepository, @NonNull CurrentDateProvider dateProvider) {
        if (!isEmpty(arguments.event())) { // NOPMD
            return new ProgramStageRepository(briteDatabase, userRepository,
                    modelFactory, dateProvider, arguments.event(), arguments.section());
        } else if (!isEmpty(arguments.enrollment())) { //NOPMD
            return new EnrollmentRepository(briteDatabase,
                    modelFactory, dateProvider, arguments.enrollment());
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }
    }
}
