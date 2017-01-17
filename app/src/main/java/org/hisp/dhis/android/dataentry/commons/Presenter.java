package org.hisp.dhis.android.dataentry.commons;

import android.support.annotation.NonNull;

public interface Presenter {
    void onAttach(@NonNull View view);

    void onDetach();
}
