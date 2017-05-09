package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryViewArguments;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

class EnrollmentRepositoryImpl implements FormRepository {

    private static final List<String> TITLE_TABLES = Arrays.asList(EnrollmentModel.TABLE, ProgramModel.TABLE);

    private static final String SELECT_TITLE = "SELECT Program.displayName\n" +
            "FROM Enrollment\n" +
            "  JOIN Program ON Enrollment.program = Program.uid\n" +
            "WHERE Enrollment.enrollment = ?";

    private static final String SELECT_ENROLLMENT = "SELECT Enrollment.enrollment\n" +
            "FROM Enrollment\n" +
            "WHERE Enrollment.enrollment = ?";

    @NonNull
    private final BriteDatabase briteDatabase;

    EnrollmentRepositoryImpl(@NonNull BriteDatabase briteDatabase) {
        this.briteDatabase = briteDatabase;
    }

    @NonNull
    @Override
    public Flowable<String> title(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(TITLE_TABLES, SELECT_TITLE, uid)
                .mapToOne(cursor -> cursor.getString(0)));
    }

    @NonNull
    @Override
    public Flowable<List<DataEntryViewArguments>> sections(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(EnrollmentModel.TABLE, SELECT_ENROLLMENT, uid)
                .mapToList(cursor -> DataEntryViewArguments.createForEnrollment(cursor.getString(0))));
    }
}