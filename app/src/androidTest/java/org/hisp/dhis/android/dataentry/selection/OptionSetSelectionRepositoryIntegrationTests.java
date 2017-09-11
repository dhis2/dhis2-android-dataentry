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
public class OptionSetSelectionRepositoryIntegrationTests {
    private static final int OPTIONSET_VERSION = 1;
    private static final String OPTIONSET_UID = "optionset_uid";
    private static final String OPTIONSET_CODE = "option_set_code";
    private static final String OPTIONSET_TEXT = "option_set_text";
    private static final String OPTIONSET_NAME = "option_set_name";
    private static final String OPTIONSET_DISPLAY_NAME = "option_set_display_name";
    private static final String OPTION_UID = "option_uid";
    private static final String OPTION_CODE_1 = "option_code_1";
    private static final String OPTION_CODE_2 = "option_code_2";
    private static final String OPTION_NAME = "option_name";
    private static final String OPTION_2_UID = "option2_uid";
    private static final String OPTION_3_UID = "option_3_uid";
    private static final String OPTION_DISPLAY_NAME = "option_display_name";
    private static final String OPTION_2_DISPLAY_NAME = "option_2_name";
    private static final String OPTION_3_DISPLAY_NAME = "option_3_display_name";
    private static final String OPTION_CODE_3 = "option_code_3";

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    // under tests
    private SelectionRepository repository;

    @Before
    public void setup() {
        SQLiteDatabase database = databaseRule.database();
        repository = new OptionSetRepositoryImpl(databaseRule.briteDatabase(), OPTIONSET_UID);

        database.insert(OptionSetModel.TABLE, null, optionSet(OPTIONSET_UID, OPTIONSET_DISPLAY_NAME));
        database.insert(OptionModel.TABLE, null, option(OPTION_UID,
                OPTION_DISPLAY_NAME, OPTIONSET_UID, OPTION_CODE_1));
        database.insert(OptionModel.TABLE, null, option(OPTION_2_UID,
                OPTION_2_DISPLAY_NAME, OPTIONSET_UID, OPTION_CODE_2));
    }

    @Test
    public void searchMustReturnAllMatchingOptions() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("option").test();

        // happy path test: verify that one OptionSet with two options is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(OPTION_UID,
                OPTION_DISPLAY_NAME, OPTION_CODE_1))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(OPTION_2_UID,
                OPTION_2_DISPLAY_NAME, OPTION_CODE_2))).isTrue();
    }

    @Test
    public void searchMustReturnAllOptionsOnEmptyQuery() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("").test();

        // happy path test: verify that one OptionSet with two options is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(OPTION_UID,
                OPTION_DISPLAY_NAME, OPTION_CODE_1))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(OPTION_2_UID,
                OPTION_2_DISPLAY_NAME, OPTION_CODE_2))).isTrue();
    }

    @Test
    public void searchMustNotReturnNonMatchingOptions() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("random_option").test();

        // happy path test: verify that one OptionSet with two options is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void searchMustObserveUpdatesInOptionTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("option").test();

        // change name of option & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().update(OptionModel.TABLE, option(OPTION_2_UID,
                "updated_option2", OPTIONSET_UID, OPTION_CODE_2),
                OptionModel.Columns.UID + "=?", OPTION_2_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(OPTION_UID,
                OPTION_DISPLAY_NAME, OPTION_CODE_1))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(OPTION_2_UID,
                "updated_option2", OPTION_CODE_2))).isTrue();
    }

    @Test
    public void searchMustObserveInsertsInOptionTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("option").test();

        // add an option & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().insert(OptionModel.TABLE, option(OPTION_3_UID,
                OPTION_3_DISPLAY_NAME, OPTIONSET_UID, OPTION_CODE_3));

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.contains(SelectionViewModel.create(OPTION_UID,
                OPTION_DISPLAY_NAME, OPTION_CODE_1))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(OPTION_2_UID,
                OPTION_2_DISPLAY_NAME, OPTION_CODE_2))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(OPTION_3_UID,
                OPTION_3_DISPLAY_NAME, OPTION_CODE_3))).isTrue();
    }

    @Test
    public void searchMustObserveDeletesInOptionTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("option").test();
        // delete the option & verify that it is observed.

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().delete(OptionModel.TABLE, OptionModel.Columns.UID + " = ?", OPTION_2_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).uid()).isEqualTo(OPTION_UID);
        assertThat(result.get(0).name()).isEqualTo(OPTION_DISPLAY_NAME);
        assertThat(result.get(0).code()).isEqualTo(OPTION_CODE_1);
    }

    @Test
    public void searchMustObserveParentTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("option").test();

        // delete an option set and verify that fk constrained
        // options are updated and the client is updated...
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

    @Test
    public void searchMustReturnEmptyListOnWrongParent() {
        repository = new OptionSetRepositoryImpl(databaseRule.briteDatabase(), "wrong");

        // try to retrieve optionSet that is not in db
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("option").test();

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(0);
    }

    private ContentValues optionSet(String uid, String displayName) {
        ContentValues values = new ContentValues();
        values.put(OptionSetModel.Columns.UID, uid);
        values.put(OptionSetModel.Columns.CODE, OPTIONSET_CODE);
        values.put(OptionSetModel.Columns.NAME, OPTIONSET_NAME);
        values.put(OptionSetModel.Columns.DISPLAY_NAME, displayName);
        values.put(OptionSetModel.Columns.CREATED, new Date().toString());
        values.put(OptionSetModel.Columns.LAST_UPDATED, new Date().toString());
        values.put(OptionSetModel.Columns.VERSION, OPTIONSET_VERSION);
        values.put(OptionSetModel.Columns.VALUE_TYPE, OPTIONSET_TEXT);
        return values;
    }

    private ContentValues option(String uid, String displayName,
            String optionSetUid, String optionCode) {
        ContentValues values = new ContentValues();
        values.put(OptionModel.Columns.UID, uid);
        values.put(OptionModel.Columns.CODE, optionCode);
        values.put(OptionModel.Columns.NAME, OPTION_NAME);
        values.put(OptionModel.Columns.DISPLAY_NAME, displayName);
        values.put(OptionModel.Columns.CREATED, new Date().toString());
        values.put(OptionModel.Columns.LAST_UPDATED, new Date().toString());
        values.put(OptionModel.Columns.OPTION_SET, optionSetUid);
        return values;
    }
}
