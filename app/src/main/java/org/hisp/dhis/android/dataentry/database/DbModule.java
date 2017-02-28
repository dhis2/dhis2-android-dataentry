package org.hisp.dhis.android.dataentry.database;

import android.content.Context;
import android.support.annotation.Nullable;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {
    private final String databaseName;

    public DbModule(@Nullable String databaseName) {
        this.databaseName = databaseName;
    }

    @Provides
    @Singleton
    SqlBrite.Logger sqlBriteLogger() {
        return new SqlBriteLogger();
    }

    @Provides
    @Singleton
    SqlBrite sqlBrite(SqlBrite.Logger logger) {
        return new SqlBrite.Builder()
                .logger(logger)
                .build();
    }

    @Provides
    @Singleton
    DbOpenHelper databaseOpenHelper(Context context) {
        return new DbOpenHelper(context, databaseName);
    }

    @Provides
    @Singleton
    BriteDatabase briteDatabase(DbOpenHelper dbOpenHelper,
            SqlBrite sqlBrite, SchedulerProvider schedulerProvider) {
        return sqlBrite.wrapDatabaseHelper(dbOpenHelper, schedulerProvider.legacyIo());
    }

    @Provides
    @Singleton
    DatabaseAdapter databaseAdapter(BriteDatabase briteDatabase) {
        return new SqlBriteDatabaseAdapter(briteDatabase);
    }
}
