package org.hisp.dhis.android.dataentry.utils;

import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class IdlingSchedulerProvider implements SchedulerProvider {
    private final DelegatingIdlingResourceScheduler computation;
    private final DelegatingIdlingResourceScheduler io;
    private final LegacyDelegatingIdlingResourceScheduler legacyIo;

    public IdlingSchedulerProvider() {
        computation = new DelegatingIdlingResourceScheduler(
                "RxJava computation scheduler", Schedulers.computation());
        io = new DelegatingIdlingResourceScheduler(
                "RxJava I/O scheduler", Schedulers.io());
        legacyIo = new LegacyDelegatingIdlingResourceScheduler(
                "Legacy RXJava I/O scheduler", rx.schedulers.Schedulers.io());

        Espresso.registerIdlingResources(computation, io, legacyIo);
    }

    @NonNull
    @Override
    public Scheduler computation() {
        return computation;
    }

    @NonNull
    @Override
    public rx.Scheduler legacyIo() {
        return legacyIo;
    }

    @NonNull
    @Override
    public Scheduler io() {
        return io;
    }

    @NonNull
    @Override
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
