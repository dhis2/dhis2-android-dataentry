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

package org.hisp.dhis.android.dataentry.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.dataentry.Inject;
import org.hisp.dhis.android.dataentry.commons.View;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.server.UserManager;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import java.io.IOException;
import java.net.HttpURLConnection;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.HttpUrl;
import retrofit2.Response;
import timber.log.Timber;

public class LoginPresenterImpl implements LoginPresenter {

    @NonNull
    private final Inject injectHandler;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final ConfigurationRepository configurationRepository;

    @NonNull
    private final CompositeDisposable disposable;

    @Nullable
    private LoginView loginView;

    public LoginPresenterImpl(@NonNull Inject inject,
            @NonNull SchedulerProvider schedulerProvider,
            @NonNull ConfigurationRepository configurationRepository) {
        this.injectHandler = inject;
        this.schedulerProvider = schedulerProvider;
        this.configurationRepository = configurationRepository;
        this.disposable = new CompositeDisposable();
    }

    @UiThread
    @Override
    public void validateCredentials(@NonNull String server, @NonNull String user, @NonNull String pass) {
        HttpUrl baseUrl = HttpUrl.parse(canonizeUrl(server));
        if (baseUrl == null) {
            showInvalidServerUrlError();
            return;
        }

        showProgress();

        disposable.add(configurationRepository.configure(baseUrl)
                .map((config) -> injectHandler.createServerComponent(config).userManager())
                .switchMap((userManager) -> userManager.logIn(user, pass))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::handleResponse, this::handleError));
    }

    @UiThread
    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof LoginView) {
            loginView = (LoginView) view;
        }

        UserManager userManager = null;
        if (injectHandler.serverComponent() != null) {
            userManager = injectHandler.serverComponent().userManager();
        }

        if (userManager != null) {
            disposable.add(userManager.isUserLoggedIn()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe((isUserLoggedIn) -> {
                        if (isUserLoggedIn) {
                            navigateToHome();
                        }
                    }));
        }
    }

    @UiThread
    @Override
    public void onDetach() {
        loginView = null;
        disposable.clear();
    }

    private void handleResponse(@NonNull Response<User> userResponse) {
        Timber.d("Authentication response url: %s", userResponse.raw().request().url().toString());
        Timber.d("Authentication response code: %s", userResponse.code());
        if (userResponse.isSuccessful()) {
            navigateToHome();
        } else if (userResponse.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            showInvalidCredentialsError();
        } else if (userResponse.code() == HttpURLConnection.HTTP_NOT_FOUND) {
            showInvalidServerUrlError();
        } else if (userResponse.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
            showUnexpectedError();
        } else if (userResponse.code() >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
            showServerError();
        }
    }

    private void showProgress() {
        if (loginView != null) {
            loginView.showProgress();
        }
    }

    private void hideProgress() {
        if (loginView != null) {
            loginView.hideProgress();
        }
    }

    private void navigateToHome() {
        if (loginView != null) {
            loginView.navigateToHome();
        }
    }

    private void showInvalidCredentialsError() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.showInvalidCredentialsError();
        }
    }

    private void showInvalidServerUrlError() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.showInvalidServerUrlError();
        }
    }

    private void showUnexpectedError() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.showUnexpectedError();
        }
    }

    private void showServerError() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.showServerError();
        }
    }

    private void handleError(@NonNull Throwable throwable) {
        Timber.e(throwable);

        if (throwable instanceof IOException) {
            showInvalidServerUrlError();
        } else {
            showUnexpectedError();
        }
    }

    private String canonizeUrl(@NonNull String serverUrl) {
        return serverUrl.endsWith("/") ? serverUrl : serverUrl + "/";
    }
}
