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

import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.utils.MockSchedulersProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.HttpURLConnection;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LoginPresenterUnitTests {

    @Mock
    private User user;

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private LoginView loginView;

    private LoginPresenter loginPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        loginPresenter = new LoginPresenterImpl(configurationRepository,
                new MockSchedulersProvider());
    }

    @Test
    public void onAttachShouldNotCallViewIfUserIsNotLoggedIn() {
        when(configurationRepository.isUserLoggedIn()).thenReturn(Observable.just(false));

        loginPresenter.onAttach(loginView);

        verify(configurationRepository).isUserLoggedIn();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void onAttachShouldCallViewIfUserIsLoggedIn() {
        when(configurationRepository.isUserLoggedIn()).thenReturn(Observable.just(true));

        loginPresenter.onAttach(loginView);

        verify(configurationRepository).isUserLoggedIn();
        verify(loginView).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallNavigateToHomeIfSuccess() {
        Response<User> userResponse = Response.success(user);
        when(configurationRepository.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(configurationRepository.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        loginPresenter.onAttach(loginView);

        loginPresenter.validateCredentials("test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(configurationRepository, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIf401() {
        Response<User> userResponse = Response.error(HttpURLConnection.HTTP_UNAUTHORIZED,
                ResponseBody.create(MediaType.parse("Content-Type: application/json"), "{}"));
        when(configurationRepository.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(configurationRepository.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        loginPresenter.onAttach(loginView);

        loginPresenter.validateCredentials("test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(configurationRepository, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).showInvalidCredentialsError();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIf404() {
        Response<User> userResponse = Response.error(HttpURLConnection.HTTP_NOT_FOUND,
                ResponseBody.create(MediaType.parse("Content-Type: application/json"), "{}"));
        when(configurationRepository.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(configurationRepository.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.just(userResponse));
        loginPresenter.onAttach(loginView);

        loginPresenter.validateCredentials("test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(configurationRepository, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).showInvalidServerUrlError();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewIfUnexpectedException() {
        // ToDo
    }

    @Test
    public void validateCredentialsShouldThrowIfViewIsNotAttached() {
        // ToDo
    }

    @Test
    public void validateCredentialsShouldCallViewIfMalformedUrlExceptionIsThrown() {
        // ToDo
    }
}
