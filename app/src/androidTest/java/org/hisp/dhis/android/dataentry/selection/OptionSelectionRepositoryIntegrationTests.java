package org.hisp.dhis.android.dataentry.selection;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.option.OptionModel;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class OptionSelectionRepositoryIntegrationTests {

    public static final String OPTIONSET_CODE = "option_set_code";
    public static final String OPTIONSET_DISPLAY_NAME = "option_set_dislayName";
    public static final String OPTIONSET_TEXT = "OPTIONSET_TEXT";
    public static final int OPTIONSET_VERSION = 1;
    public static final String OPTION_DISPLAY_NAME = "option_display_name";
    public static final String OPTION_CODE = "option_code";
    public static final String OPTIONSET_UID = "optionset_uid";
    public static final String OPTIONSET_NAME = "option_set_name";
    public static final String OPTION_UID = "option_uid";
    public static final String OPTION_NAME = "option_name";
    public static final String OPTION_2_UID = "option2_uid";
    public static final String OPTION_2_DISPLAY_NAME = "opton_2_name";
    public static final String OPTION_3_UID = "option_3_uid";
    public static final String OPTION_3_DISPLAY_NAME = "option_3_display_name";

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private static Date date;
    private static String dateString;

    private SelectionRepository repository;
    TestSubscriber<List<SelectionViewModel>> subscriber;

    public OptionSelectionRepositoryIntegrationTests() {
        date = new Date();
        dateString = date.toString();
    }

    @Before
    public void setup() {
        SQLiteDatabase database = databaseRule.database();
        repository = new OptionSetRepositoryImpl(databaseRule.briteDatabase());

        database.insert(OptionSetModel.TABLE, null, optionSet(OPTIONSET_UID, OPTIONSET_DISPLAY_NAME));
        database.insert(OptionModel.TABLE, null, option(OPTION_UID, OPTION_DISPLAY_NAME, OPTIONSET_UID));
        database.insert(OptionModel.TABLE, null, option(OPTION_2_UID, OPTION_2_DISPLAY_NAME, OPTIONSET_UID));

        subscriber = repository.list(OPTIONSET_UID).test();
    }

    @Test
    public void retrieveData() {
        // happy path test: verify that one OptionSet with two options is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).uid()).isEqualTo(OPTION_UID);
        assertThat(result.get(0).label()).isEqualTo(OPTION_DISPLAY_NAME);
        assertThat(result.get(1).uid()).isEqualTo(OPTION_2_UID);
        assertThat(result.get(1).label()).isEqualTo(OPTION_2_DISPLAY_NAME);
    }

    @Test
    public void optionSetEmpty() {
        // try to retrieve option set that has no options.
        databaseRule.database().insert(OptionSetModel.TABLE, null, optionSet("empty", OPTIONSET_DISPLAY_NAME));

        TestSubscriber<List<SelectionViewModel>> subscriber = repository.list("empty").test();

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void optionSetNull() {
        // try to retrieve optionSet that is not in db
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.list(null).test();

        subscriber.assertValueCount(0);
        subscriber.assertError(IllegalArgumentException.class);
        subscriber.assertNotComplete();
    }

    @Test
    public void optionSetWrong() {
        // try to retrieve optionSet that is not in db
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.list("wrong").test();

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void optionModification() {
        // change name of option & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().update(OptionModel.TABLE, option(OPTION_2_UID, "updated_option2", OPTIONSET_UID),
                OptionModel.Columns.UID + "=?", OPTION_2_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).uid()).isEqualTo(OPTION_UID);
        assertThat(result.get(0).label()).isEqualTo(OPTION_DISPLAY_NAME);
        assertThat(result.get(1).uid()).isEqualTo(OPTION_2_UID);
        assertThat(result.get(1).label()).isEqualTo("updated_option2");
    }

    @Test
    public void optionAddition() {
        // add an option & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().insert(OptionModel.TABLE, option(OPTION_3_UID, OPTION_3_DISPLAY_NAME,
                OPTIONSET_UID));

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).uid()).isEqualTo(OPTION_UID);
        assertThat(result.get(0).label()).isEqualTo(OPTION_DISPLAY_NAME);
        assertThat(result.get(1).uid()).isEqualTo(OPTION_2_UID);
        assertThat(result.get(1).label()).isEqualTo(OPTION_2_DISPLAY_NAME);
        assertThat(result.get(2).uid()).isEqualTo(OPTION_3_UID);
        assertThat(result.get(2).label()).isEqualTo(OPTION_3_DISPLAY_NAME);
    }

    @Test
    public void optionDeletion() {
        // delete the option & verify that it is observed.

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().delete(OptionModel.TABLE, OptionModel.Columns.UID + "=?", OPTION_2_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).uid()).isEqualTo(OPTION_UID);
        assertThat(result.get(0).label()).isEqualTo(OPTION_DISPLAY_NAME);
    }

    @Test
    public void optionSetDeletion() {
        // delete an opitonSet and verify that fk constrainted Options are updated and the client is updated...
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().delete(OptionSetModel.TABLE, OptionSetModel.Columns.UID + "=?", OPTIONSET_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(0);
    }

    ///Helper methods:
    ///
    private static ContentValues optionSet(String uid, String displayName) {
        ContentValues values = new ContentValues();
        values.put(OptionSetModel.Columns.UID, uid);
        values.put(OptionSetModel.Columns.CODE, OPTIONSET_CODE);
        values.put(OptionSetModel.Columns.NAME, OPTIONSET_NAME);
        values.put(OptionSetModel.Columns.DISPLAY_NAME, displayName);
        values.put(OptionSetModel.Columns.CREATED, dateString);
        values.put(OptionSetModel.Columns.LAST_UPDATED, dateString);
        values.put(OptionSetModel.Columns.VERSION, OPTIONSET_VERSION);
        values.put(OptionSetModel.Columns.VALUE_TYPE, OPTIONSET_TEXT);
        return values;
    }

    private static ContentValues option(String uid, String displayName, String optionSetUid) {
        ContentValues values = new ContentValues();
        values.put(OptionModel.Columns.UID, uid);
        values.put(OptionModel.Columns.CODE, OPTION_CODE);
        values.put(OptionModel.Columns.NAME, OPTION_NAME);
        values.put(OptionModel.Columns.DISPLAY_NAME, displayName);
        values.put(OptionModel.Columns.CREATED, dateString);
        values.put(OptionModel.Columns.LAST_UPDATED, dateString);
        values.put(OptionModel.Columns.OPTION_SET, optionSetUid);
        return values;
    }

}
