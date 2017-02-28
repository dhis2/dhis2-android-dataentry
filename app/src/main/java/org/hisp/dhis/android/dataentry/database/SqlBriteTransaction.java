package org.hisp.dhis.android.dataentry.database;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.data.database.Transaction;

class SqlBriteTransaction implements Transaction {
    private final BriteDatabase.Transaction transaction;

    SqlBriteTransaction(BriteDatabase.Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void begin() {
        // no-op
        // transaction was already started on
    }

    @Override
    public void setSuccessful() {
        transaction.markSuccessful();
    }

    @Override
    public void end() {
        transaction.end();
    }
}