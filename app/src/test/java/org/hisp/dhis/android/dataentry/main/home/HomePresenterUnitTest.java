/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.dataentry.main.home;

import org.hisp.dhis.android.dataentry.utils.MockSchedulersProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HomePresenterUnitTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    HomeView homeView;

    @Mock
    HomeRepository homeRepository;

    private HomePresenter homePresenter; // the class we are testing

    @Captor
    private ArgumentCaptor<List<HomeViewModel>> homeEntityListCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        homePresenter = new HomePresenterImpl(new MockSchedulersProvider(), homeRepository);
    }

    @Test
    public void onAttachShouldPopulateList() throws Exception {

        List<HomeViewModel> homeViewModelList = new ArrayList<>();
        homeViewModelList.add(HomeViewModel.create("test_id", "test_display_name", HomeViewModel.Type.PROGRAM));

        when(homeRepository.homeEntities()).thenReturn(Observable.just(homeViewModelList));

        homePresenter.onAttach(homeView);

        verify(homeView.swapData()).accept(homeEntityListCaptor.capture());

        assertThat(homeEntityListCaptor.getValue()).isEqualTo(homeViewModelList);

    }

    @Test
    public void onDetachShouldRemoveObservers() {

        PublishSubject<List<HomeViewModel>> subject = PublishSubject.create();
        when(homeRepository.homeEntities()).thenReturn(subject);

        homePresenter.onAttach(homeView);
        assertThat(subject.hasObservers()).isTrue();

        homePresenter.onDetach();
        assertThat(subject.hasObservers()).isFalse();

    }

}