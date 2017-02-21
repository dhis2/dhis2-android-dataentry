package org.hisp.dhis.android.dataentry.utils;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public final class ImmediateScheduler {
    private ImmediateScheduler() {
        // no instances
    }

    public static Scheduler create() {
        return Schedulers.from((command) -> command.run());
    }
}
