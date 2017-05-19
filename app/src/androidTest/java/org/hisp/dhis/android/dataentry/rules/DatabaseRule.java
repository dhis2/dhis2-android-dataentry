package org.hisp.dhis.android.dataentry.rules;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.rules.ExternalResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rx.Scheduler;

public final class DatabaseRule extends ExternalResource {
    private final Scheduler scheduler;

    @Nullable
    private BriteDatabase briteDatabase;

    public DatabaseRule(@NonNull Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public DatabaseRule(@NonNull BriteDatabase briteDatabase) {
        this.scheduler = null;
        this.briteDatabase = briteDatabase;
    }

    @Override
    protected void before() throws Throwable {
        // if database was supplied, don't re-create it
        if (scheduler != null) {
            // create a new in-memory database before running test
            DbOpenHelper dbOpenHelper = new DbOpenHelper(
                    InstrumentationRegistry.getTargetContext(), null);

            SqlBrite sqlBrite = new SqlBrite.Builder().build();
            briteDatabase = sqlBrite.wrapDatabaseHelper(dbOpenHelper, scheduler);
        }
    }

    @Override
    protected void after() {
        // close database only in case when it was created within Rule
        if (scheduler != null && briteDatabase != null) {
            briteDatabase.close();
        }
    }

    @NonNull
    public SQLiteDatabase database() {
        if (briteDatabase == null) {
            throw new IllegalStateException("Database has not been created yet");
        }

        return briteDatabase.getWritableDatabase();
    }

    @NonNull
    public BriteDatabase briteDatabase() {
        if (briteDatabase == null) {
            throw new IllegalStateException("Database has not been created yet");
        }

        return briteDatabase;
    }

    public void insertMetaData() throws IOException {
        if (briteDatabase == null) {
            throw new IllegalStateException("Database has not been created yet");
        }

        SQLiteDatabase db = briteDatabase.getWritableDatabase();
        db.beginTransaction();

        String[] files = InstrumentationRegistry.getContext().getAssets().list("db");
        for (String file : files) {

            if (file.endsWith(".sql")) {

                InputStream inputStream = InstrumentationRegistry.getContext().getAssets().open("db/" + file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String sqlInsert = reader.readLine();
                while (sqlInsert != null) {
                    db.execSQL(sqlInsert);
                    sqlInsert = reader.readLine();
                }
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();

    }
}