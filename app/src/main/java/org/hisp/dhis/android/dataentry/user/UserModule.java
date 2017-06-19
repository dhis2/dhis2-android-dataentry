package org.hisp.dhis.android.dataentry.user;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerUser;

import dagger.Module;
import dagger.Provides;

@Module
@PerUser
public class UserModule {

    @Provides
    @PerUser
    UserRepository userRepository(BriteDatabase briteDatabase) {
        return new UserRepositoryImpl(briteDatabase);
    }
}
