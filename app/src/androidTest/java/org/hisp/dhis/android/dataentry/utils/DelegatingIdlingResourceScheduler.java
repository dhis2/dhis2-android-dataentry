/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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