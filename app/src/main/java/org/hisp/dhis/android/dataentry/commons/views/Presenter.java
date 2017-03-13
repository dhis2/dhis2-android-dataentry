package org.hisp.dhis.android.dataentry.commons.views;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

public interface Presenter {
    @UiThread
    void onAttach(@NonNull View view);

    @UiThread
    void onDetach();
}
