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
