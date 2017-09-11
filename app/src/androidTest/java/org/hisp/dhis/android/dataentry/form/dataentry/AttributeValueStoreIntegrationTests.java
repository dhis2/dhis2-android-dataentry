package org.hisp.dhis.android.dataentry.form.dataentry;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel.Columns;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
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
public class AttributeValueStoreIntegrationTests {
    private static final String[] TEAV_PROJECTION = {
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.TRACKED_ENTITY_ATTRIBUTE,
            Columns.TRACKED_ENTITY_INSTANCE,
            Columns.VALUE,
    };

    private static final String[] TEI_PROJECTION = {
            TrackedEntityInstanceModel.Columns.UID,
            TrackedEntityInstanceModel.Columns.CREATED,
            TrackedEntityInstanceModel.Columns.LAST_UPDATED,
            TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT,
            TrackedEntityInstanceModel.Columns.TRACKED_ENTITY,
            TrackedEntityInstanceModel.Columns.STATE
    };

    private static final String[] ENROLLMENT_PROJECTION = {
            EnrollmentModel.Columns.UID,
            EnrollmentModel.Columns.PROGRAM,
            EnrollmentModel.Columns.ORGANISATION_UNIT,
            EnrollmentModel.Columns.TRACKED_ENTITY_INSTANCE,
            EnrollmentModel.Columns.STATE
    };

    private static final String ATTRIBUTE_ONE_UID = "attribute_one_uid";
    private static final String ATTRIBUTE_ONE_NAME = "attribute_one_name";

    private static final String ATTRIBUTE_TWO_UID = "attribute_two_uid";
    private static final String ATTRIBUTE_TWO_NAME = "attribute_two_name";

    private static final String ATTRIBUTE_THREE_UID = "attribute_three_uid";
    private static final String ATTRIBUTE_THREE_NAME = "attribute_three_name";

