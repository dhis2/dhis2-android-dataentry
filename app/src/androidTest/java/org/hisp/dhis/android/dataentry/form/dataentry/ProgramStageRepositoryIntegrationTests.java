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
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactory;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactoryImpl;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.radiobutton.RadioButtonViewModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramStageRepositoryIntegrationTests {
    private static final String ENTER_TEXT = "enter_text";
    private static final String ENTER_LONG_TEXT = "enter_long_text";
    private static final String ENTER_NUMBER = "enter_number";
    private static final String ENTER_INTEGER = "enter_integer";
    private static final String ENTER_POSITIVE_INTEGER = "enter_positive_integer";
    private static final String ENTER_NEGATIVE_INTEGER = "enter_negative_integer";
    private static final String ENTER_POSITIVE_INTEGER_OR_ZERO = "enter_positive_integer_or_zero";

    private static final String DATA_ELEMENT_ONE_UID = "data_element_one_uid";
    private static final String DATA_ELEMENT_TWO_UID = "data_element_two_uid";
    private static final String DATA_ELEMENT_THREE_UID = "data_element_three_uid";
    private static final String EVENT_UID = "event_uid";

    private static final String TEST_DATA_VALUE_ONE = "false";
    private static final String TEST_DATA_VALUE_TWO = "test_data_value_two";
    private static final String TEST_DATA_VALUE_THREE = "test_data_value_three";

    private static final String DATA_ELEMENT_ONE_NAME = "data_element_one_name";
    private static final String DATA_ELEMENT_TWO_NAME = "data_element_two_name";
    private static final String DATA_ELEMENT_THREE_NAME = "data_element_three_name";

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private DataEntryRepository programStageRepository;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();

        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, "organisation_unit_uid");
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, "program_uid");
        db.insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = new ContentValues();
        programStage.put(ProgramStageModel.Columns.UID, "ps_uid");
        programStage.put(ProgramStageModel.Columns.PROGRAM, "program_uid");
        db.insert(ProgramStageModel.TABLE, null, programStage);

        db.insert(DataElementModel.TABLE, null, dataElement(DATA_ELEMENT_ONE_UID,
                DATA_ELEMENT_ONE_NAME, ValueType.BOOLEAN.name()));
        db.insert(DataElementModel.TABLE, null, dataElement(DATA_ELEMENT_TWO_UID,
                DATA_ELEMENT_TWO_NAME, ValueType.LONG_TEXT.name()));
        db.insert(DataElementModel.TABLE, null, dataElement(DATA_ELEMENT_THREE_UID,
                DATA_ELEMENT_THREE_NAME, ValueType.TEXT.name()));

        db.insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement(
                "ps_data_element_one", "ps_uid", DATA_ELEMENT_ONE_UID, 3, true));
        db.insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement(
                "ps_data_element_two", "ps_uid", DATA_ELEMENT_TWO_UID, 1, false));
        db.insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement(
                "ps_data_element_three", "ps_uid", DATA_ELEMENT_THREE_UID, 2, true));

        db.insert(EventModel.TABLE, null, event(EVENT_UID,
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                "organisation_unit_uid", "program_uid", "ps_uid", State.TO_POST));

        FieldViewModelFactory fieldViewModelFactory = new FieldViewModelFactoryImpl(
                ENTER_TEXT, ENTER_LONG_TEXT, ENTER_NUMBER, ENTER_INTEGER,
                ENTER_POSITIVE_INTEGER, ENTER_NEGATIVE_INTEGER, ENTER_POSITIVE_INTEGER_OR_ZERO);

        programStageRepository = new ProgramStageRepositoryImpl(
                databaseRule.briteDatabase(), fieldViewModelFactory, EVENT_UID);
    }

    @Test
    public void fieldsShouldPropagateCorrectResults() {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID,
                DATA_ELEMENT_ONE_UID, TEST_DATA_VALUE_ONE));
        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID,
                DATA_ELEMENT_TWO_UID, TEST_DATA_VALUE_TWO));
        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID,
                DATA_ELEMENT_THREE_UID, TEST_DATA_VALUE_THREE));

        TestSubscriber<List<FieldViewModel>> testObserver = programStageRepository.fields().test();

        FieldViewModel fieldOne = RadioButtonViewModel.fromRawValue(DATA_ELEMENT_ONE_UID,
                DATA_ELEMENT_ONE_NAME, true, TEST_DATA_VALUE_ONE);
        FieldViewModel fieldTwo = EditTextViewModel.create(DATA_ELEMENT_TWO_UID,
                DATA_ELEMENT_TWO_NAME, false, TEST_DATA_VALUE_TWO, ENTER_LONG_TEXT, 3);
        FieldViewModel fieldThree = EditTextViewModel.create(DATA_ELEMENT_THREE_UID,
                DATA_ELEMENT_THREE_NAME, true, TEST_DATA_VALUE_THREE, ENTER_TEXT, 1);

        List<FieldViewModel> fields = testObserver.values().get(0);

        assertThat(testObserver.valueCount()).isEqualTo(1);
        assertThat(fields.get(0)).isEqualTo(fieldTwo);
        assertThat(fields.get(1)).isEqualTo(fieldThree);
        assertThat(fields.get(2)).isEqualTo(fieldOne);
    }

    @Test
    public void fieldsShouldObserveChangesInDatabase() {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID,
                DATA_ELEMENT_ONE_UID, TEST_DATA_VALUE_ONE));
        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID,
                DATA_ELEMENT_TWO_UID, TEST_DATA_VALUE_TWO));

        FieldViewModel fieldOne = RadioButtonViewModel.fromRawValue(DATA_ELEMENT_ONE_UID,
                DATA_ELEMENT_ONE_NAME, true, TEST_DATA_VALUE_ONE);
        FieldViewModel fieldTwo = EditTextViewModel.create(DATA_ELEMENT_TWO_UID,
                DATA_ELEMENT_TWO_NAME, false, TEST_DATA_VALUE_TWO, ENTER_LONG_TEXT, 3);
        FieldViewModel fieldThree = EditTextViewModel.create(DATA_ELEMENT_THREE_UID,
                DATA_ELEMENT_THREE_NAME, true, null, ENTER_TEXT, 1);

        TestSubscriber<List<FieldViewModel>> testObserver = programStageRepository.fields().test();
        assertThat(testObserver.valueCount()).isEqualTo(1);
        assertThat(testObserver.values().get(0).get(0)).isEqualTo(fieldTwo);
        assertThat(testObserver.values().get(0).get(1)).isEqualTo(fieldThree);
        assertThat(testObserver.values().get(0).get(2)).isEqualTo(fieldOne);

        databaseRule.briteDatabase().insert(TrackedEntityDataValueModel.TABLE, dataValue(EVENT_UID,
                DATA_ELEMENT_THREE_UID, TEST_DATA_VALUE_THREE));

        FieldViewModel fieldThreeUpdated = EditTextViewModel.create(DATA_ELEMENT_THREE_UID,
                DATA_ELEMENT_THREE_NAME, true, TEST_DATA_VALUE_THREE, ENTER_TEXT, 1);
        assertThat(testObserver.valueCount()).isEqualTo(2);
        assertThat(testObserver.values().get(1).get(0)).isEqualTo(fieldTwo);
        assertThat(testObserver.values().get(1).get(1)).isEqualTo(fieldThreeUpdated);
        assertThat(testObserver.values().get(1).get(2)).isEqualTo(fieldOne);
    }

    @Test
    public void fieldsWithoutDataValuesMustBePropagated() {
        SQLiteDatabase db = databaseRule.database();

        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID,
                DATA_ELEMENT_ONE_UID, null));
        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID,
                DATA_ELEMENT_TWO_UID, null));
        db.insert(TrackedEntityDataValueModel.TABLE, null, dataValue(EVENT_UID,
                DATA_ELEMENT_THREE_UID, null));

        TestSubscriber<List<FieldViewModel>> testObserver = programStageRepository.fields().test();

        FieldViewModel fieldOne = RadioButtonViewModel.fromRawValue(DATA_ELEMENT_ONE_UID,
                DATA_ELEMENT_ONE_NAME, true, null);
        FieldViewModel fieldTwo = EditTextViewModel.create(DATA_ELEMENT_TWO_UID,
                DATA_ELEMENT_TWO_NAME, false, null, ENTER_LONG_TEXT, 3);
        FieldViewModel fieldThree = EditTextViewModel.create(DATA_ELEMENT_THREE_UID,
                DATA_ELEMENT_THREE_NAME, true, null, ENTER_TEXT, 1);

        List<FieldViewModel> fields = testObserver.values().get(0);

        assertThat(testObserver.valueCount()).isEqualTo(1);
        assertThat(fields.get(0)).isEqualTo(fieldTwo);
        assertThat(fields.get(1)).isEqualTo(fieldThree);
        assertThat(fields.get(2)).isEqualTo(fieldOne);
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
        event.put(EventModel.Columns.PROGRAM, program);
        event.put(EventModel.Columns.PROGRAM_STAGE, programStage);
        event.put(EventModel.Columns.STATE, state.toString());
        return event;
    }

    private static ContentValues dataValue(String event, String dataelement, String value) {
        ContentValues dataValue = new ContentValues();
        dataValue.put(TrackedEntityDataValueModel.Columns.EVENT, event);
        dataValue.put(TrackedEntityDataValueModel.Columns.DATA_ELEMENT, dataelement);
        dataValue.put(TrackedEntityDataValueModel.Columns.VALUE, value);
        return dataValue;
    }
}
