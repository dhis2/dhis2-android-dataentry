package org.hisp.dhis.android.dataentry.commons.ui;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

public interface Presenter<V extends View> {
    @UiThread
    void onAttach(@NonNull V view);

    @UiThread
    void onDetach();
}
