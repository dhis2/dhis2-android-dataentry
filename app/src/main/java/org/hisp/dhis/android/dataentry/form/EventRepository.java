package org.hisp.dhis.android.dataentry.form;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionModel;
import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.RuleEngineContext;
import org.hisp.dhis.rules.RuleExpressionEvaluator;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
class EventRepository implements FormRepository {
    private static final List<String> TITLE_TABLES = Arrays.asList(
            ProgramModel.TABLE, ProgramStageModel.TABLE);

    private static final List<String> SECTION_TABLES = Arrays.asList(
            EventModel.TABLE, ProgramModel.TABLE, ProgramStageModel.TABLE, ProgramStageSectionModel.TABLE);

    private static final String SELECT_PROGRAM = "SELECT program\n" +
            "FROM Event \n" +
            "WHERE uid =?\n" +
            "LIMIT 1;";

    private static final String SELECT_TITLE = "SELECT\n" +
            "  Program.displayName,\n" +
            "  ProgramStage.displayName\n" +
            "FROM Event\n" +
            "  JOIN Program ON Event.program = Program.uid\n" +
            "  JOIN ProgramStage ON Event.programStage = ProgramStage.uid\n" +
            "WHERE Event.uid = ?";

    private static final String SELECT_SECTIONS = "SELECT\n" +
            "  Program.uid AS programUid,\n" +
            "  ProgramStage.uid AS programStageUid,\n" +
            "  ProgramStageSection.uid AS programStageSectionUid,\n" +
            "  ProgramStageSection.displayName AS programStageDisplayName\n" +
            "FROM Event\n" +
            "  JOIN Program ON Event.program = Program.uid\n" +
            "  JOIN ProgramStage ON Event.programStage = ProgramStage.uid\n" +
            "  LEFT OUTER JOIN ProgramStageSection ON ProgramStageSection.programStage = Event.programStage\n" +
            "WHERE Event.uid = ?";

    private static final String SELECT_EVENT_DATE = "SELECT\n" +
            "  Event.eventDate\n" +
            "FROM Event\n" +
            "WHERE Event.uid = ?";

    private static final String SELECT_EVENT_STATUS = "SELECT\n" +
            "  Event.status\n" +
            "FROM Event\n" +
            "WHERE Event.uid = ?";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final Flowable<RuleEngine> cachedRuleEngineFlowable;

    @Nonnull
    private final String eventUid;

    EventRepository(@NonNull BriteDatabase briteDatabase,
            @NonNull RuleExpressionEvaluator evaluator,
            @NonNull RulesRepository rulesRepository,
            @NonNull String eventUid) {
        this.briteDatabase = briteDatabase;
        this.eventUid = eventUid;

        // We don't want to rebuild RuleEngine on each request, since metadata of
        // the event is not changing throughout lifecycle of FormComponent.
        this.cachedRuleEngineFlowable = eventProgram()
                .switchMap(program -> Flowable.zip(rulesRepository.rules(program),
                        rulesRepository.ruleVariables(program), (rules, variables) ->
                                RuleEngineContext.builder(evaluator)
                                        .rules(rules)
                                        .ruleVariables(variables)
                                        .build().toEngineBuilder()
                                        .build()))
                .cacheWithInitialCapacity(1);
    }

    @NonNull
    @Override
    public Flowable<RuleEngine> ruleEngine() {
        return cachedRuleEngineFlowable;
    }

    @NonNull
    @Override
    public Flowable<String> title() {
        return toV2Flowable(briteDatabase
                .createQuery(TITLE_TABLES, SELECT_TITLE, eventUid)
                .mapToOne(cursor -> cursor.getString(0) + " - " + cursor.getString(1)))
                .distinctUntilChanged();
    }

    @NonNull
    @Override
    public Flowable<String> reportDate() {
        return toV2Flowable(briteDatabase
                .createQuery(EventModel.TABLE, SELECT_EVENT_DATE, eventUid)
                .mapToOne(cursor -> cursor.getString(0) == null ? "" : cursor.getString(0)))
                .distinctUntilChanged();
    }

    @NonNull
    @Override
    public Flowable<ReportStatus> reportStatus() {
        return toV2Flowable(briteDatabase
                .createQuery(EventModel.TABLE, SELECT_EVENT_STATUS, eventUid)
                .mapToOne(cursor -> ReportStatus.fromEventStatus(EventStatus.valueOf(cursor.getString(0)))))
                .distinctUntilChanged();
    }

    @NonNull
    @Override
    public Flowable<List<FormSectionViewModel>> sections() {
        return toV2Flowable(briteDatabase
                .createQuery(SECTION_TABLES, SELECT_SECTIONS, eventUid)
                .mapToList(cursor -> mapToFormSectionViewModels(eventUid, cursor))
                .distinctUntilChanged());
    }

    @NonNull
    @Override
    public Consumer<String> storeReportDate() {
        return reportDate -> {
            ContentValues event = new ContentValues();
            event.put(EventModel.Columns.EVENT_DATE, reportDate);
            event.put(EventModel.Columns.STATE, State.TO_UPDATE.name()); // TODO: Check if state is TO_POST
            // TODO: and if so, keep the TO_POST state
            briteDatabase.update(EventModel.TABLE, event, EventModel.Columns.UID + " = ?", eventUid);
        };
    }

    @NonNull
    @Override
    public Consumer<ReportStatus> storeReportStatus() {
        return reportStatus -> {
            ContentValues event = new ContentValues();
            event.put(EventModel.Columns.STATUS, ReportStatus.toEventStatus(reportStatus).name());
            event.put(EventModel.Columns.STATE, State.TO_UPDATE.name()); // TODO: Check if state is TO_POST
            // TODO: and if so, keep the TO_POST state
            briteDatabase.update(EventModel.TABLE, event, EventModel.Columns.UID + " = ?", eventUid);
        };
    }

    @Override
    public Consumer<String> autoGenerateEvent() {
        return s -> {
            // no-op. Events are only auto generated for Enrollments
        };
    }

    @NonNull
    private Flowable<String> eventProgram() {
        return toV2Flowable(briteDatabase.createQuery(EventModel.TABLE, SELECT_PROGRAM, eventUid)
                .mapToOne(cursor -> cursor.getString(0)));
    }

    @NonNull
    private FormSectionViewModel mapToFormSectionViewModels(
            @NonNull String eventUid, @NonNull Cursor cursor) {
        if (cursor.getString(2) == null) {
            // This programstage has no sections
            return FormSectionViewModel.createForProgramStage(
                    eventUid, cursor.getString(1));
        } else {
            // This programstage has sections
            return FormSectionViewModel.createForSection(
                    eventUid, cursor.getString(2), cursor.getString(3));
        }
    }
}