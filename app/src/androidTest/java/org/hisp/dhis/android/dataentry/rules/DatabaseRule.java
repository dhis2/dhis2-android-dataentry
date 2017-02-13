/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dataentry.rules;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.rules.ExternalResource;

import rx.Scheduler;

public final class DatabaseRule extends ExternalResource {
    private final Scheduler scheduler;

    @Nullable
    private BriteDatabase briteDatabase;

    public DatabaseRule(@NonNull Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    protected void before() throws Throwable {
        // create a new in-memory database before running test
        DbOpenHelper dbOpenHelper = new DbOpenHelper(
                InstrumentationRegistry.getTargetContext(), null);

        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        briteDatabase = sqlBrite.wrapDatabaseHelper(dbOpenHelper, scheduler);
    }

    @Override
    protected void after() {
        // closing database will purge all data
        if (briteDatabase != null) {
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
}