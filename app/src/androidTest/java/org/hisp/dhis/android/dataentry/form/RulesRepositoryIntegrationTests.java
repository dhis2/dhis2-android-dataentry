package org.hisp.dhis.android.dataentry.form;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramRuleActionModel;
import org.hisp.dhis.android.core.program.ProgramRuleActionType;
import org.hisp.dhis.android.core.program.ProgramRuleModel;
import org.hisp.dhis.android.core.program.ProgramRuleVariableModel;
import org.hisp.dhis.android.core.program.ProgramRuleVariableSourceType;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionAssign;
import org.hisp.dhis.rules.models.RuleActionDisplayKeyValuePair;
import org.hisp.dhis.rules.models.RuleActionDisplayText;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleActionShowError;
import org.hisp.dhis.rules.models.RuleActionShowWarning;
import org.hisp.dhis.rules.models.RuleValueType;
import org.hisp.dhis.rules.models.RuleVariable;
import org.hisp.dhis.rules.models.RuleVariableAttribute;
import org.hisp.dhis.rules.models.RuleVariableCurrentEvent;
import org.hisp.dhis.rules.models.RuleVariableNewestEvent;
import org.hisp.dhis.rules.models.RuleVariableNewestStageEvent;
import org.hisp.dhis.rules.models.RuleVariablePreviousEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RulesRepositoryIntegrationTests {
    private static final String TEST_PROGRAM_UID = "test_program_uid";
    private static final String TEST_PROGRAM_STAGE = "test_program_stage";
    private static final String TEST_ATTRIBUTE = "test_attribute";
    private static final String TEST_DATA_ELEMENT = "test_data_element";

    @org.junit.Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private RulesRepository rulesRepository;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();

        // Program and ProgramStage.
        db.insertOrThrow(ProgramModel.TABLE, null, program(TEST_PROGRAM_UID));
        db.insertOrThrow(ProgramStageModel.TABLE, null,
                programStage(TEST_PROGRAM_STAGE, TEST_PROGRAM_UID));

        // Insert test attributes and data elements.
        db.insertOrThrow(TrackedEntityAttributeModel.TABLE, null,
                trackedEntityAttribute(TEST_ATTRIBUTE, ValueType.BOOLEAN));
        db.insertOrThrow(DataElementModel.TABLE, null,
                dataElement(TEST_DATA_ELEMENT, ValueType.TEXT));

        rulesRepository = new RulesRepository(databaseRule.briteDatabase());
    }

    @Test
    public void rulesMustNotBlockIfNoProgramRulesInDatabase() {
        TestSubscriber<List<Rule>> subscriber = rulesRepository.rules(TEST_PROGRAM_UID).test();

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        List<Rule> rules = subscriber.values().get(0);
        assertThat(rules.size()).isEqualTo(0);
    }

    @Test
    public void rulesMustObserveChangesInDatabase() {
        TestSubscriber<List<Rule>> subscriber = rulesRepository.rules(TEST_PROGRAM_UID).test();

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        List<Rule> rules = subscriber.values().get(0);
        assertThat(rules.size()).isEqualTo(0);

        databaseRule.briteDatabase().insert(ProgramRuleModel.TABLE,
                programRule("test_rule_uid", 15, "a > b", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE));

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(2);

        rules = subscriber.values().get(1);
        assertThat(rules.size()).isEqualTo(1);
        assertThat(rules.get(0).actions()).isEmpty();
        assertThat(rules.get(0).condition()).isEqualTo("a > b");
        assertThat(rules.get(0).priority()).isEqualTo(15);
        assertThat(rules.get(0).programStage()).isEqualTo(TEST_PROGRAM_STAGE);

        databaseRule.briteDatabase().insert(ProgramRuleActionModel.TABLE,
                programRuleAction("test_action_one", "test_rule_uid", null, null,
                        ProgramRuleActionType.SHOWERROR.toString(), null, TEST_ATTRIBUTE,
                        null, "test_location", "test_content", "test_data"));

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(3);

        rules = subscriber.values().get(2);
        assertThat(rules.size()).isEqualTo(1);
        assertThat(rules.get(0).actions().size()).isEqualTo(1);
        assertThat(rules.get(0).condition()).isEqualTo("a > b");
        assertThat(rules.get(0).priority()).isEqualTo(15);
        assertThat(rules.get(0).programStage()).isEqualTo(TEST_PROGRAM_STAGE);
    }

    @Test
    public void rulesMustReturnProgramRulesWithActionsOfCertainType() {
        databaseRule.database().insertOrThrow(ProgramRuleModel.TABLE, null,
                programRule("test_rule_uid_one", 15, "a > b", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE));
        databaseRule.database().insertOrThrow(ProgramRuleModel.TABLE, null,
                programRule("test_rule_uid_two", 11, "b > c", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE));

        // first rule
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_one", "test_rule_uid_one", null, null,
                        ProgramRuleActionType.SHOWERROR.toString(), null, TEST_ATTRIBUTE,
                        null, "test_location", "test_content", "test_data"));
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_two", "test_rule_uid_one", null, null,
                        ProgramRuleActionType.DISPLAYTEXT.toString(), null, TEST_ATTRIBUTE,
                        null, "test_location", "test_content", "test_data"));
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_three", "test_rule_uid_one", null, null,
                        ProgramRuleActionType.HIDEFIELD.toString(), null, TEST_ATTRIBUTE,
                        null, "test_location", "test_content", "test_data"));
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_four", "test_rule_uid_one", null, null,
                        ProgramRuleActionType.HIDESECTION.toString(), null, TEST_ATTRIBUTE,
                        null, "test_location", "test_content", "test_data"));
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_five", "test_rule_uid_one", null, null,
                        ProgramRuleActionType.HIDEPROGRAMSTAGE.toString(), null, TEST_ATTRIBUTE,
                        null, "test_location", "test_content", "test_data"));
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_six", "test_rule_uid_one", null, null,
                        ProgramRuleActionType.WARNINGONCOMPLETE.toString(), null, TEST_ATTRIBUTE,
                        null, "test_location", "test_content", "test_data"));
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_seven", "test_rule_uid_one", null, null,
                        ProgramRuleActionType.ERRORONCOMPLETE.toString(), null, TEST_ATTRIBUTE,
                        null, "test_location", "test_content", "test_data"));

        // second rule
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_eight", "test_rule_uid_two", null, null,
                        ProgramRuleActionType.SHOWWARNING.toString(), null, null,
                        TEST_DATA_ELEMENT, "test_location", "test_content", "test_data"));
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_nine", "test_rule_uid_two", null, null,
                        ProgramRuleActionType.DISPLAYKEYVALUEPAIR.toString(), null, null,
                        TEST_DATA_ELEMENT, "test_location", "test_content", "test_data"));
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_ten", "test_rule_uid_two", null, null,
                        ProgramRuleActionType.ASSIGN.toString(), null, null,
                        TEST_DATA_ELEMENT, "test_location", "test_content", "test_data"));
        databaseRule.database().insertOrThrow(ProgramRuleActionModel.TABLE, null,
                programRuleAction("test_uid_eleven", "test_rule_uid_two", null, null,
                        ProgramRuleActionType.CREATEEVENT.toString(), null, null,
                        TEST_DATA_ELEMENT, "test_location", "test_content", "test_data"));

        TestSubscriber<List<Rule>> subscriber = rulesRepository.rules(TEST_PROGRAM_UID).test();

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        List<Rule> rules = subscriber.values().get(0);
        assertThat(rules.size()).isEqualTo(2);

        assertThat(rules.get(0).condition()).isEqualTo("a > b");
        assertThat(rules.get(0).priority()).isEqualTo(15);
        assertThat(rules.get(0).programStage()).isEqualTo(TEST_PROGRAM_STAGE);

        List<RuleAction> ruleActionsOne = rules.get(0).actions();
        assertThat(ruleActionsOne.size()).isEqualTo(3);

        assertThat(ruleActionsOne.get(0)).isInstanceOf(RuleActionShowError.class);
        assertThat(((RuleActionShowError) ruleActionsOne.get(0)).content()).isEqualTo("test_content");
        assertThat(((RuleActionShowError) ruleActionsOne.get(0)).data()).isEqualTo("test_data");
        assertThat(((RuleActionShowError) ruleActionsOne.get(0)).field()).isEqualTo(TEST_ATTRIBUTE);

        assertThat(ruleActionsOne.get(1)).isInstanceOf(RuleActionDisplayText.class);
        assertThat(((RuleActionDisplayText) ruleActionsOne.get(1)).content()).isEqualTo("test_content");
        assertThat(((RuleActionDisplayText) ruleActionsOne.get(1)).data()).isEqualTo("test_data");
        assertThat(((RuleActionDisplayText) ruleActionsOne.get(1)).location())
                .isEqualTo(RuleActionDisplayText.LOCATION_INDICATOR_WIDGET);

        assertThat(ruleActionsOne.get(2)).isInstanceOf(RuleActionHideField.class);
        assertThat(((RuleActionHideField) ruleActionsOne.get(2)).content()).isEqualTo("test_content");
        assertThat(((RuleActionHideField) ruleActionsOne.get(2)).field()).isEqualTo(TEST_ATTRIBUTE);

        List<RuleAction> ruleActionsTwo = rules.get(1).actions();
        assertThat(ruleActionsTwo.size()).isEqualTo(3);

        assertThat(ruleActionsTwo.get(0)).isInstanceOf(RuleActionShowWarning.class);
        assertThat(((RuleActionShowWarning) ruleActionsTwo.get(0)).content()).isEqualTo("test_content");
        assertThat(((RuleActionShowWarning) ruleActionsTwo.get(0)).data()).isEqualTo("test_data");
        assertThat(((RuleActionShowWarning) ruleActionsTwo.get(0)).field()).isEqualTo(TEST_DATA_ELEMENT);

        assertThat(ruleActionsTwo.get(1)).isInstanceOf(RuleActionDisplayKeyValuePair.class);
        assertThat(((RuleActionDisplayKeyValuePair) ruleActionsTwo.get(1)).content()).isEqualTo("test_content");
        assertThat(((RuleActionDisplayKeyValuePair) ruleActionsTwo.get(1)).data()).isEqualTo("test_data");
        assertThat(((RuleActionDisplayKeyValuePair) ruleActionsTwo.get(1)).location())
                .isEqualTo(RuleActionDisplayText.LOCATION_INDICATOR_WIDGET);

        assertThat(ruleActionsTwo.get(2)).isInstanceOf(RuleActionAssign.class);
        assertThat(((RuleActionAssign) ruleActionsTwo.get(2)).content()).isEqualTo("test_content");
        assertThat(((RuleActionAssign) ruleActionsTwo.get(2)).data()).isEqualTo("test_data");
        assertThat(((RuleActionAssign) ruleActionsTwo.get(2)).field()).isEqualTo(TEST_DATA_ELEMENT);
    }

    @Test
    public void rulesMustBeFilteredByProgramUid() {
        databaseRule.database().insertOrThrow(ProgramModel.TABLE, null,
                program("test_another_program_uid"));

        databaseRule.database().insertOrThrow(ProgramRuleModel.TABLE, null,
                programRule("test_rule_uid_one", 15, "a > b", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE));
        databaseRule.database().insertOrThrow(ProgramRuleModel.TABLE, null,
                programRule("test_rule_uid_two", 11, "b > c", "test_another_program_uid", TEST_PROGRAM_STAGE));

        TestSubscriber<List<Rule>> subscriber = rulesRepository.rules(TEST_PROGRAM_UID).test();

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        List<Rule> rules = subscriber.values().get(0);
        assertThat(rules.size()).isEqualTo(1);

        assertThat(rules.get(0).condition()).isEqualTo("a > b");
        assertThat(rules.get(0).priority()).isEqualTo(15);
        assertThat(rules.get(0).programStage()).isEqualTo(TEST_PROGRAM_STAGE);
    }

    @Test
    public void ruleVariablesMustNotBlockIfNoProgramRulesInDatabase() {
        TestSubscriber<List<RuleVariable>> subscriber = rulesRepository.ruleVariables(TEST_PROGRAM_UID).test();

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        List<RuleVariable> ruleVariables = subscriber.values().get(0);
        assertThat(ruleVariables.size()).isEqualTo(0);
    }

    @Test
    public void ruleVariablesMustObserveChangesInDatabase() {
        TestSubscriber<List<RuleVariable>> subscriber = rulesRepository.ruleVariables(TEST_PROGRAM_UID).test();

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        List<RuleVariable> ruleVariables = subscriber.values().get(0);
        assertThat(ruleVariables.size()).isEqualTo(0);

        databaseRule.database().insertOrThrow(DataElementModel.TABLE, null,
                dataElement("test_data_element_one", ValueType.EMAIL));
        databaseRule.briteDatabase().insert(ProgramRuleVariableModel.TABLE,
                programRuleVariable("variable_uid_one", "name_one", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE,
                        "test_data_element_one", null, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT));

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(2);

        ruleVariables = subscriber.values().get(1);
        assertThat(ruleVariables.size()).isEqualTo(1);

        assertThat(ruleVariables.get(0)).isInstanceOf(RuleVariableCurrentEvent.class);
        assertThat(((RuleVariableCurrentEvent) ruleVariables.get(0)).name()).isEqualTo("name_one");
        assertThat(((RuleVariableCurrentEvent) ruleVariables.get(0)).dataElement()).isEqualTo("test_data_element_one");
        assertThat(((RuleVariableCurrentEvent) ruleVariables.get(0)).dataElementType()).isEqualTo(RuleValueType.TEXT);
    }

    @Test
    public void rulesVariablesMustReturnVariablesOfCertainType() {
        databaseRule.database().insertOrThrow(DataElementModel.TABLE, null,
                dataElement("test_data_element_one", ValueType.EMAIL));
        databaseRule.database().insertOrThrow(DataElementModel.TABLE, null,
                dataElement("test_data_element_two", ValueType.BOOLEAN));
        databaseRule.database().insertOrThrow(DataElementModel.TABLE, null,
                dataElement("test_data_element_three", ValueType.NUMBER));
        databaseRule.database().insertOrThrow(TrackedEntityAttributeModel.TABLE, null,
                dataElement("test_attribute_one", ValueType.DATETIME));

        databaseRule.database().insertOrThrow(ProgramRuleVariableModel.TABLE, null,
                programRuleVariable("variable_uid_one", "name_one", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE,
                        null, "test_attribute_one", ProgramRuleVariableSourceType.TEI_ATTRIBUTE));
        databaseRule.database().insertOrThrow(ProgramRuleVariableModel.TABLE, null,
                programRuleVariable("variable_uid_two", "name_two", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE,
                        "test_data_element_one", null, ProgramRuleVariableSourceType.DATAELEMENT_CURRENT_EVENT));
        databaseRule.database().insertOrThrow(ProgramRuleVariableModel.TABLE, null,
                programRuleVariable("variable_uid_three", "name_three", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE,
                        "test_data_element_two", null, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM));
        databaseRule.database().insertOrThrow(ProgramRuleVariableModel.TABLE, null,
                programRuleVariable("variable_uid_four", "name_four", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE,
                        "test_data_element_three", null, ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE));
        databaseRule.database().insertOrThrow(ProgramRuleVariableModel.TABLE, null,
                programRuleVariable("variable_uid_five", "name_five", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE,
                        "test_data_element_three", null, ProgramRuleVariableSourceType.DATAELEMENT_PREVIOUS_EVENT));
        databaseRule.database().insertOrThrow(ProgramRuleVariableModel.TABLE, null,
                programRuleVariable("variable_uid_six", "name_six", TEST_PROGRAM_UID, TEST_PROGRAM_STAGE,
                        null, "test_attribute_one", ProgramRuleVariableSourceType.CALCULATED_VALUE));

        TestSubscriber<List<RuleVariable>> subscriber = rulesRepository.ruleVariables(TEST_PROGRAM_UID).test();

        subscriber.assertNotComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        List<RuleVariable> ruleVariables = subscriber.values().get(0);
        assertThat(ruleVariables.size()).isEqualTo(5);

        assertThat(ruleVariables.get(0)).isInstanceOf(RuleVariableAttribute.class);
        assertThat(((RuleVariableAttribute) ruleVariables.get(0)).name()).isEqualTo("name_one");
        assertThat(((RuleVariableAttribute) ruleVariables.get(0)).trackedEntityAttribute()).isEqualTo("test_attribute_one");
        assertThat(((RuleVariableAttribute) ruleVariables.get(0)).trackedEntityAttributeType()).isEqualTo(RuleValueType.TEXT);

        assertThat(ruleVariables.get(1)).isInstanceOf(RuleVariableCurrentEvent.class);
        assertThat(((RuleVariableCurrentEvent) ruleVariables.get(1)).name()).isEqualTo("name_two");
        assertThat(((RuleVariableCurrentEvent) ruleVariables.get(1)).dataElement()).isEqualTo("test_data_element_one");
        assertThat(((RuleVariableCurrentEvent) ruleVariables.get(1)).dataElementType()).isEqualTo(RuleValueType.TEXT);

        assertThat(ruleVariables.get(2)).isInstanceOf(RuleVariableNewestEvent.class);
        assertThat(((RuleVariableNewestEvent) ruleVariables.get(2)).name()).isEqualTo("name_three");
        assertThat(((RuleVariableNewestEvent) ruleVariables.get(2)).dataElement()).isEqualTo("test_data_element_two");
        assertThat(((RuleVariableNewestEvent) ruleVariables.get(2)).dataElementType()).isEqualTo(RuleValueType.BOOLEAN);

        assertThat(ruleVariables.get(3)).isInstanceOf(RuleVariableNewestStageEvent.class);
        assertThat(((RuleVariableNewestStageEvent) ruleVariables.get(3)).name()).isEqualTo("name_four");
        assertThat(((RuleVariableNewestStageEvent) ruleVariables.get(3)).dataElement()).isEqualTo("test_data_element_three");
        assertThat(((RuleVariableNewestStageEvent) ruleVariables.get(3)).dataElementType()).isEqualTo(RuleValueType.NUMERIC);

        assertThat(ruleVariables.get(4)).isInstanceOf(RuleVariablePreviousEvent.class);
        assertThat(((RuleVariablePreviousEvent) ruleVariables.get(4)).name()).isEqualTo("name_five");
        assertThat(((RuleVariablePreviousEvent) ruleVariables.get(4)).dataElement()).isEqualTo("test_data_element_three");
        assertThat(((RuleVariablePreviousEvent) ruleVariables.get(4)).dataElementType()).isEqualTo(RuleValueType.NUMERIC);
    }

    @NonNull
    private static ContentValues program(@NonNull String uid) {
        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, uid);
        return program;
    }

    @NonNull
    private static ContentValues programStage(@NonNull String uid, @NonNull String program) {
        ContentValues stage = new ContentValues();
        stage.put(ProgramStageModel.Columns.UID, uid);
        stage.put(ProgramStageModel.Columns.PROGRAM, program);
        return stage;
    }

    @NonNull
    private static ContentValues programRule(@NonNull String uid, @NonNull Integer priority,
            @NonNull String condition, @NonNull String program, @NonNull String programStage) {
        ContentValues programRule = new ContentValues();
        programRule.put(ProgramRuleModel.Columns.UID, uid);
        programRule.put(ProgramRuleModel.Columns.PRIORITY, priority);
        programRule.put(ProgramRuleModel.Columns.CONDITION, condition);
        programRule.put(ProgramRuleModel.Columns.PROGRAM, program);
        programRule.put(ProgramRuleModel.Columns.PROGRAM_STAGE, programStage);
        return programRule;
    }

    @NonNull
    private static ContentValues programRuleAction(@NonNull String uid, @NonNull String programRule,
            @Nullable String programStage, @Nullable String stageSection, @Nullable String ruleActionType,
            @Nullable String programIndicator, @Nullable String trackedEntityAttribute, @Nullable String dataElement,
            @Nullable String location, @Nullable String content, @Nullable String data) {
        ContentValues action = new ContentValues();
        action.put(ProgramRuleActionModel.Columns.UID, uid);
        action.put(ProgramRuleActionModel.Columns.PROGRAM_RULE, programRule);
        action.put(ProgramRuleActionModel.Columns.PROGRAM_STAGE, programStage);
        action.put(ProgramRuleActionModel.Columns.PROGRAM_STAGE_SECTION, stageSection);
        action.put(ProgramRuleActionModel.Columns.PROGRAM_RULE_ACTION_TYPE, ruleActionType);
        action.put(ProgramRuleActionModel.Columns.PROGRAM_INDICATOR, programIndicator);
        action.put(ProgramRuleActionModel.Columns.TRACKED_ENTITY_ATTRIBUTE, trackedEntityAttribute);
        action.put(ProgramRuleActionModel.Columns.DATA_ELEMENT, dataElement);
        action.put(ProgramRuleActionModel.Columns.LOCATION, location);
        action.put(ProgramRuleActionModel.Columns.CONTENT, content);
        action.put(ProgramRuleActionModel.Columns.DATA, data);
        return action;
    }

    @NonNull
    private static ContentValues programRuleVariable(@NonNull String uid, @NonNull String name,
            @NonNull String program, @Nullable String stage, @Nullable String dataElement,
            @Nullable String trackedEntityAttribute, @NonNull ProgramRuleVariableSourceType sourceType) {
        ContentValues ruleVariable = new ContentValues();
        ruleVariable.put(ProgramRuleVariableModel.Columns.UID, uid);
        ruleVariable.put(ProgramRuleVariableModel.Columns.NAME, name);
        ruleVariable.put(ProgramRuleVariableModel.Columns.PROGRAM, program);
        ruleVariable.put(ProgramRuleVariableModel.Columns.PROGRAM_STAGE, stage);
        ruleVariable.put(ProgramRuleVariableModel.Columns.DATA_ELEMENT, dataElement);
        ruleVariable.put(ProgramRuleVariableModel.Columns.TRACKED_ENTITY_ATTRIBUTE, trackedEntityAttribute);
        ruleVariable.put(ProgramRuleVariableModel.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE, sourceType.toString());
        return ruleVariable;
    }

    @NonNull
    private static ContentValues trackedEntityAttribute(@NonNull String uid, @NonNull ValueType valueType) {
        ContentValues attribute = new ContentValues();
        attribute.put(TrackedEntityAttributeModel.Columns.UID, uid);
        attribute.put(TrackedEntityAttributeModel.Columns.VALUE_TYPE, valueType.toString());
        return attribute;
    }

    @NonNull
    private static ContentValues dataElement(@NonNull String uid, @NonNull ValueType valueType) {
        ContentValues dataElement = new ContentValues();
        dataElement.put(DataElementModel.Columns.UID, uid);
        dataElement.put(DataElementModel.Columns.VALUE_TYPE, valueType.toString());
        return dataElement;
    }

}
