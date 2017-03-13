package org.hisp.dhis.android.dataentry.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.dataentry.Components;
import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.server.UserManager;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import java.io.IOException;
import java.net.HttpURLConnection;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.HttpUrl;
import retrofit2.Response;
import timber.log.Timber;

class LoginPresenterImpl implements LoginPresenter {

    @NonNull
    private final Components componentsHandler;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final ConfigurationRepository configurationRepository;

    @NonNull
    private final CompositeDisposable disposable;

    @Nullable
    private LoginView loginView;

    LoginPresenterImpl(@NonNull Components components,
            @NonNull SchedulerProvider schedulerProvider,
            @NonNull ConfigurationRepository configurationRepository) {
        this.componentsHandler = components;
        this.schedulerProvider = schedulerProvider;
        this.configurationRepository = configurationRepository;
        this.disposable = new CompositeDisposable();
    }

    @UiThread
    @Override
    public void validateCredentials(@NonNull String server,
            @NonNull String user, @NonNull String password) {
        HttpUrl baseUrl = HttpUrl.parse(canonizeUrl(server));
        if (baseUrl == null) {
            showInvalidServerUrlError();
            return;
        }

        showProgress();

        disposable.add(configurationRepository.configure(baseUrl)
                .map((config) -> componentsHandler.createServerComponent(config).userManager())
                .switchMap((userManager) -> userManager.logIn(user, password))
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
        if (componentsHandler.serverComponent() != null) {
            userManager = componentsHandler.serverComponent().userManager();
        }

        if (userManager != null) {
            disposable.add(userManager.isUserLoggedIn()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe((isUserLoggedIn) -> {
                        if (isUserLoggedIn) {
                            navigateToHome();
                        }
                    }, Timber::e));
        }
    }

    @UiThread
    @Override
    public void onDetach() {
        // in order not to leak reference to the view (activity)
        loginView = null;
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

    private void navigateToHome() {
        if (loginView != null) {
            componentsHandler.createUserComponent();
            loginView.navigateToHome();
        }
    }

    private void showInvalidCredentialsError() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.renderInvalidCredentialsError();
        }
    }

    private void showInvalidServerUrlError() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.renderInvalidServerUrlError();
        }
    }

    private void showUnexpectedError() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.renderUnexpectedError();
        }
    }

    private void showServerError() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.renderServerError();
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
