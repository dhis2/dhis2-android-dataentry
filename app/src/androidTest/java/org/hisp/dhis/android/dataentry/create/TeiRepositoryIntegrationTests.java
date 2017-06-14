package org.hisp.dhis.android.dataentry.create;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import io.reactivex.observers.TestObserver;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.dataentry.commons.utils.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TeiRepositoryIntegrationTests {
    private static final String CURRENT_DATE = "2016-04-06T00:05:57.495";
    private static final String[] PROJECTION = {
            TrackedEntityInstanceModel.Columns.UID,
            TrackedEntityInstanceModel.Columns.CREATED,
            TrackedEntityInstanceModel.Columns.LAST_UPDATED,
            TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT,
            TrackedEntityInstanceModel.Columns.TRACKED_ENTITY,
            TrackedEntityInstanceModel.Columns.STATE,
    };

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private CreateItemsRepository createItemsRepository;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();

        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, "organization_unit_uid");
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityModel.Columns.UID, "tracked_entity_uid");
        db.insert(TrackedEntityModel.TABLE, null, trackedEntity);

        Date currentDate = BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE);

        CodeGenerator codeGenerator = () -> "test_code";
        CurrentDateProvider currentDateProvider = () -> currentDate;
        createItemsRepository = new TeiRepositoryImpl(databaseRule.briteDatabase(),
                codeGenerator, currentDateProvider);
    }

    @Test
    public void saveMustPersistTeiWithCorrectProperties() {
        TestObserver<String> testObserver = createItemsRepository.save(
                "organization_unit_uid", "tracked_entity_uid").test();

        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.assertValueCount(1);

        assertThat(testObserver.values().get(0)).isEqualTo("test_code");

        assertThatCursor(databaseRule.database()
                .query(TrackedEntityInstanceModel.TABLE, PROJECTION, null, null, null, null, null))
                .hasRow("test_code", CURRENT_DATE, CURRENT_DATE, "organization_unit_uid",
                        "tracked_entity_uid", State.TO_POST.name())
                .isExhausted();
    }
}
