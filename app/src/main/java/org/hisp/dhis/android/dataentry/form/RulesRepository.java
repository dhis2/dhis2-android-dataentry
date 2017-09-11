package org.hisp.dhis.android.dataentry.form;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.program.ProgramRuleActionModel;
import org.hisp.dhis.android.core.program.ProgramRuleActionType;
import org.hisp.dhis.android.core.program.ProgramRuleModel;
import org.hisp.dhis.android.core.program.ProgramRuleVariableModel;
import org.hisp.dhis.android.core.program.ProgramRuleVariableSourceType;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.tuples.Quartet;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import rx.Observable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;
import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

@SuppressWarnings("PMD")
final class RulesRepository {
    private static final String QUERY_RULES = "SELECT\n" +
            "  ProgramRule.uid, \n" +
            "  ProgramRule.programStage,\n" +
            "  ProgramRule.priority,\n" +
            "  ProgramRule.condition\n" +
            "FROM ProgramRule\n" +
            "WHERE program = ?;";

    private static final String QUERY_VARIABLES = "SELECT\n" +
            "  name,\n" +
            "  programStage,\n" +
            "  programRuleVariableSourceType,\n" +
            "  dataElement,\n" +
            "  trackedEntityAttribute,\n" +
            "  Element.type,\n" +
            "  Attribute.type\n" +
            "FROM ProgramRuleVariable\n" +
            "  LEFT OUTER JOIN (\n" +
            "    SELECT\n" +
            "      uid as elementUid,\n" +
            "      valueType AS type\n" +
            "    FROM DataElement\n" +
            "  ) AS Element ON ProgramRuleVariable.dataElement = Element.elementUid\n" +
            "  LEFT OUTER JOIN (\n" +
            "    SELECT\n" +
            "      uid as attributeUid,\n" +
            "      valueType AS type\n" +
            "    FROM TrackedEntityAttribute\n" +
            "  ) AS Attribute ON ProgramRuleVariable.trackedEntityAttribute = Attribute.attributeUid\n" +
            "WHERE program = ? AND programRuleVariableSourceType IN (\n" +
            "  \"DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE\",\n" +
            "  \"DATAELEMENT_NEWEST_EVENT_PROGRAM\",\n" +
            "  \"DATAELEMENT_CURRENT_EVENT\",\n" +
            "  \"DATAELEMENT_PREVIOUS_EVENT\",\n" +
            "  \"TEI_ATTRIBUTE\"\n" +
            ");";

    private static final String QUERY_ACTIONS = "SELECT\n" +
            "  ProgramRuleAction.programRule,\n" +
            "  ProgramRuleAction.programStage,\n" +
            "  ProgramRuleAction.programStageSection,\n" +
            "  ProgramRuleAction.programRuleActionType,\n" +
            "  ProgramRuleAction.programIndicator,\n" +
            "  ProgramRuleAction.trackedEntityAttribute,\n" +
            "  ProgramRuleAction.dataElement,\n" +
            "  ProgramRuleAction.location,\n" +
            "  ProgramRuleAction.content,\n" +
            "  ProgramRuleAction.data\n" +
            "FROM ProgramRuleAction\n" +
            "  INNER JOIN ProgramRule ON ProgramRuleAction.programRule = ProgramRule.uid\n" +
            "WHERE program = ? AND ProgramRuleAction.programRuleActionType IN (\n" +
            "  \"DISPLAYKEYVALUEPAIR\",\n" +
            "  \"DISPLAYTEXT\",\n" +
            "  \"HIDEFIELD\",\n" +
            "  \"ASSIGN\",\n" +
            "  \"SHOWWARNING\",\n" +
            "  \"SHOWERROR\"" +
            ");";

    @NonNull
    private final BriteDatabase briteDatabase;

    RulesRepository(@NonNull BriteDatabase briteDatabase) {
        this.briteDatabase = briteDatabase;
    }

    @NonNull
    Flowable<List<Rule>> rules(@NonNull String programUid) {
        return Flowable.combineLatest(queryRules(programUid),
                queryRuleActions(programUid), RulesRepository::mapActionsToRules);
    }

    @NonNull
    Flowable<List<RuleVariable>> ruleVariables(@NonNull String programUid) {
        return toV2Flowable(briteDatabase.createQuery(ProgramRuleVariableModel.TABLE, QUERY_VARIABLES, programUid)
                .mapToList(RulesRepository::mapToRuleVariable));
    }

    @NonNull
    private Flowable<List<Quartet<String, String, Integer, String>>> queryRules(
            @NonNull String programUid) {
        return toV2Flowable(briteDatabase.createQuery(ProgramRuleModel.TABLE, QUERY_RULES, programUid)
                .mapToList(RulesRepository::mapToQuartet));
    }

    @NonNull
    private Flowable<Map<String, Collection<RuleAction>>> queryRuleActions(@NonNull String programUid) {
        return toV2Flowable(briteDatabase.createQuery(ProgramRuleActionModel.TABLE, QUERY_ACTIONS, programUid)
                .mapToList(RulesRepository::mapToActionPairs)
                .switchMap(pairs -> Observable.from(pairs)
                        .toMultimap(Pair::val0, Pair::val1)));
    }

