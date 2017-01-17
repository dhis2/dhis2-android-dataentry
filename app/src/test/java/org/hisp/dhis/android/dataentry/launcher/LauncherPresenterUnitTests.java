package org.hisp.dhis.android.dataentry.launcher;

import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.dataentry.commons.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.user.UserRepository;
import org.hisp.dhis.android.dataentry.utils.MockSchedulersProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Observable;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LauncherPresenterUnitTests {

    @Mock
    private LauncherView launcherView;

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private UserRepository userRepository;

    private ConfigurationModel configuration;
    private LauncherPresenter launcherPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        configuration = ConfigurationModel.builder()
                .serverUrl("test_server_url")
                .build();

        launcherPresenter = new LauncherPresenterImpl(new MockSchedulersProvider(),
                configurationRepository, userRepository);
        launcherPresenter.onAttach(launcherView);
    }

    @Test
    public void isUserLoggedIn_shouldCallNavigateToLoginView_ifConfigurationIsNotPresent() {
        // server is not configured
        ConfigurationModel configuration = ConfigurationModel.builder()
                .serverUrl("").build();

        when(configurationRepository.configuration()).thenReturn(Observable.just(configuration));
        when(userRepository.isUserLoggedIn()).thenReturn(Observable.just(false));

        launcherPresenter.isUserLoggedIn();

        verify(launcherView).navigateToLoginView();
    }

    @Test
    public void isUserLoggedIn_shouldCallNavigateToLoginView_ifUserNotPresent() {
        when(configurationRepository.configuration()).thenReturn(Observable.just(configuration));
        when(userRepository.isUserLoggedIn()).thenReturn(Observable.just(false));

        launcherPresenter.isUserLoggedIn();

        verify(launcherView).navigateToLoginView();
    }

    @Test
    public void isUserLoggedIn_shouldCallNavigateToHomeView_ifUserPresent() {
        when(configurationRepository.configuration()).thenReturn(Observable.just(configuration));
        when(userRepository.isUserLoggedIn()).thenReturn(Observable.just(true));

        launcherPresenter.isUserLoggedIn();

        verify(launcherView).navigateToHomeView();
    }

    @Test
    public void isUserLoggedIn_shouldCallNavigationToHomeView_ifUserRepositoryIsNull() {
        LauncherPresenter launcherPresenter = new LauncherPresenterImpl(
                new MockSchedulersProvider(), configurationRepository, null);
        launcherPresenter.onAttach(launcherView);

        when(configurationRepository.configuration()).thenReturn(Observable.just(configuration));

        launcherPresenter.isUserLoggedIn();

        verify(launcherView).navigateToLoginView();
    }

    @Test
    public void onAttach_shouldNotInvokeView() {
        launcherPresenter.onAttach(launcherView);

        verify(launcherView, never()).navigateToLoginView();
        verify(launcherView, never()).navigateToHomeView();
    }

    @Test
    public void viewMethods_shouldNotBeCalled_afterDetach() {
        when(configurationRepository.configuration()).thenReturn(Observable.just(configuration));
        when(userRepository.isUserLoggedIn()).thenReturn(Observable.just(true));

        launcherPresenter.onDetach();
        launcherPresenter.isUserLoggedIn();

        verify(launcherView, never()).navigateToLoginView();
        verify(launcherView, never()).navigateToHomeView();
    }
}
