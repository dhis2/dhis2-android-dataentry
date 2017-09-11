package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.user.UserRepository;

import dagger.Module;
import dagger.Provides;

import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

@Module
@PerFragment
public final class DataEntryStoreModule {

    @NonNull
    private final DataEntryArguments arguments;

    public DataEntryStoreModule(@NonNull DataEntryArguments arguments) {
        this.arguments = arguments;
    }

    @Provides
    @PerFragment
    DataEntryStore dataEntryRepository(@NonNull BriteDatabase briteDatabase,
            @NonNull UserRepository userRepository, @NonNull CurrentDateProvider dateProvider) {
        if (!isEmpty(arguments.event())) { // NOPMD
            return new DataValueStore(briteDatabase,
                    userRepository, dateProvider, arguments.event());
        } else if (!isEmpty(arguments.enrollment())) { //NOPMD
            return new AttributeValueStore(briteDatabase,
                    dateProvider, arguments.enrollment());
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }
    }
}
