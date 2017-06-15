package org.hisp.dhis.android.dataentry.form;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import static org.hisp.dhis.android.dataentry.form.FormViewArguments.Type.ENROLLMENT;
import static org.hisp.dhis.android.dataentry.form.FormViewArguments.Type.EVENT;

@AutoValue
public abstract class FormViewArguments implements Parcelable {

    // this is the uid for an enrollment or an event
    @NonNull
    abstract String uid();

    @NonNull
    abstract Type type();

    @NonNull
    public static FormViewArguments createForEnrollment(@NonNull String enrollmentUid) {
        return new AutoValue_FormViewArguments(enrollmentUid, ENROLLMENT);
    }

    @NonNull
    public static FormViewArguments createForEvent(@NonNull String eventUid) {
        return new AutoValue_FormViewArguments(eventUid, EVENT);
    }

    enum Type {
        ENROLLMENT, EVENT
    }
}