    private static final String TEI_UID = "tei_uid";
    private static final String ENROLLMENT_UID = "enrollment_uid";
    private static final String ORGANISATION_UNIT_UID = "organisation_unit_uid";
    private static final String TRACKED_ENTITY_UID = "tracked_entity_uid";
    private static final String PROGRAM_UID = "program_uid";
    private static final String CURRENT_DATE = "2016-04-06T00:05:57.495";

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private DataEntryStore dataEntryStore;
    private Date currentDate;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();

        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, ORGANISATION_UNIT_UID);
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityModel.Columns.UID, TRACKED_ENTITY_UID);
        db.insert(TrackedEntityModel.TABLE, null, trackedEntity);

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, PROGRAM_UID);
        program.put(ProgramModel.Columns.TRACKED_ENTITY, TRACKED_ENTITY_UID);
        db.insert(ProgramModel.TABLE, null, program);

        db.insert(TrackedEntityAttributeModel.TABLE, null, attribute(ATTRIBUTE_ONE_UID,
                ATTRIBUTE_ONE_NAME, ValueType.BOOLEAN.name()));
        db.insert(TrackedEntityAttributeModel.TABLE, null, attribute(ATTRIBUTE_TWO_UID,
                ATTRIBUTE_TWO_NAME, ValueType.LONG_TEXT.name()));
        db.insert(TrackedEntityAttributeModel.TABLE, null, attribute(ATTRIBUTE_THREE_UID,
                ATTRIBUTE_THREE_NAME, ValueType.TEXT.name()));

        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null, ptea(
                "ptea_one", PROGRAM_UID, ATTRIBUTE_ONE_UID, 3, true));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null, ptea(
                "ptea_two", PROGRAM_UID, ATTRIBUTE_TWO_UID, 1, false));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null, ptea(
                "ptea_three", PROGRAM_UID, ATTRIBUTE_THREE_UID, 2, true));

        // provider of time stamps for data values
        currentDate = BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE);
        CurrentDateProvider currentDateProvider = () -> currentDate;

        // class under tests
        dataEntryStore = new AttributeValueStore(databaseRule.briteDatabase(),
                currentDateProvider, ENROLLMENT_UID);
    }

    @Test
    public void saveShouldUpdateExistingAttributeValue() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityInstanceModel.TABLE, null, tei(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_POST));
        db.insert(EnrollmentModel.TABLE, null, enrollment(ENROLLMENT_UID,
                PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.TO_POST));

        Date createdDate = new Date();
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, attributeValue(TEI_UID, createdDate,
                createdDate, ATTRIBUTE_ONE_UID, "test_attribute_value"));

        TestSubscriber<Long> testSubscriber = dataEntryStore.save(
                ATTRIBUTE_ONE_UID, "test_updated_attribute_value").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityAttributeValueModel.TABLE, TEAV_PROJECTION,
                Columns.TRACKED_ENTITY_ATTRIBUTE + " = ?", new String[]{
                        ATTRIBUTE_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(createdDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ATTRIBUTE_ONE_UID, TEI_UID, "test_updated_attribute_value"
        ).isExhausted();
    }

    @Test
    public void saveShouldNullifyExistingAttributeValue() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityInstanceModel.TABLE, null, tei(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_POST));
        db.insert(EnrollmentModel.TABLE, null, enrollment(ENROLLMENT_UID,
                PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.TO_POST));

        Date createdDate = new Date();
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, attributeValue(TEI_UID, createdDate,
                createdDate, ATTRIBUTE_ONE_UID, "test_attribute_value"));

        TestSubscriber<Long> testSubscriber = dataEntryStore
                .save(ATTRIBUTE_ONE_UID, null).test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityAttributeValueModel.TABLE, TEAV_PROJECTION,
                Columns.TRACKED_ENTITY_ATTRIBUTE + " = ?", new String[]{
                        ATTRIBUTE_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(createdDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ATTRIBUTE_ONE_UID, TEI_UID, null
        ).isExhausted();
    }

    @Test
    public void saveShouldInsertNewAttributeValue() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityInstanceModel.TABLE, null, tei(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_POST));
        db.insert(EnrollmentModel.TABLE, null, enrollment(ENROLLMENT_UID,
                PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.TO_POST));

        TestSubscriber<Long> testSubscriber = dataEntryStore
                .save(ATTRIBUTE_ONE_UID, "test_attribute_value").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityAttributeValueModel.TABLE, TEAV_PROJECTION,
                Columns.TRACKED_ENTITY_ATTRIBUTE + " = ?", new String[]{
                        ATTRIBUTE_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ATTRIBUTE_ONE_UID, TEI_UID, "test_attribute_value"
        ).isExhausted();
    }

    @Test
    public void saveMustUpdateTeiStateFromSyncedToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityInstanceModel.TABLE, null, tei(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.SYNCED));
        db.insert(EnrollmentModel.TABLE, null, enrollment(ENROLLMENT_UID,
                PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED));

        TestSubscriber<Long> testSubscriber = dataEntryStore
                .save(ATTRIBUTE_ONE_UID, "test_attribute_value").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityAttributeValueModel.TABLE, TEAV_PROJECTION,
                Columns.TRACKED_ENTITY_ATTRIBUTE + " = ?", new String[]{
                        ATTRIBUTE_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ATTRIBUTE_ONE_UID, TEI_UID, "test_attribute_value"
        ).isExhausted();

        assertThatCursor(db.query(TrackedEntityInstanceModel.TABLE, TEI_PROJECTION,
                TrackedEntityInstanceModel.Columns.UID + " = ?", new String[]{
                        TEI_UID}, null, null, null)
        ).hasRow(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_UPDATE.toString()
        ).isExhausted();

        assertThatCursor(db.query(EnrollmentModel.TABLE, ENROLLMENT_PROJECTION,
                EnrollmentModel.Columns.UID + " = ?", new String[]{
                        ENROLLMENT_UID}, null, null, null)
        ).hasRow(ENROLLMENT_UID, PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED.toString()
        ).isExhausted();
    }

    @Test
    public void saveMustUpdateTeiStateFromErrorToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityInstanceModel.TABLE, null, tei(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.ERROR));
        db.insert(EnrollmentModel.TABLE, null, enrollment(ENROLLMENT_UID,
                PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED));

        TestSubscriber<Long> testSubscriber = dataEntryStore
                .save(ATTRIBUTE_ONE_UID, "test_attribute_value").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityAttributeValueModel.TABLE, TEAV_PROJECTION,
                Columns.TRACKED_ENTITY_ATTRIBUTE + " = ?", new String[]{
                        ATTRIBUTE_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ATTRIBUTE_ONE_UID, TEI_UID, "test_attribute_value"
        ).isExhausted();

        assertThatCursor(db.query(TrackedEntityInstanceModel.TABLE, TEI_PROJECTION,
                TrackedEntityInstanceModel.Columns.UID + " = ?", new String[]{
                        TEI_UID}, null, null, null)
        ).hasRow(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_UPDATE.toString()
        ).isExhausted();

        assertThatCursor(db.query(EnrollmentModel.TABLE, ENROLLMENT_PROJECTION,
                EnrollmentModel.Columns.UID + " = ?", new String[]{
                        ENROLLMENT_UID}, null, null, null)
        ).hasRow(ENROLLMENT_UID, PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED.toString()
        ).isExhausted();
    }

    @Test
    public void saveMustUpdateTeiStateFromToDeleteToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityInstanceModel.TABLE, null, tei(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_DELETE));
        db.insert(EnrollmentModel.TABLE, null, enrollment(ENROLLMENT_UID,
                PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED));

        TestSubscriber<Long> testSubscriber = dataEntryStore
                .save(ATTRIBUTE_ONE_UID, "test_attribute_value").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityAttributeValueModel.TABLE, TEAV_PROJECTION,
                Columns.TRACKED_ENTITY_ATTRIBUTE + " = ?", new String[]{
                        ATTRIBUTE_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ATTRIBUTE_ONE_UID, TEI_UID, "test_attribute_value"
        ).isExhausted();

        assertThatCursor(db.query(TrackedEntityInstanceModel.TABLE, TEI_PROJECTION,
                TrackedEntityInstanceModel.Columns.UID + " = ?", new String[]{
                        TEI_UID}, null, null, null)
        ).hasRow(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_UPDATE.toString()
        ).isExhausted();

        assertThatCursor(db.query(EnrollmentModel.TABLE, ENROLLMENT_PROJECTION,
                EnrollmentModel.Columns.UID + " = ?", new String[]{
                        ENROLLMENT_UID}, null, null, null)
        ).hasRow(ENROLLMENT_UID, PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED.toString()
        ).isExhausted();
    }

    @Test
    public void saveMustNotUpdateTeiStateFromToPostToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityInstanceModel.TABLE, null, tei(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_POST));
        db.insert(EnrollmentModel.TABLE, null, enrollment(ENROLLMENT_UID,
                PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED));

        TestSubscriber<Long> testSubscriber = dataEntryStore
                .save(ATTRIBUTE_ONE_UID, "test_attribute_value").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityAttributeValueModel.TABLE, TEAV_PROJECTION,
                Columns.TRACKED_ENTITY_ATTRIBUTE + " = ?", new String[]{
                        ATTRIBUTE_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ATTRIBUTE_ONE_UID, TEI_UID, "test_attribute_value"
        ).isExhausted();

        assertThatCursor(db.query(TrackedEntityInstanceModel.TABLE, TEI_PROJECTION,
                TrackedEntityInstanceModel.Columns.UID + " = ?", new String[]{
                        TEI_UID}, null, null, null)
        ).hasRow(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_POST.toString()
        ).isExhausted();

        assertThatCursor(db.query(EnrollmentModel.TABLE, ENROLLMENT_PROJECTION,
                EnrollmentModel.Columns.UID + " = ?", new String[]{
                        ENROLLMENT_UID}, null, null, null)
        ).hasRow(ENROLLMENT_UID, PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED.toString()
        ).isExhausted();
    }

    @Test
    public void saveMustUpdateTeiStateFromToUpdateToUpdate() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityInstanceModel.TABLE, null, tei(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_UPDATE));
        db.insert(EnrollmentModel.TABLE, null, enrollment(ENROLLMENT_UID,
                PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED));

        TestSubscriber<Long> testSubscriber = dataEntryStore
                .save(ATTRIBUTE_ONE_UID, "test_attribute_value").test();
        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();
        testSubscriber.assertTerminated();

        assertThat(testSubscriber.valueCount()).isEqualTo(1);
        assertThat(testSubscriber.values().get(0)).isEqualTo(1);

        assertThatCursor(db.query(TrackedEntityAttributeValueModel.TABLE, TEAV_PROJECTION,
                Columns.TRACKED_ENTITY_ATTRIBUTE + " = ?", new String[]{
                        ATTRIBUTE_ONE_UID}, null, null, null)
        ).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ATTRIBUTE_ONE_UID, TEI_UID, "test_attribute_value"
        ).isExhausted();

        assertThatCursor(db.query(TrackedEntityInstanceModel.TABLE, TEI_PROJECTION,
                TrackedEntityInstanceModel.Columns.UID + " = ?", new String[]{
                        TEI_UID}, null, null, null)
        ).hasRow(TEI_UID,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                BaseIdentifiableObject.DATE_FORMAT.format(currentDate),
                ORGANISATION_UNIT_UID, TRACKED_ENTITY_UID, State.TO_UPDATE.toString()
        ).isExhausted();

        assertThatCursor(db.query(EnrollmentModel.TABLE, ENROLLMENT_PROJECTION,
                EnrollmentModel.Columns.UID + " = ?", new String[]{
                        ENROLLMENT_UID}, null, null, null)
        ).hasRow(ENROLLMENT_UID, PROGRAM_UID, ORGANISATION_UNIT_UID, TEI_UID, State.SYNCED.toString()
        ).isExhausted();
    }

    private static ContentValues enrollment(String uid, String program,
            String orgUnit, String tei, State state) {
        ContentValues enrollment = new ContentValues();
        enrollment.put(EnrollmentModel.Columns.UID, uid);
        enrollment.put(EnrollmentModel.Columns.PROGRAM, program);
        enrollment.put(EnrollmentModel.Columns.ORGANISATION_UNIT, orgUnit);
        enrollment.put(EnrollmentModel.Columns.TRACKED_ENTITY_INSTANCE, tei);
        enrollment.put(EnrollmentModel.Columns.STATE, state.toString());
        return enrollment;
    }

    private static ContentValues tei(String uid, Date created, String orgUnit,
            String trackedEntity, State state) {
        ContentValues trackedEntityInstance = new ContentValues();
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.UID, uid);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.CREATED,
                BaseIdentifiableObject.DATE_FORMAT.format(created));
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.LAST_UPDATED,
                BaseIdentifiableObject.DATE_FORMAT.format(created));
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT, orgUnit);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.TRACKED_ENTITY, trackedEntity);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.STATE, state.toString());
        return trackedEntityInstance;
    }

    private static ContentValues attribute(String uid, String displayName, String valueType) {
        ContentValues attribute = new ContentValues();
        attribute.put(TrackedEntityAttributeModel.Columns.UID, uid);
        attribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_NAME, displayName);
        attribute.put(TrackedEntityAttributeModel.Columns.VALUE_TYPE, valueType);
        return attribute;
    }


    private static ContentValues ptea(String uid, String program,
            String attribute, Integer sortOrder, Boolean isCompulsory) {
        ContentValues psde = new ContentValues();
        psde.put(ProgramTrackedEntityAttributeModel.Columns.UID, uid);
        psde.put(ProgramTrackedEntityAttributeModel.Columns.PROGRAM, program);
        psde.put(ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE, attribute);
        psde.put(ProgramTrackedEntityAttributeModel.Columns.SORT_ORDER, sortOrder);
        psde.put(ProgramTrackedEntityAttributeModel.Columns.MANDATORY, isCompulsory ? 1 : 0);
        return psde;
    }

    private static ContentValues attributeValue(String tei, Date created, Date lastUpdated,
            String attribute, String value) {
        ContentValues attributeValue = new ContentValues();
        attributeValue.put(Columns.TRACKED_ENTITY_INSTANCE, tei);
        attributeValue.put(Columns.TRACKED_ENTITY_ATTRIBUTE, attribute);
        attributeValue.put(Columns.CREATED, BaseIdentifiableObject
                .DATE_FORMAT.format(created));
        attributeValue.put(Columns.LAST_UPDATED, BaseIdentifiableObject
                .DATE_FORMAT.format(lastUpdated));
        attributeValue.put(Columns.VALUE, value);
        return attributeValue;
    }
}
