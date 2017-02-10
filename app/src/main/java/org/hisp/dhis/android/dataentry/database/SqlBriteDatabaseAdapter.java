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
package org.hisp.dhis.android.dataentry.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import timber.log.Timber;

import static org.hisp.dhis.android.dataentry.utils.Preconditions.isNull;

// TODO: tests
class SqlBriteDatabaseAdapter implements DatabaseAdapter {
    private final BriteDatabase sqlBriteDatabase;
    private BriteDatabase.Transaction transaction;

    SqlBriteDatabaseAdapter(@NonNull BriteDatabase briteDatabase) {
        isNull(briteDatabase, "Brite");
        sqlBriteDatabase = briteDatabase;
    }

    @Override
    public SQLiteStatement compileStatement(String sql) {
        return sqlBriteDatabase.getWritableDatabase().compileStatement(sql);
    }

    @Override
    public Cursor query(String sql, String... selectionArgs) {
        return sqlBriteDatabase.query(sql, selectionArgs);
    }

    @Override
    public long executeInsert(String table, SQLiteStatement sqLiteStatement) {
        return sqlBriteDatabase.executeInsert(table, sqLiteStatement);
    }

    @Override
    public int executeUpdateDelete(String table, SQLiteStatement sqLiteStatement) {
        return sqlBriteDatabase.executeUpdateDelete(table, sqLiteStatement);
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        return sqlBriteDatabase.delete(table, whereClause, whereArgs);
    }

    @Override
    public void delete(String s) {
        Timber.d(s);
    }

    @Override
    public void beginTransaction() {
        transaction = sqlBriteDatabase.newTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        transaction.markSuccessful();
    }

    @Override
    public void endTransaction() {
        transaction.end();
    }
}
