package org.hisp.dhis.android.dataentry.form;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.program.ProgramModel;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
class EnrollmentFormRepository implements FormRepository {
    private static final List<String> TITLE_TABLES = Arrays.asList(
            EnrollmentModel.TABLE, ProgramModel.TABLE);

    private static final String SELECT_TITLE = "SELECT Program.displayName\n" +
            "FROM Enrollment\n" +
            "  JOIN Program ON Enrollment.program = Program.uid\n" +
            "WHERE Enrollment.uid = ?";

    private static final String SELECT_ENROLLMENT_UID = "SELECT Enrollment.uid\n" +
            "FROM Enrollment\n" +
            "WHERE Enrollment.uid = ?";

    private static final String SELECT_ENROLLMENT_STATUS = "SELECT Enrollment.status\n" +
            "FROM Enrollment\n" +
            "WHERE Enrollment.uid = ?";

    private static final String SELECT_ENROLLMENT_DATE = "SELECT Enrollment.enrollmentDate\n" +
            "FROM Enrollment\n" +
            "WHERE Enrollment.uid = ?";

    @NonNull
    private final BriteDatabase briteDatabase;

    EnrollmentFormRepository(@NonNull BriteDatabase briteDatabase) {
        this.briteDatabase = briteDatabase;
    }

    @NonNull
    @Override
    public Flowable<String> title(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(TITLE_TABLES, SELECT_TITLE, uid)
                .mapToOne(cursor -> cursor.getString(0)))
                .distinctUntilChanged();
    }

    @NonNull
    @Override
    public Flowable<String> reportDate(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(EnrollmentModel.TABLE, SELECT_ENROLLMENT_DATE, uid)
                .mapToOne(cursor -> cursor.getString(0) == null ? "" : cursor.getString(0)))
                .distinctUntilChanged();
    }

    @NonNull
    @Override
    public Flowable<ReportStatus> reportStatus(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(EnrollmentModel.TABLE, SELECT_ENROLLMENT_STATUS, uid)
                .mapToOne(cursor ->
                        ReportStatus.fromEnrollmentStatus(EnrollmentStatus.valueOf(cursor.getString(0)))))
                .distinctUntilChanged();
    }

    @NonNull
    @Override
    public Flowable<List<FormSectionViewModel>> sections(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(EnrollmentModel.TABLE, SELECT_ENROLLMENT_UID, uid)
                .mapToList(cursor -> FormSectionViewModel
                        .createForEnrollment(cursor.getString(0))));
    }

    @NonNull
    @Override
    public Consumer<String> storeReportDate(@NonNull String uid) {
        return reportDate -> {
            ContentValues enrollment = new ContentValues();
            enrollment.put(EnrollmentModel.Columns.DATE_OF_ENROLLMENT, reportDate);
            enrollment.put(EnrollmentModel.Columns.STATE, State.TO_UPDATE.name()); // TODO: Check if state is TO_POST
            // TODO: and if so, keep the TO_POST state
            briteDatabase.update(EnrollmentModel.TABLE, enrollment,
                    EnrollmentModel.Columns.UID + " = ?", uid);
        };
    }

    @NonNull
    @Override
    public Consumer<ReportStatus> storeReportStatus(@NonNull String uid) {
        return reportStatus -> {
            ContentValues enrollment = new ContentValues();
            enrollment.put(EnrollmentModel.Columns.ENROLLMENT_STATUS,
                    ReportStatus.toEnrollmentStatus(reportStatus).name());
            enrollment.put(EnrollmentModel.Columns.STATE, State.TO_UPDATE.name()); // TODO: Check if state is TO_POST
            // TODO: and if so, keep the TO_POST state
            briteDatabase.update(EnrollmentModel.TABLE, enrollment, EnrollmentModel.Columns.UID + " = ?", uid);
        };
    }
}