    @NonNull
    private static List<Rule> mapActionsToRules(
            @NonNull List<Quartet<String, String, Integer, String>> rawRules,
            @NonNull Map<String, Collection<RuleAction>> ruleActions) {
        List<Rule> rules = new ArrayList<>();

        for (Quartet<String, String, Integer, String> rawRule : rawRules) {
            Collection<RuleAction> actions = ruleActions.get(rawRule.val0());

            if (actions == null) {
                actions = new ArrayList<>();
            }

            rules.add(Rule.create(rawRule.val1(), rawRule.val2(),
                    rawRule.val3(), new ArrayList<>(actions)));
        }

        return rules;
    }

    @NonNull
    private static Quartet<String, String, Integer, String> mapToQuartet(@NonNull Cursor cursor) {
        String uid = cursor.getString(0);
        String condition = cursor.getString(3);

        String stage = cursor.isNull(1) ? "" : cursor.getString(1);
        Integer priority = cursor.isNull(2) ? 0 : cursor.getInt(2);

        return Quartet.create(uid, stage, priority, condition);
    }

    @NonNull
    private static Pair<String, RuleAction> mapToActionPairs(@NonNull Cursor cursor) {
        return Pair.create(cursor.getString(0), create(cursor));
    }

    @NonNull
    private static RuleVariable mapToRuleVariable(@NonNull Cursor cursor) {
        String name = cursor.getString(0);
        String stage = cursor.getString(1);
        String sourceType = cursor.getString(2);
        String dataElement = cursor.getString(3);
        String attribute = cursor.getString(4);

        // Mime types of the attribute and data element.
        String attributeType = cursor.getString(5);
        String elementType = cursor.getString(6);

        // String representation of value type.
        RuleValueType mimeType;
        if (!isEmpty(attributeType)) {
            mimeType = convertType(attributeType);
        } else if (!isEmpty(elementType)) {
            mimeType = convertType(elementType);
        } else {
            throw new IllegalArgumentException("No ValueType was supplied");
        }

        switch (ProgramRuleVariableSourceType.valueOf(sourceType)) {
            case TEI_ATTRIBUTE:
                return RuleVariableAttribute.create(name, attribute, mimeType);
            case DATAELEMENT_CURRENT_EVENT:
                return RuleVariableCurrentEvent.create(name, dataElement, mimeType);
            case DATAELEMENT_NEWEST_EVENT_PROGRAM:
                return RuleVariableNewestEvent.create(name, dataElement, mimeType);
            case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE:
                return RuleVariableNewestStageEvent.create(name, dataElement, stage, mimeType);
            case DATAELEMENT_PREVIOUS_EVENT:
                return RuleVariablePreviousEvent.create(name, dataElement, mimeType);
            default:
                throw new IllegalArgumentException("Unsupported variable " +
                        "source type: " + sourceType);
        }
    }

    @NonNull
    private static RuleValueType convertType(@NonNull String type) {
        ValueType valueType = ValueType.valueOf(type);
        if (valueType.isInteger() || valueType.isNumeric()) {
            return RuleValueType.NUMERIC;
        } else if (valueType.isBoolean()) {
            return RuleValueType.BOOLEAN;
        } else {
            return RuleValueType.TEXT;
        }
    }

    @NonNull
    private static RuleAction create(@NonNull Cursor cursor) {
        String attribute = cursor.getString(5);
        String dataElement = cursor.getString(6);
        String location = cursor.getString(7);
        String content = cursor.getString(8);
        String data = cursor.getString(9);

        switch (ProgramRuleActionType.valueOf(cursor.getString(3))) {
            case DISPLAYKEYVALUEPAIR:
                return createDisplayKeyValuePairAction(content, data, location);
            case DISPLAYTEXT:
                return createDisplayTextAction(content, data, location);
            case HIDEFIELD:
                return RuleActionHideField.create(content,
                        isEmpty(attribute) ? dataElement : attribute);
            case ASSIGN:
                return RuleActionAssign.create(content, data,
                        isEmpty(attribute) ? dataElement : attribute);
            case SHOWWARNING:
                return RuleActionShowWarning.create(content, data,
                        isEmpty(attribute) ? dataElement : attribute);
            case SHOWERROR:
                return RuleActionShowError.create(content, data,
                        isEmpty(attribute) ? dataElement : attribute);
            default:
                throw new IllegalArgumentException(
                        "Unsupported RuleActionType: " + cursor.getString(3));
        }
    }

    @NonNull
    private static RuleActionDisplayText createDisplayTextAction(@NonNull String content,
            @NonNull String data, @NonNull String location) {
        if (location.equals(RuleActionDisplayText.LOCATION_FEEDBACK_WIDGET)) {
            return RuleActionDisplayText.createForFeedback(content, data);
        } else {
            return RuleActionDisplayText.createForIndicators(content, data);
        }
    }

    @NonNull
    private static RuleActionDisplayKeyValuePair createDisplayKeyValuePairAction(
            @NonNull String content, @NonNull String data, @NonNull String location) {
        if (location.equals(RuleActionDisplayKeyValuePair.LOCATION_FEEDBACK_WIDGET)) {
            return RuleActionDisplayKeyValuePair.createForFeedback(content, data);
        } else {
            return RuleActionDisplayKeyValuePair.createForIndicators(content, data);
        }
    }
}
