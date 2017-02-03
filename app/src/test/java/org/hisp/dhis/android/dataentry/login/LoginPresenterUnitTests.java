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

import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.dataentry.Inject;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.server.UserManager;
import org.hisp.dhis.android.dataentry.utils.MockSchedulersProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LoginPresenterUnitTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Inject injectHandler;

    @Mock
    private User user;

    @Mock
    private ConfigurationModel configuration;

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private UserManager userManager;

    @Mock
    private LoginView loginView;

    private LoginPresenter loginPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        loginPresenter = new LoginPresenterImpl(injectHandler,
                new MockSchedulersProvider(), configurationRepository);
    }

    @Test
    public void onAttachShouldNotCallViewIfUserIsNotLoggedIn() {
        when(userManager.isUserLoggedIn()).thenReturn(Observable.just(false));
        when(injectHandler.serverComponent().userManager()).thenReturn(userManager);

        loginPresenter.onAttach(loginView);

        verify(userManager).isUserLoggedIn();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void onAttachShouldCallViewIfUserIsLoggedIn() {
        when(userManager.isUserLoggedIn()).thenReturn(Observable.just(true));
        when(injectHandler.serverComponent().userManager()).thenReturn(userManager);

        loginPresenter.onAttach(loginView);

        verify(userManager).isUserLoggedIn();
        verify(loginView).navigateToHome();
    }

    @Test
    public void onAttachShouldNotCallViewIfError() {
        when(userManager.isUserLoggedIn()).thenReturn(Observable.error(IllegalStateException::new));
        when(injectHandler.serverComponent().userManager()).thenReturn(userManager);

        loginPresenter.onAttach(loginView);

        verify(userManager).isUserLoggedIn();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIfUrlMalformed() {
        Response<User> userResponse = Response.success(user);
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("htt:play.dhis2.org/",
                "test_username", "test_password");

        // no interactions
        verify(configurationRepository, never()).configure(any());
        verify(userManager, never()).logIn(any(), any());
        verify(userManager, never()).isUserLoggedIn();

        // show error message
        verify(loginView).showInvalidServerUrlError();
    }

    @Test
    public void validateCredentialsShouldCallNavigateToHomeIfSuccess() {
        Response<User> userResponse = Response.success(user);
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(injectHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(injectHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIf401() {
        Response<User> userResponse = Response.error(HttpURLConnection.HTTP_UNAUTHORIZED,
                ResponseBody.create(MediaType.parse("Content-Type: application/json"), "{}"));
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(injectHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(injectHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).showInvalidCredentialsError();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIf404() {
        Response<User> userResponse = Response.error(HttpURLConnection.HTTP_NOT_FOUND,
                ResponseBody.create(MediaType.parse("Content-Type: application/json"), "{}"));
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(injectHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(injectHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).showInvalidServerUrlError();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIf400() {
        Response<User> userResponse = Response.error(HttpURLConnection.HTTP_BAD_REQUEST,
                ResponseBody.create(MediaType.parse("Content-Type: application/json"), "{}"));
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(injectHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(injectHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).showUnexpectedError();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIfIOException() {
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.error(IOException::new));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(injectHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(injectHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).showInvalidServerUrlError();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIfMalformedUrlExceptionIsThrown() {
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.error(new MalformedURLException()));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(injectHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(injectHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).showInvalidServerUrlError();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldNotCallViewIfDetached() {
        Response<User> userResponse = Response.success(user);
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        // we don't call attach
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(injectHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView, never()).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(injectHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIfUnexpectedException() {
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.error(Exception::new));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(injectHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(injectHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).showUnexpectedError();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIf500() {
        Response<User> userResponse = Response.error(HttpURLConnection.HTTP_INTERNAL_ERROR,
                ResponseBody.create(MediaType.parse("Content-Type: application/json"), "{}"));
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(injectHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(injectHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(injectHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).showServerError();
        verify(loginView, never()).navigateToHome();
    }
}
