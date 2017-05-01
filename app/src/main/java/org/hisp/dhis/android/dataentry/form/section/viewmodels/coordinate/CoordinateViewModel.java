package org.hisp.dhis.android.dataentry.form.section.viewmodels.coordinate;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.dataentry.form.section.viewmodels.EditableViewModel;

@AutoValue
public abstract class CoordinateViewModel extends EditableViewModel {

    @NonNull
    public abstract String latitude();

    @NonNull
    public abstract String longitude();

    @NonNull
    public static CoordinateViewModel create(@NonNull String uid, @NonNull String label,
            @NonNull Boolean mandatory, @NonNull String latitude, @NonNull String longitude) {
        return new AutoValue_CoordinateViewModel(uid, label, mandatory, latitude, longitude);
    }
}