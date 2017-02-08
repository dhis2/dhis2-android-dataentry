package org.hisp.dhis.android.dataentry.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Scheduler;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BriteDatabase.class, SqlBrite.class})
public class SqlBriteDatabaseAdapterTests {

    BriteDatabase sqlBriteDatabase;

    SqlBrite sqlBrite;

    @Mock
    DbOpenHelper dbOpenHelper;

    @Mock
    SQLiteDatabase sqLiteDatabase;

    @Mock
    SQLiteStatement sqLiteStatement;

    @Mock
    BriteDatabase.Transaction transaction;

    @Mock
    SchedulerProvider schedulerProvider;

    @Mock
    Scheduler scheduler;

    SqlBriteDatabaseAdapter sqlBriteDatabaseAdapter; // the class we are testing

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sqlBriteDatabase = PowerMockito.mock(BriteDatabase.class);
        sqlBrite = PowerMockito.mock(SqlBrite.class);

        scheduler = Schedulers.immediate();

        Mockito.when(schedulerProvider.legacyIo()).thenReturn(scheduler);

        Mockito.when(sqlBrite.wrapDatabaseHelper(dbOpenHelper, scheduler)).thenReturn(sqlBriteDatabase);

        sqlBriteDatabaseAdapter = new SqlBriteDatabaseAdapter(dbOpenHelper, sqlBrite, schedulerProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void providingNullDbOpenHelper_shouldThrowError() throws Exception {
        new SqlBriteDatabaseAdapter(null, sqlBrite, schedulerProvider);
    }

    @Test
    public void statementIsCompiledOnWritableDatabase() throws Exception {
        Mockito.when(sqlBriteDatabase.getWritableDatabase()).thenReturn(sqLiteDatabase);

        String sql = "INSERT VALUE INTO TABLE";
        sqlBriteDatabaseAdapter.compileStatement(sql);

        verify(sqLiteDatabase).compileStatement(sql);
    }

    @Test
    public void queryIsRunOnReadableDatabase() throws Exception {
        String sql = "SELECT * FROM TABLE";
        sqlBriteDatabaseAdapter.query(sql, null);

        Cursor result = verify(sqlBriteDatabase).query(sql, null);
    }

    @Test
    public void deleteIsRunOnWritableDatabase() throws Exception {
        sqlBriteDatabaseAdapter.delete(null, null, null);
        verify(sqlBriteDatabase).delete(null, null, null);
    }

    @Test
    public void sqlStatementsAreExecuted() throws Exception {
        sqlBriteDatabaseAdapter.executeInsert("TABLE", sqLiteStatement);
        verify(sqlBriteDatabase).executeInsert("TABLE", sqLiteStatement);

        sqlBriteDatabaseAdapter.executeUpdateDelete("TABLE", sqLiteStatement);
        verify(sqlBriteDatabase).executeUpdateDelete("TABLE", sqLiteStatement);
    }

    @Test
    public void transactionIsCreated() throws Exception {
        Mockito.when(sqlBriteDatabase.newTransaction()).thenReturn(transaction);
        sqlBriteDatabaseAdapter.beginTransaction();
        BriteDatabase.Transaction result = verify(sqlBriteDatabase).newTransaction();
    }
}