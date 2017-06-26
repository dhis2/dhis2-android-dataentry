package org.hisp.dhis.android.dataentry.form.dataentry;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;
import org.hisp.dhis.android.core.user.UserCredentialsModel;
import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.hisp.dhis.android.dataentry.user.UserRepository;
import org.hisp.dhis.android.dataentry.user.UserRepositoryImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.dataentry.commons.utils.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class DataValueStoreIntegrationTests {
    private static final String[] TEDV_PROJECTION = {
            TrackedEntityDataValueModel.Columns.CREATED,
            TrackedEntityDataValueModel.Columns.LAST_UPDATED,
            TrackedEntityDataValueModel.Columns.DATA_ELEMENT,
            TrackedEntityDataValueModel.Columns.EVENT,
            TrackedEntityDataValueModel.Columns.VALUE,
            TrackedEntityDataValueModel.Columns.STORED_BY
    };

    private static final String[] EVENT_PROJECTION = {
            EventModel.Columns.UID,
            EventModel.Columns.CREATED,
            EventModel.Columns.LAST_UPDATED,
            EventModel.Columns.ORGANISATION_UNIT,
            EventModel.Columns.PROGRAM,
            EventModel.Columns.PROGRAM_STAGE,
            EventModel.Columns.STATE
    };

    private static final String DATA_ELEMENT_ONE_UID = "data_element_one_uid";
    private static final String DATA_ELEMENT_TWO_UID = "data_element_two_uid";
    private static final String DATA_ELEMENT_THREE_UID = "data_element_three_uid";
    private static final String EVENT_UID = "event_uid";

    private static final String DATA_ELEMENT_ONE_NAME = "data_element_one_name";
    private static final String DATA_ELEMENT_TWO_NAME = "data_element_two_name";
    private static final String DATA_ELEMENT_THREE_NAME = "data_element_three_name";
    private static final String TEST_USERNAME = "test_username";
    private static final String PROGRAM_UID = "program_uid";
    private static final String PS_UID = "ps_uid";
    private static final String ORGANISATION_UNIT_UID = "organisation_unit_uid";
    private static final String CURRENT_DATE = "2016-04-06T00:05:57.495";

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private DataEntryStore dataEntryStore;
    private Date currentDate;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();

        ContentValues user = new ContentValues();
        user.put(UserModel.Columns.UID, "test_user_uid");
        db.insert(UserModel.TABLE, null, user);

        ContentValues userCredentials = new ContentValues();
        userCredentials.put(UserCredentialsModel.Columns.UID, "test_user_credentials_uid");
        userCredentials.put(UserCredentialsModel.Columns.USER, "test_user_uid");
        userCredentials.put(UserCredentialsModel.Columns.USERNAME, TEST_USERNAME);
        db.insert(UserCredentialsModel.TABLE, null, userCredentials);

        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, ORGANISATION_UNIT_UID);
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, PROGRAM_UID);
        db.insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = new ContentValues();
        programStage.put(ProgramStageModel.Columns.UID, PS_UID);
        programStage.put(ProgramStageModel.Columns.PROGRAM, PROGRAM_UID);
        db.insert(ProgramStageModel.TABLE, null, programStage);

        db.insert(DataElementModel.TABLE, null, dataElement(DATA_ELEMENT_ONE_UID,
                DATA_ELEMENT_ONE_NAME, ValueType.BOOLEAN.name()));
        db.insert(DataElementModel.TABLE, null, dataElement(DATA_ELEMENT_TWO_UID,
                DATA_ELEMENT_TWO_NAME, ValueType.LONG_TEXT.name()));
        db.insert(DataElementModel.TABLE, null, dataElement(DATA_ELEMENT_THREE_UID,
                DATA_ELEMENT_THREE_NAME, ValueType.TEXT.name()));

        db.insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement(
                "ps_data_element_one", PS_UID, DATA_ELEMENT_ONE_UID, 3, true));
        db.insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement(
                "ps_data_element_two", PS_UID, DATA_ELEMENT_TWO_UID, 1, false));
        db.insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement(
                "ps_data_element_three", PS_UID, DATA_ELEMENT_THREE_UID, 2, true));

        // provider of time stamps for data values
        currentDate = BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE);
        CurrentDateProvider currentDateProvider = () -> currentDate;

        // user repository used to retrieve user name
        UserRepository userRepository = new UserRepositoryImpl(databaseRule.briteDatabase());

        // class under tests
        dataEntryStore = new DataValueStore(databaseRule.briteDatabase(),
                userRepository, currentDateProvider, EVENT_UID);
    }

    @Test
    public void saveShouldUpdateExistingDataValue() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(EventModel.TABLE, null, event(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_POST));

        Date createdDate = new Date();
        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID, createdDate,
                createdDate, DATA_ELEMENT_ONE_UID, "test_data_value", TEST_USERNAME));

        TestSubscriber<Long> testSubscriber = dataEntryStore.save(
                DATA_ELEMENT_ONE_UID, "test_updated_datavalue").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityDataValueModel.TABLE, TEDV_PROJECTION,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ?", new String[]{
                        DATA_ELEMENT_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(createdDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                DATA_ELEMENT_ONE_UID, EVENT_UID, "test_updated_datavalue", TEST_USERNAME
        ).isExhausted();
    }

    @Test
    public void saveShouldNullifyExistingDataValue() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(EventModel.TABLE, null, event(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_POST));

        Date createdDate = new Date();
        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID, createdDate,
                createdDate, DATA_ELEMENT_ONE_UID, "test_data_value", TEST_USERNAME));

        TestSubscriber<Long> testSubscriber = dataEntryStore.save(
                DATA_ELEMENT_ONE_UID, null).test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityDataValueModel.TABLE, TEDV_PROJECTION,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ?", new String[]{
                        DATA_ELEMENT_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(createdDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                DATA_ELEMENT_ONE_UID, EVENT_UID, null, TEST_USERNAME
        ).isExhausted();
    }

    @Test
    public void saveShouldInsertNewDataValue() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(EventModel.TABLE, null, event(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_POST));

        TestSubscriber<Long> testSubscriber = dataEntryStore.save(
                DATA_ELEMENT_ONE_UID, "test_datavalue").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityDataValueModel.TABLE, TEDV_PROJECTION,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ?", new String[]{
                        DATA_ELEMENT_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                DATA_ELEMENT_ONE_UID, EVENT_UID, "test_datavalue", TEST_USERNAME
        ).isExhausted();
    }

    @Test
    public void saveMustUpdateEventStateFromSyncedToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(EventModel.TABLE, null, event(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.SYNCED));

        TestSubscriber<Long> testSubscriber = dataEntryStore.save(
                DATA_ELEMENT_ONE_UID, "test_datavalue").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityDataValueModel.TABLE, TEDV_PROJECTION,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ?", new String[]{
                        DATA_ELEMENT_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                DATA_ELEMENT_ONE_UID, EVENT_UID, "test_datavalue", TEST_USERNAME
        ).isExhausted();

        assertThatCursor(db.query(EventModel.TABLE, EVENT_PROJECTION, EventModel.Columns.UID + " = ?",
                new String[]{EVENT_UID}, null, null, null)
        ).hasRow(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_UPDATE
        ).isExhausted();
    }

    @Test
    public void saveMustUpdateEventStateFromErrorToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(EventModel.TABLE, null, event(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.ERROR));

        TestSubscriber<Long> testSubscriber = dataEntryStore.save(
                DATA_ELEMENT_ONE_UID, "test_datavalue").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityDataValueModel.TABLE, TEDV_PROJECTION,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ?", new String[]{
                        DATA_ELEMENT_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                DATA_ELEMENT_ONE_UID, EVENT_UID, "test_datavalue", TEST_USERNAME
        ).isExhausted();

        assertThatCursor(db.query(EventModel.TABLE, EVENT_PROJECTION, EventModel.Columns.UID + " = ?",
                new String[]{EVENT_UID}, null, null, null)
        ).hasRow(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_UPDATE
        ).isExhausted();
    }

    @Test
    public void saveMustUpdateEventStateFromToDeleteToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(EventModel.TABLE, null, event(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_DELETE));

        TestSubscriber<Long> testSubscriber = dataEntryStore.save(
                DATA_ELEMENT_ONE_UID, "test_datavalue").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityDataValueModel.TABLE, TEDV_PROJECTION,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ?", new String[]{
                        DATA_ELEMENT_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                DATA_ELEMENT_ONE_UID, EVENT_UID, "test_datavalue", TEST_USERNAME
        ).isExhausted();

        assertThatCursor(db.query(EventModel.TABLE, EVENT_PROJECTION, EventModel.Columns.UID + " = ?",
                new String[]{EVENT_UID}, null, null, null)
        ).hasRow(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_UPDATE
        ).isExhausted();
    }

    @Test
    public void saveMustNotUpdateEventStateFromToPostToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(EventModel.TABLE, null, event(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_POST));

        TestSubscriber<Long> testSubscriber = dataEntryStore.save(
                DATA_ELEMENT_ONE_UID, "test_datavalue").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityDataValueModel.TABLE, TEDV_PROJECTION,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ?", new String[]{
                        DATA_ELEMENT_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                DATA_ELEMENT_ONE_UID, EVENT_UID, "test_datavalue", TEST_USERNAME
        ).isExhausted();

        assertThatCursor(db.query(EventModel.TABLE, EVENT_PROJECTION, EventModel.Columns.UID + " = ?",
                new String[]{EVENT_UID}, null, null, null)
        ).hasRow(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_POST
        ).isExhausted();
    }

    @Test
    public void saveMustNotUpdateEventStateFromToUpdateToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(EventModel.TABLE, null, event(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_UPDATE));

        TestSubscriber<Long> testSubscriber = dataEntryStore.save(
                DATA_ELEMENT_ONE_UID, "test_datavalue").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityDataValueModel.TABLE, TEDV_PROJECTION,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ?", new String[]{
                        DATA_ELEMENT_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                DATA_ELEMENT_ONE_UID, EVENT_UID, "test_datavalue", TEST_USERNAME
        ).isExhausted();

        assertThatCursor(db.query(EventModel.TABLE, EVENT_PROJECTION, EventModel.Columns.UID + " = ?",
                new String[]{EVENT_UID}, null, null, null)
        ).hasRow(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, PROGRAM_UID, PS_UID, State.TO_UPDATE
        ).isExhausted();
    }


    private static ContentValues programStageDataElement(String uid, String programStage,
            String dataElement, Integer sortOrder, Boolean isCompulsory) {
        ContentValues psde = new ContentValues();
        psde.put(ProgramStageDataElementModel.Columns.UID, uid);
        psde.put(ProgramStageDataElementModel.Columns.PROGRAM_STAGE, programStage);
        psde.put(ProgramStageDataElementModel.Columns.DATA_ELEMENT, dataElement);
        psde.put(ProgramStageDataElementModel.Columns.SORT_ORDER, sortOrder);
        psde.put(ProgramStageDataElementModel.Columns.COMPULSORY, isCompulsory ? 1 : 0);
        return psde;
    }

    private static ContentValues dataElement(String uid, String displayName, String valueType) {
        ContentValues dataElementTwo = new ContentValues();
        dataElementTwo.put(DataElementModel.Columns.UID, uid);
        dataElementTwo.put(DataElementModel.Columns.DISPLAY_NAME, displayName);
        dataElementTwo.put(DataElementModel.Columns.VALUE_TYPE, valueType);
        return dataElementTwo;
    }

    private static ContentValues event(String uid, Date created, String orgUnit,
            String program, String programStage, State state) {
        ContentValues event = new ContentValues();
        event.put(EventModel.Columns.UID, uid);
        event.put(EventModel.Columns.CREATED,
                BaseIdentifiableObject.DATE_FORMAT.format(created));
        event.put(EventModel.Columns.ORGANISATION_UNIT, orgUnit);
        event.put(EventModel.Columns.LAST_UPDATED,
                BaseIdentifiableObject.DATE_FORMAT.format(created));
        event.put(EventModel.Columns.PROGRAM, program);
        event.put(EventModel.Columns.PROGRAM_STAGE, programStage);
        event.put(EventModel.Columns.STATE, state.toString());
        return event;
    }

    private static ContentValues dataValue(String event, Date created, Date lastUpdated,
            String dataelement, String value, String storedBy) {
        ContentValues dataValue = new ContentValues();
        dataValue.put(TrackedEntityDataValueModel.Columns.EVENT, event);
        dataValue.put(TrackedEntityDataValueModel.Columns.CREATED,
                BaseIdentifiableObject.DATE_FORMAT.format(created));
        dataValue.put(TrackedEntityDataValueModel.Columns.LAST_UPDATED,
                BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));
        dataValue.put(TrackedEntityDataValueModel.Columns.DATA_ELEMENT, dataelement);
        dataValue.put(TrackedEntityDataValueModel.Columns.VALUE, value);
        dataValue.put(TrackedEntityDataValueModel.Columns.STORED_BY, storedBy);
        return dataValue;
    }
}
