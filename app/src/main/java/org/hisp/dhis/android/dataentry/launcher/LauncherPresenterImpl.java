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

package org.hisp.dhis.android.dataentry.launcher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.dataentry.commons.View;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

class LauncherPresenterImpl implements LauncherPresenter {

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    @Nullable
    private final ConfigurationRepository configurationRepository;

    @Nullable
    private LauncherView launcherView;

    public LauncherPresenterImpl(@NonNull SchedulerProvider schedulerProvider,
            @Nullable ConfigurationRepository configurationRepository) {
        this.schedulerProvider = schedulerProvider;
        this.configurationRepository = configurationRepository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void isUserLoggedIn() {
        /* in case if user repository is null, it means that d2 has not been configured */
        if (configurationRepository == null) {
            navigateToLoginView();
            return;
        }

        compositeDisposable.add(configurationRepository.isUserLoggedIn()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe((isUserLoggedIn) -> {
                    if (isUserLoggedIn) {
                        navigateToHomeView();
                    } else {
                        navigateToLoginView();
                    }
                }, Timber::e));
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof LauncherView) {
            launcherView = (LauncherView) view;
        }
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
        launcherView = null;
    }

    private void navigateToHomeView() {
        if (launcherView != null) {
            launcherView.navigateToHomeView();
        }
    }

    private void navigateToLoginView() {
        if (launcherView != null) {
            launcherView.navigateToLoginView();
        }
    }
}
