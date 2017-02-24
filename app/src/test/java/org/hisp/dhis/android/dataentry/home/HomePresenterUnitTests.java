package org.hisp.dhis.android.dataentry.home;

import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.dataentry.user.UserRepository;
import org.hisp.dhis.android.dataentry.utils.MockSchedulersProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class HomePresenterUnitTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HomeView homeView;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserModel userModel;

    @Captor
    private ArgumentCaptor<String> usernameCaptor;

    @Captor
    private ArgumentCaptor<String> userInitialsCaptor;

    private PublishSubject<UserModel> userSubject;
    private HomePresenter homePresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userSubject = PublishSubject.create();
        homePresenter = new HomePresenterImpl(new MockSchedulersProvider(), userRepository);
        when(userRepository.me()).thenReturn(userSubject);
    }

    @Test
    public void onAttachShouldCallViewWithCorrectUsernameAndInitials() throws Exception {
        when(userModel.firstName()).thenReturn("John");
        when(userModel.surname()).thenReturn("Watson");

        homePresenter.onAttach(homeView);
        userSubject.onNext(userModel);

        verify(homeView.showUsername()).accept(usernameCaptor.capture());
        verify(homeView.showUserInitials()).accept(userInitialsCaptor.capture());
        verify(homeView, never()).showUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("John Watson");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("JW");
    }

    @Test
    public void onAttachShouldNotAppendSpaceToUsername() throws Exception {
        when(userModel.firstName()).thenReturn(null);
        when(userModel.surname()).thenReturn("Watson");

        homePresenter.onAttach(homeView);
        userSubject.onNext(userModel);

        verify(homeView.showUsername()).accept(usernameCaptor.capture());
        verify(homeView.showUserInitials()).accept(userInitialsCaptor.capture());
        verify(homeView, never()).showUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("Watson");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("W");
    }

    @Test
    public void onAttachShouldCapitalizeInitials() throws Exception {
        when(userModel.firstName()).thenReturn("john");
        when(userModel.surname()).thenReturn("watson");

        homePresenter.onAttach(homeView);
        userSubject.onNext(userModel);

        verify(homeView.showUsername()).accept(usernameCaptor.capture());
        verify(homeView.showUserInitials()).accept(userInitialsCaptor.capture());
        verify(homeView, never()).showUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("john watson");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("JW");
    }

    @Test
    public void onAttachShouldNotFailIfArgumentsAreNull() throws Exception {
        when(userModel.firstName()).thenReturn(null);
        when(userModel.surname()).thenReturn(null);

        homePresenter.onAttach(homeView);
        userSubject.onNext(userModel);

        verify(homeView.showUsername()).accept(usernameCaptor.capture());
        verify(homeView.showUserInitials()).accept(userInitialsCaptor.capture());
        verify(homeView, never()).showUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("");
    }

    @Test
    public void onAttachShouldNotFailIfArgumentsAreEmpty() throws Exception {
        when(userModel.firstName()).thenReturn("");
        when(userModel.surname()).thenReturn("");
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);
        userSubject.onNext(userModel);

        verify(homeView.showUsername()).accept(usernameCaptor.capture());
        verify(homeView.showUserInitials()).accept(userInitialsCaptor.capture());
        verify(homeView, never()).showUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("");
    }

    @Test
    public void onDetachShouldNotInteractWithView() {
        homePresenter.onAttach(homeView);
        userSubject.onNext(userModel);

        verify(userRepository).me();
        verify(homeView).showUsername();
        verify(homeView).showUserInitials();

        homePresenter.onDetach();
        verifyNoMoreInteractions(userRepository, homeView);
    }

    @Test
    public void onDetachShouldUnsubscribeFromRepository() {
        homePresenter.onAttach(homeView);
        assertThat(userSubject.hasObservers()).isTrue();

        homePresenter.onDetach();
        assertThat(userSubject.hasObservers()).isFalse();
    }
}
