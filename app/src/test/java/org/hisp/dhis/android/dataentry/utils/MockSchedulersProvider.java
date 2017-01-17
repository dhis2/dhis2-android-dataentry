package org.hisp.dhis.android.dataentry.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public final class MockSchedulersProvider implements SchedulerProvider {
    private final Executor executor;

    public MockSchedulersProvider() {
        // synchronous executor
        this.executor = Runnable::run;
    }

    @NonNull
    @Override
    public Scheduler computation() {
        return Schedulers.from(executor);
    }

    @NonNull
    @Override
    public Scheduler io() {
        return Schedulers.from(executor);
    }

    @NonNull
    @Override
    public Scheduler ui() {
        return Schedulers.from(executor);
    }
}
