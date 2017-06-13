package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
abstract class CardViewActionModel {

    @NonNull
    abstract public Boolean click();

    @NonNull
    abstract public Boolean clear();

    @NonNull
    public static CardViewActionModel createClear() {
        return new AutoValue_CardViewActionModel(false, true);
    }

    @NonNull
    public static CardViewActionModel createClick() {
        return new AutoValue_CardViewActionModel(true, false);
    }

}
