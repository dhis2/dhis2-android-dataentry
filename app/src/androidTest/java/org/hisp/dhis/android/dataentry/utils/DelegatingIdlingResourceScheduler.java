package org.hisp.dhis.android.dataentry.utils;

import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

public class DelegatingIdlingResourceScheduler extends Scheduler implements IdlingResource {
    private final Scheduler scheduler;
    private final CountingIdlingResource countingIdlingResource;

    public DelegatingIdlingResourceScheduler(@NonNull String name, @NonNull Scheduler scheduler) {
        this.scheduler = scheduler;
        this.countingIdlingResource = new CountingIdlingResource(name, true);
    }

    @Override
    public Worker createWorker() {
        return new IdlingWorker(scheduler.createWorker());
    }

    @Override
    public String getName() {
        return countingIdlingResource.getName();
    }

    @Override
    public boolean isIdleNow() {
        return countingIdlingResource.isIdleNow();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        countingIdlingResource.registerIdleTransitionCallback(callback);
    }

    private final class IdlingWorker extends Worker {
        private final Worker worker;
        private boolean recursive;

        IdlingWorker(Worker worker) {
            this.worker = worker;
        }

        @Override
        public Disposable schedule(Runnable run) {
            return recursive ?
                    worker.schedule(run) :
                    worker.schedule(decorateAction(run));
        }

        @Override
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            return recursive ?
                    worker.schedule(run, delay, unit) :
                    worker.schedule(decorateAction(run), delay, unit);
        }

        @Override
        public Disposable schedulePeriodically(Runnable run,
                long initialDelay, long period, TimeUnit unit) {
            recursive = true;
            return worker.schedulePeriodically(decorateAction(run),
                    initialDelay, period, unit);
        }

        @Override
        public void dispose() {
            worker.dispose();
        }

        @Override
        public boolean isDisposed() {
            return worker.isDisposed();
        }

        private Runnable decorateAction(Runnable runnable) {
            return () -> {
                countingIdlingResource.increment();
                try {
                    runnable.run();
                } finally {
                    countingIdlingResource.decrement();
                }
            };
        }
    }
}