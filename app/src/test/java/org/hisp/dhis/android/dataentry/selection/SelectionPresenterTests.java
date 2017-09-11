package org.hisp.dhis.android.dataentry.selection;

import android.support.v7.widget.SearchView;

import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;

import org.hisp.dhis.android.dataentry.commons.schedulers.MockSchedulersProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(JUnit4.class)
public class SelectionPresenterTests {
    private static final String ARG_UID = "test_parent_uid";
    private static final String ARG_NAME = "test_parent_name";
    private static final String UID_1 = "test_uid_1";
    private static final String UID_2 = "test_uid_2";
    private static final String UID_3 = "test_uid_3";
    private static final String NAME_1 = "test_1_name";
    private static final String NAME_2 = "test_2_name";
    private static final String NAME_3 = "test_3_name";
    private static final String TEST_QUERY = "test_query";
    private static final String TEST_QUERY_2 = "test_query_2";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SelectionView view;

    @Mock
    private SearchView searchView;

    @Mock
    private SelectionRepository repository;

    @Captor
    private ArgumentCaptor<List<SelectionViewModel>> viewCaptor;

    @Mock
    private SelectionArgument argument;

    private PublishSubject<SearchViewQueryTextEvent> viewPublisher;
    private PublishProcessor<List<SelectionViewModel>> repositoryPublisher;

    // under tests
    private SelectionPresenter presenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        presenter = new SelectionPresenterImpl(ARG_NAME, repository,
                new NoopSelectionHandler(), new MockSchedulersProvider());

        viewPublisher = PublishSubject.create();
        repositoryPublisher = PublishProcessor.create();

        when(argument.uid()).thenReturn(ARG_UID);
        when(argument.name()).thenReturn(ARG_NAME);

        when(repository.search(TEST_QUERY)).thenReturn(repositoryPublisher);
        when(view.searchView()).thenReturn(viewPublisher);
    }

    @Test
    public void onAttachMustSubscribeToView() throws Exception {
        presenter.onAttach(view);
        viewPublisher.onNext(SearchViewQueryTextEvent.create(searchView, TEST_QUERY, false));
        repositoryPublisher.onNext(Arrays.asList(
                SelectionViewModel.create(UID_1, NAME_1),
                SelectionViewModel.create(UID_2, NAME_2)));

        verify(view.renderSearchResults()).accept(viewCaptor.capture());
        verify(repository).search(TEST_QUERY);
        assertThat(viewPublisher.hasObservers()).isTrue();
        assertThat(viewCaptor.getValue()).isEqualTo(Arrays.asList(
                SelectionViewModel.create(UID_1, NAME_1),
                SelectionViewModel.create(UID_2, NAME_2)));
    }

    @Test
    public void searchViewUpdates() throws Exception {
        presenter.onAttach(view);

        // trigger first search event
        when(repository.search(TEST_QUERY)).thenReturn(repositoryPublisher);
        viewPublisher.onNext(SearchViewQueryTextEvent.create(searchView, TEST_QUERY, false));
        repositoryPublisher.onNext(Arrays.asList(
                SelectionViewModel.create(UID_1, NAME_1),
                SelectionViewModel.create(UID_2, NAME_2)));

        verify(view.renderSearchResults()).accept(viewCaptor.capture());
        verify(repository).search(TEST_QUERY);
        assertThat(viewPublisher.hasObservers()).isTrue();
        assertThat(viewCaptor.getValue()).isEqualTo(Arrays.asList(
                SelectionViewModel.create(UID_1, NAME_1),
                SelectionViewModel.create(UID_2, NAME_2)));

        // trigger second search event
        when(repository.search(TEST_QUERY_2)).thenReturn(repositoryPublisher);
        viewPublisher.onNext(SearchViewQueryTextEvent.create(searchView, TEST_QUERY_2, false));
        repositoryPublisher.onNext(Arrays.asList(SelectionViewModel.create(UID_3, NAME_3)));

        verify(view.renderSearchResults(), times(2)).accept(viewCaptor.capture());
        verify(repository).search(TEST_QUERY_2);
        assertThat(viewPublisher.hasObservers()).isTrue();
        assertThat(viewCaptor.getValue().size()).isEqualTo(1);
        assertThat(viewCaptor.getValue().get(0)).isEqualTo(SelectionViewModel.create(UID_3, NAME_3));
    }

    @Test
    public void onDetachMustUnsubscribeFromView() {
        presenter.onAttach(view);
        assertThat(viewPublisher.hasObservers()).isTrue();
        assertThat(repositoryPublisher.hasSubscribers()).isFalse();

        presenter.onDetach();
        assertThat(repositoryPublisher.hasSubscribers()).isFalse();
        assertThat(viewPublisher.hasObservers()).isFalse();
    }
}



