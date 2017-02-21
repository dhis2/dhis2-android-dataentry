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

    private HomePresenter homePresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        homePresenter = new HomePresenterImpl(new MockSchedulersProvider(), userRepository);
    }

    @Test
    public void onAttachShouldCallViewWithCorrectUsernameAndInitials() throws Exception {
        when(userModel.firstName()).thenReturn("John");
        when(userModel.surname()).thenReturn("Watson");
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

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
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

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
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

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
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

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

        verify(homeView.showUsername()).accept(usernameCaptor.capture());
        verify(homeView.showUserInitials()).accept(userInitialsCaptor.capture());
        verify(homeView, never()).showUserInfo();

        assertThat(usernameCaptor.getValue()).isEqualTo("");
        assertThat(userInitialsCaptor.getValue()).isEqualTo("");
    }

    @Test
    public void onDetachShouldNotInteractWithView() {
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

        verify(userRepository).me();
        verify(homeView).showUsername();
        verify(homeView).showUserInitials();

        homePresenter.onDetach();
        verifyNoMoreInteractions(userRepository, homeView);
    }
}
