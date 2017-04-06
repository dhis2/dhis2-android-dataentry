package org.hisp.dhis.android.dataentry.main.home;

import org.hisp.dhis.android.dataentry.commons.schedulers.MockSchedulersProvider;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HomePresenterUnitTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    HomeView homeView;

    @Mock
    HomeRepository homeRepository;

    private HomePresenter homePresenter; // the class we are testing

    @Captor
    private ArgumentCaptor<List<HomeViewModel>> homeViewModelListCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        homePresenter = new HomePresenterImpl(new MockSchedulersProvider(), homeRepository);
    }

    @Test
    public void onAttachShouldPopulateList() throws Exception {

        List<HomeViewModel> homeViewModelList = new ArrayList<>();
        homeViewModelList.add(HomeViewModel.create("test_id", "test_display_name", HomeViewModel.Type.PROGRAM));

        when(homeRepository.homeViewModels()).thenReturn(Observable.just(homeViewModelList));

        homePresenter.onAttach(homeView);

        verify(homeView.swapData()).accept(homeViewModelListCaptor.capture());

        assertThat(homeViewModelListCaptor.getValue()).isEqualTo(homeViewModelList);

    }

    @Test
    public void updatesShouldBePropagatedToView() throws Exception {

        PublishSubject<List<HomeViewModel>> subject = PublishSubject.create();
        when(homeRepository.homeViewModels()).thenReturn(subject);

        List<HomeViewModel> homeViewModelList = new ArrayList<>();
        homeViewModelList.add(HomeViewModel.create("test_id", "test_display_name", HomeViewModel.Type.PROGRAM));

        homePresenter.onAttach(homeView);

        subject.onNext(homeViewModelList);
        verify(homeView.swapData()).accept(homeViewModelListCaptor.capture());

        assertThat(homeViewModelListCaptor.getValue()).isEqualTo(homeViewModelList);

        List<HomeViewModel> secondHomeViewModelList = new ArrayList<>();
        secondHomeViewModelList.add(
                HomeViewModel.create("second_test_id", "second_test_display_name", HomeViewModel.Type.TRACKED_ENTITY));

        subject.onNext(secondHomeViewModelList);

        verify(homeView.swapData(), times(2)).accept(homeViewModelListCaptor.capture());

        assertThat(homeViewModelListCaptor.getValue()).isEqualTo(secondHomeViewModelList);

    }

    @Test
    public void onDetachShouldRemoveObservers() {

        PublishSubject<List<HomeViewModel>> subject = PublishSubject.create();
        when(homeRepository.homeViewModels()).thenReturn(subject);

        homePresenter.onAttach(homeView);
        assertThat(subject.hasObservers()).isTrue();

        homePresenter.onDetach();
        assertThat(subject.hasObservers()).isFalse();

    }

}