package org.hisp.dhis.android.dataentry.selection;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel.Columns;
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
public class OrganisationUnitSelectionRepositoryIntegrationTests {
    private static final String ORGUNIT_UID = "orgunit_uid";
    private static final String ORGUNIT_2_UID = "orgunit2_uid";
    private static final String ORGUNIT_3_UID = "orgunit_3_uid";
    private static final String ORGUNIT_DISPLAY_NAME = "orgunit_display_name";
    private static final String ORGUNIT_2_DISPLAY_NAME = "orgunit_2_name";
    private static final String ORGUNIT_3_DISPLAY_NAME = "orgunit_3_display_name";

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    // under tests
    private OrganisationUnitRepositoryImpl repository;

    @Before
    public void setup() {
        SQLiteDatabase database = databaseRule.database();
        repository = new OrganisationUnitRepositoryImpl(databaseRule.briteDatabase());

        database.insert(OrganisationUnitModel.TABLE, null, orgUnit(ORGUNIT_UID, ORGUNIT_DISPLAY_NAME));
        database.insert(OrganisationUnitModel.TABLE, null, orgUnit(ORGUNIT_2_UID, ORGUNIT_2_DISPLAY_NAME));
    }

    @Test
    public void searchMustReturnAllMatchingOrgUnits() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("orgunit").test();

        // happy path test: verify that one OptionSet with two options is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(ORGUNIT_UID, ORGUNIT_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(ORGUNIT_2_UID, ORGUNIT_2_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustReturnAllOrgUnitsOnEmptyQuery() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("").test();

        // happy path test: verify that one OptionSet with two options is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(ORGUNIT_UID, ORGUNIT_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(ORGUNIT_2_UID, ORGUNIT_2_DISPLAY_NAME))).isTrue();
    }


    @Test
    public void searchMustNotReturnNonMatchingOrgUnits() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("random_org_unit").test();

        // happy path test: verify that one OptionSet with two options is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void searchMustObserveUpdatesInOrgUnitTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("orgunit").test();

        // change name of orgunit & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().update(OrganisationUnitModel.TABLE, orgUnit(ORGUNIT_2_UID,
                "updated_orgUnit2"), OrganisationUnitModel.Columns.UID + "=?", ORGUNIT_2_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(ORGUNIT_UID, ORGUNIT_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(ORGUNIT_2_UID, "updated_orgUnit2"))).isTrue();
    }

    @Test
    public void searchMustObserveInsertsInOrgUnitTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("orgunit").test();

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
        assertThat(result.contains(SelectionViewModel.create(ORGUNIT_UID, ORGUNIT_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(ORGUNIT_2_UID, ORGUNIT_2_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(ORGUNIT_3_UID, ORGUNIT_3_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustObserveDeletesInOrgUnitsTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("orgunit").test();

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().delete(OrganisationUnitModel.TABLE,
                OrganisationUnitModel.Columns.UID + "=?", ORGUNIT_UID);

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
        result.put(Columns.CREATED, new Date().toString());
        result.put(Columns.LAST_UPDATED, new Date().toString());
        result.put(Columns.SHORT_NAME, "orgUnitName");
        result.put(Columns.DISPLAY_SHORT_NAME, "orgUnitShortName");
        result.put(Columns.DESCRIPTION, "description");
        result.put(Columns.DISPLAY_DESCRIPTION, "displayDescription");
        result.put(Columns.PATH, "path");
        result.put(Columns.OPENING_DATE, new Date().toString());
        result.put(Columns.CLOSED_DATE, new Date().toString());
        result.put(Columns.PARENT, "parent");
        result.put(Columns.LEVEL, 1);
        return result;
    }
}
