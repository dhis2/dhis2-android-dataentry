/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.dataentry.main;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.dataentry.commons.View;
import org.hisp.dhis.android.dataentry.user.UserRepository;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;
import timber.log.Timber;

import static org.hisp.dhis.android.dataentry.utils.Preconditions.isNull;
import static org.hisp.dhis.android.dataentry.utils.StringUtils.isEmpty;

class MainPresenterImpl implements MainPresenter {
    private final SchedulerProvider schedulerProvider;
    private final UserRepository userRepository;
    private final CompositeDisposable compositeDisposable;

    MainPresenterImpl(@NonNull SchedulerProvider schedulerProvider,
                      @NonNull UserRepository userRepository) {
        this.schedulerProvider = schedulerProvider;
        this.userRepository = userRepository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        isNull(view, "MainView must not be null");

        if (view instanceof MainView) {
            MainView mainView = (MainView) view;

            ConnectableObservable<UserModel> userObservable = userRepository.me()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .publish();

            compositeDisposable.add(userObservable
                    .map(this::username)
                    .subscribe(mainView.showUsername(), Timber::e));

            compositeDisposable.add(userObservable
                    .map(this::userInitials)
                    .subscribe(mainView.showUserInitials(), Timber::e));

            compositeDisposable.add(userObservable.connect());
        }
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }

    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
    private String username(@NonNull UserModel user) {
        String username = "";
        if (!isEmpty(user.firstName())) {
            username += user.firstName();
        }

        if (!isEmpty(user.surname())) {
            if (!username.isEmpty()) {
                username += " ";
            }

            username += user.surname();
        }

        return username;
    }

    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
    private String userInitials(@NonNull UserModel user) {
        String initials = "";
        if (!isEmpty(user.firstName())) {
            initials += String.valueOf(user.firstName().charAt(0));
        }

        if (!isEmpty(user.surname())) {
            initials += String.valueOf(user.surname().charAt(0));
        }
        return initials.toUpperCase(Locale.US);
    }
}
