package org.hisp.dhis.android.dataentry.selection;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel.Columns;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class OrganisationUnitSelectionRepositoryIntegrationTests {

    public static final String ORGUNIT_DISPLAY_NAME = "orgunit_display_name";
    public static final String ORGUNIT_CODE = "orgunit_code";
    public static final String ORGUNITSET_UID = "orgunitset_uid";
    public static final String ORGUNITSET_NAME = "orgunit_set_name";
    public static final String ORGUNIT_UID = "orgunit_uid";
    public static final String ORGUNIT_NAME = "orgunit_name";
    public static final String ORGUNIT_2_UID = "orgunit2_uid";
    public static final String ORGUNIT_2_DISPLAY_NAME = "opton_2_name";
    public static final String ORGUNIT_3_UID = "orgunit_3_uid";
    public static final String ORGUNIT_3_DISPLAY_NAME = "orgunit_3_display_name";

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private Date date;
    private String dateString;

    private OrganisationUnitRepositoryImpl repository;
    private TestSubscriber<List<SelectionViewModel>> subscriber;

    @Before
    public void setup() {
        date = new Date();
        dateString = date.toString();

        SQLiteDatabase database = databaseRule.database();
        repository = new OrganisationUnitRepositoryImpl(databaseRule.briteDatabase());

        database.insert(OrganisationUnitModel.TABLE, null, orgUnit(ORGUNIT_UID, ORGUNIT_DISPLAY_NAME));
        database.insert(OrganisationUnitModel.TABLE, null, orgUnit(ORGUNIT_2_UID, ORGUNIT_2_DISPLAY_NAME));

        subscriber = repository.list().test();
    }

    @Test
    public void retrieve() {
        // happy path test: verify that one OptionSet with two options is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).uid()).isEqualTo(ORGUNIT_UID);
        assertThat(result.get(0).label()).isEqualTo(ORGUNIT_DISPLAY_NAME);
        assertThat(result.get(1).uid()).isEqualTo(ORGUNIT_2_UID);
        assertThat(result.get(1).label()).isEqualTo(ORGUNIT_2_DISPLAY_NAME);
    }

    @Test
    public void addition() {
        // add an option & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().insert(OrganisationUnitModel.TABLE, orgUnit(ORGUNIT_3_UID,
                ORGUNIT_3_DISPLAY_NAME));

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).uid()).isEqualTo(ORGUNIT_UID);
        assertThat(result.get(0).label()).isEqualTo(ORGUNIT_DISPLAY_NAME);
        assertThat(result.get(1).uid()).isEqualTo(ORGUNIT_2_UID);
        assertThat(result.get(1).label()).isEqualTo(ORGUNIT_2_DISPLAY_NAME);
        assertThat(result.get(2).uid()).isEqualTo(ORGUNIT_3_UID);
        assertThat(result.get(2).label()).isEqualTo(ORGUNIT_3_DISPLAY_NAME);
    }

    @Test
    public void deletion() {
        // delete an opitonSet and verify that fk constrainted Options are updated and the client is updated...
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().delete(OrganisationUnitModel.TABLE, OrganisationUnitModel.Columns.UID + "=?",
                ORGUNIT_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(1);
    }

    private ContentValues orgUnit(String orgunitUid, String orgunitName) {
        ContentValues result = new ContentValues();
        result.put(Columns.UID, orgunitUid);
        result.put(Columns.CODE, "orgUnitCode");
        result.put(Columns.NAME, "orgUnitName");
        result.put(Columns.DISPLAY_NAME, orgunitName);
        result.put(Columns.CREATED, dateString);
        result.put(Columns.LAST_UPDATED, dateString);
        result.put(Columns.SHORT_NAME, "orgUnitName");
        result.put(Columns.DISPLAY_SHORT_NAME, "orgUnitShortName");
        result.put(Columns.DESCRIPTION, "description");
        result.put(Columns.DISPLAY_DESCRIPTION, "displayDescription");
        result.put(Columns.PATH, "path");
        result.put(Columns.OPENING_DATE, dateString);
        result.put(Columns.CLOSED_DATE, dateString);
        result.put(Columns.PARENT, "parent");
        result.put(Columns.LEVEL, 1);
        return result;
    }
}
