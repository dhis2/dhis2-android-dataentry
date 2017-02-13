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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class HomePresenterUnitTests {

    @Mock
    private HomeView homeView;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserModel userModel;

    private HomePresenter homePresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        homePresenter = new HomePresenterImpl(new MockSchedulersProvider(), userRepository);
    }

    @Test
    public void onAttachShouldCallViewWithCorrectUsernameAndInitials() {
        when(userModel.firstName()).thenReturn("John");
        when(userModel.surname()).thenReturn("Watson");
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

        verify(homeView).showUsername("John Watson");
        verify(homeView).showUserInitials("JW");

        verify(homeView, never()).showUserInfo(any());
    }

    @Test
    public void onAttachShouldNotAppendSpaceToUsername() {
        when(userModel.firstName()).thenReturn(null);
        when(userModel.surname()).thenReturn("Watson");
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

        verify(homeView).showUsername("Watson");
        verify(homeView).showUserInitials("W");

        verify(homeView, never()).showUserInfo(any());
    }

    @Test
    public void onAttachShouldCapitalizeInitials() {
        when(userModel.firstName()).thenReturn("john");
        when(userModel.surname()).thenReturn("watson");
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

        verify(homeView).showUsername("john watson");
        verify(homeView).showUserInitials("JW");

        verify(homeView, never()).showUserInfo(any());
    }

    @Test
    public void onAttachShouldNotFailIfArgumentsAreNull() {
        when(userModel.firstName()).thenReturn(null);
        when(userModel.surname()).thenReturn(null);
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

        verify(homeView).showUsername("");
        verify(homeView).showUserInitials("");

        verify(homeView, never()).showUserInfo(any());
    }

    @Test
    public void onAttachShouldNotFailIfArgumentsAreEmpty() {
        when(userModel.firstName()).thenReturn("");
        when(userModel.surname()).thenReturn("");
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

        verify(homeView).showUsername("");
        verify(homeView).showUserInitials("");

        verify(homeView, never()).showUserInfo(any());
    }

    @Test
    public void onDetachShouldNotInteractWithView() {
        when(userRepository.me()).thenReturn(Observable.just(userModel));

        homePresenter.onAttach(homeView);

        verify(userRepository).me();
        verify(homeView).showUsername(any());
        verify(homeView).showUserInitials(any());

        homePresenter.onDetach();
        verifyNoMoreInteractions(userRepository, homeView);
    }
}
