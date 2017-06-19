package org.hisp.dhis.android.dataentry.commons.schedulers;

import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action0;

class LegacyDelegatingIdlingResourceScheduler extends rx.Scheduler implements IdlingResource {
    private final rx.Scheduler scheduler;
    private final CountingIdlingResource countingIdlingResource;

    LegacyDelegatingIdlingResourceScheduler(@NonNull String name, @NonNull rx.Scheduler scheduler) {
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
        public Subscription schedule(Action0 action) {
            if (recursive) {
                return worker.schedule(action);
            } else {
                return worker.schedule(decorateAction(action));
            }
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (recursive) {
                return worker.schedule(action, delayTime, unit);
            } else {
                return worker.schedule(decorateAction(action), delayTime, unit);
            }
        }

        @Override
        public Subscription schedulePeriodically(Action0 action, long initialDelay, long period, TimeUnit unit) {
            recursive = true;
            return worker.schedulePeriodically(decorateAction(action),
                    initialDelay, period, unit);
        }

        @Override
        public void unsubscribe() {
            worker.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return worker.isUnsubscribed();
        }

        private Action0 decorateAction(Action0 action) {
            return () -> {
                countingIdlingResource.increment();
                try {
                    action.call();
                } finally {
                    countingIdlingResource.decrement();
                }
            };
        }
    }
}