package org.hisp.dhis.android.dataentry.main;

import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.dataentry.commons.schedulers.MockSchedulersProvider;
import org.hisp.dhis.android.dataentry.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MainPresenterUnitTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MainView mainView;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserModel userModel;

    private MainPresenter mainPresenter;

    @Captor
    private ArgumentCaptor<String> usernameCaptor;

    @Captor
    private ArgumentCaptor<String> userInitialsCaptor;

    private PublishProcessor<UserModel> userSubject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userSubject = PublishProcessor.create();
        when(userRepository.me()).thenReturn(userSubject);

        mainPresenter = new MainPresenterImpl(null, new MockSchedulersProvider(), userRepository);
    }

    @Test
    public void onAttachShouldCallViewWithCorrectUsernameAndInitials() throws Exception {
        when(userModel.firstName()).thenReturn("John");
        when(userModel.surname()).thenReturn("Watson");

        mainPresenter.onAttach(mainView);
        userSubject.onNext(userModel);

        verify(mainView.renderUsername()).accept(usernameCaptor.capture());
        verify(mainView.renderUserInitials()).accept(userInitialsCaptor.capture());
        verify(mainView, never()).renderUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("John Watson");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("JW");
    }

    @Test
    public void onAttachShouldNotAppendSpaceToUsername() throws Exception {
        when(userModel.firstName()).thenReturn(null);
        when(userModel.surname()).thenReturn("Watson");

        mainPresenter.onAttach(mainView);
        userSubject.onNext(userModel);

        verify(mainView.renderUsername()).accept(usernameCaptor.capture());
        verify(mainView.renderUserInitials()).accept(userInitialsCaptor.capture());
        verify(mainView, never()).renderUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("Watson");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("W");
    }

    @Test
    public void onAttachShouldCapitalizeInitials() throws Exception {
        when(userModel.firstName()).thenReturn("john");
        when(userModel.surname()).thenReturn("watson");

        mainPresenter.onAttach(mainView);
        userSubject.onNext(userModel);

        verify(mainView.renderUsername()).accept(usernameCaptor.capture());
        verify(mainView.renderUserInitials()).accept(userInitialsCaptor.capture());
        verify(mainView, never()).renderUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("john watson");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("JW");
    }

    @Test
    public void onAttachShouldNotFailIfArgumentsAreNull() throws Exception {
        when(userModel.firstName()).thenReturn(null);
        when(userModel.surname()).thenReturn(null);

        mainPresenter.onAttach(mainView);
        userSubject.onNext(userModel);

        verify(mainView.renderUsername()).accept(usernameCaptor.capture());
        verify(mainView.renderUserInitials()).accept(userInitialsCaptor.capture());
        verify(mainView, never()).renderUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("");
    }

    @Test
    public void onAttachShouldNotFailIfArgumentsAreEmpty() throws Exception {
        when(userModel.firstName()).thenReturn("");
        when(userModel.surname()).thenReturn("");
        when(userRepository.me()).thenReturn(Flowable.just(userModel));

        mainPresenter.onAttach(mainView);
        userSubject.onNext(userModel);

        verify(mainView.renderUsername()).accept(usernameCaptor.capture());
        verify(mainView.renderUserInitials()).accept(userInitialsCaptor.capture());
        verify(mainView, never()).renderUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("");
    }

    @Test
    public void onDetachShouldNotInteractWithView() {
        userSubject.onNext(userModel);
        when(userRepository.me()).thenReturn(Flowable.just(userModel));

        mainPresenter.onAttach(mainView);

        verify(userRepository).me();
        verify(mainView).renderUsername();
        verify(mainView).renderUserInitials();

        mainPresenter.onDetach();
        verifyNoMoreInteractions(userRepository, mainView);
    }

    @Test
    public void onDetachShouldUnsubscribeFromRepository() {
        mainPresenter.onAttach(mainView);
        assertThat(userSubject.hasSubscribers()).isTrue();

        mainPresenter.onDetach();
        assertThat(userSubject.hasSubscribers()).isFalse();
    }
}
