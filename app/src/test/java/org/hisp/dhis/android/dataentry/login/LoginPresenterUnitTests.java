package org.hisp.dhis.android.dataentry.login;

import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.dataentry.Components;
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
    private Components componentsHandler;

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

        loginPresenter = new LoginPresenterImpl(componentsHandler,
                new MockSchedulersProvider(), configurationRepository);
    }

    @Test
    public void onAttachShouldNotCallViewIfUserIsNotLoggedIn() {
        when(userManager.isUserLoggedIn()).thenReturn(Observable.just(false));
        when(componentsHandler.serverComponent().userManager()).thenReturn(userManager);

        loginPresenter.onAttach(loginView);

        verify(userManager).isUserLoggedIn();
        verify(componentsHandler, never()).createUserComponent();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void onAttachShouldCallViewIfUserIsLoggedIn() {
        when(userManager.isUserLoggedIn()).thenReturn(Observable.just(true));
        when(componentsHandler.serverComponent().userManager()).thenReturn(userManager);

        loginPresenter.onAttach(loginView);

        verify(userManager).isUserLoggedIn();
        verify(componentsHandler).createUserComponent();
        verify(loginView).navigateToHome();
    }

    @Test
    public void onAttachShouldNotCallViewIfError() {
        when(userManager.isUserLoggedIn()).thenReturn(Observable.error(IllegalStateException::new));
        when(componentsHandler.serverComponent().userManager()).thenReturn(userManager);

        loginPresenter.onAttach(loginView);

        verify(userManager).isUserLoggedIn();
        verify(componentsHandler, never()).createUserComponent();
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
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("htt:play.dhis2.org/",
                "test_username", "test_password");

        // no interactions
        verify(configurationRepository, never()).configure(any());
        verify(userManager, never()).logIn(any(), any());
        verify(userManager, never()).isUserLoggedIn();
        verify(componentsHandler, never()).createUserComponent();

        // show error message
        verify(loginView).renderInvalidServerUrlError();
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
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(componentsHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(componentsHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(componentsHandler).createUserComponent();
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
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(componentsHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(componentsHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).renderInvalidCredentialsError();
        verify(componentsHandler, never()).createUserComponent();
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
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(componentsHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(componentsHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).renderInvalidServerUrlError();
        verify(componentsHandler, never()).createUserComponent();
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
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(componentsHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(componentsHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).renderUnexpectedError();
        verify(componentsHandler, never()).createUserComponent();
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
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(componentsHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(componentsHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).renderInvalidServerUrlError();
        verify(componentsHandler, never()).createUserComponent();
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
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(componentsHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(componentsHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).renderInvalidServerUrlError();
        verify(componentsHandler, never()).createUserComponent();
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
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        // we don't call attach
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(componentsHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView, never()).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(componentsHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView, never()).navigateToHome();
        verify(componentsHandler, never()).createUserComponent();
    }

    @Test
    public void validateCredentialsShouldCallViewIfUnexpectedException() {
        when(userManager.isUserLoggedIn())
                .thenReturn(Observable.just(false));
        when(userManager.logIn("test_user_name", "test_password"))
                .thenReturn(Observable.error(Exception::new));
        when(configurationRepository.configure(any()))
                .thenReturn(Observable.just(configuration));
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(componentsHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(componentsHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).renderUnexpectedError();
        verify(componentsHandler, never()).createUserComponent();
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
        when(componentsHandler.createServerComponent(configuration).userManager())
                .thenReturn(userManager);

        loginPresenter.onAttach(loginView);
        loginPresenter.validateCredentials("https://play.dhis2.org/demo/",
                "test_user_name", "test_password");

        InOrder mocksInOrder = inOrder(componentsHandler, configurationRepository, userManager, loginView);
        mocksInOrder.verify(loginView).showProgress();
        mocksInOrder.verify(configurationRepository).configure(any());
        mocksInOrder.verify(componentsHandler).createServerComponent(configuration);
        mocksInOrder.verify(userManager).logIn("test_user_name", "test_password");
        mocksInOrder.verify(loginView).hideProgress();
        mocksInOrder.verify(loginView).renderServerError();
        verify(componentsHandler, never()).createUserComponent();
        verify(loginView, never()).navigateToHome();
    }

    @Test
    public void validateCredentialsShouldCallViewOnTimeOut() {
        // ToDo
    }
}
