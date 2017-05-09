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
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FormItemViewModel;
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
public class ProgramStageRepositoryImplTests {

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

        db.insert(DataElementModel.TABLE, null, dataElement("data_element_one_uid",
                "data_element_one_name", ValueType.BOOLEAN.name()));
        db.insert(DataElementModel.TABLE, null, dataElement("data_element_two_uid",
                "data_element_two_name", ValueType.LONG_TEXT.name()));
        db.insert(DataElementModel.TABLE, null, dataElement("data_element_three_uid",
                "data_element_three_name", ValueType.TEXT.name()));

        db.insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement(
                "ps_data_element_one", "ps_uid", "data_element_one_uid", 3, true));
        db.insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement(
                "ps_data_element_two", "ps_uid", "data_element_two_uid", 1, false));
        db.insert(ProgramStageDataElementModel.TABLE, null, programStageDataElement(
                "ps_data_element_three", "ps_uid", "data_element_three_uid", 2, true));

        db.insert(EventModel.TABLE, null, event("event_uid",
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                "organisation_unit_uid", "program_uid", "ps_uid", State.TO_POST));

        programStageRepository = new ProgramStageRepositoryImpl(
                databaseRule.briteDatabase(), "event_uid");
    }

    @Test
    public void fieldsShouldPropagateCorrectResults() {
        TestSubscriber<List<FormItemViewModel>> testObserver = programStageRepository.fields().test();

        // change radio button view model creation (value is not only true or false in this case)
        FormItemViewModel fieldOne = RadioButtonViewModel.create("data_element_one_uid",
                "data_element_one_name", true, false);
        FormItemViewModel fieldTwo = RadioButtonViewModel.create("data_element_two_uid",
                "data_element_two_name", false, false);
        FormItemViewModel fieldThree = RadioButtonViewModel.create("data_element_three_uid",
                "data_element_three_name", true, false);

        List<FormItemViewModel> fields = testObserver.values().get(0);

        assertThat(testObserver.valueCount()).isEqualTo(1);
        assertThat(fields.get(0)).isEqualTo(null);
        assertThat(fields.get(1)).isEqualTo(null);
        assertThat(fields.get(2)).isEqualTo(null);

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
}
