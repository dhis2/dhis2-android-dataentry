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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(JUnit4.class)
public class SelectionPresenterTests {

    public static final String ARG_UID = "test_parent_uid";
    public static final String ARG_NAME = "test_parent_name";
    public static final String UID_1 = "test_uid_1";
    public static final String NAME_1 = "test_1_name";
    public static final String UID_2 = "test_uid_2";
    public static final String NAME_2 = "test_2_name";
    public static final String UID_3 = "test_uid_3";
    public static final String NAME_3 = "test_3_name";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SelectionView view;

    @Mock
    private SearchView searchView;

    @Captor
    private ArgumentCaptor<List<SelectionViewModel>> viewCaptor;
    private PublishSubject<SearchViewQueryTextEvent> viewPublisher;
    private PublishProcessor<List<SelectionViewModel>> repositoryPublisher;

    @Mock
    private SelectionRepository repository;

    @Mock
    private SelectionArgument argument;

    private SelectionPresenter presenter;

    private List<SelectionViewModel> values;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        viewPublisher = PublishSubject.create();
        repositoryPublisher = PublishProcessor.create();
        presenter = new SelectionPresenterImpl(argument, repository, new MockSchedulersProvider());

        values = new ArrayList<>(3);
        values.add(SelectionViewModel.create(UID_1, NAME_1));
        values.add(SelectionViewModel.create(UID_2, NAME_2));

        when(argument.uid()).thenReturn(ARG_UID);
        when(argument.name()).thenReturn(ARG_NAME);

        when(repository.list()).thenReturn(repositoryPublisher);
        when(view.subscribeToSearchView()).thenReturn(viewPublisher);


    }

    @Test
    public void onAttach() throws Exception {
        presenter.onAttach(view);
        viewPublisher.onNext(SearchViewQueryTextEvent.create(searchView, "", false));
        repositoryPublisher.onNext(values);

        assertThat(viewPublisher.hasObservers()).isTrue();
        verify(view.update()).accept(viewCaptor.capture());
        verify(repository).list();
        assertThat(viewCaptor.getValue()).isEqualTo(values);
    }

    /* Updates from database will not be propagated to the view, by design.
    Since the complexity of writing an rx call that does that was quite high vs the benefits (how often does the data
     in the database change when the dialog is up ?)
     */

    @Test
    public void searchViewUpdates() throws Exception {
        presenter.onAttach(view);
        viewPublisher.onNext(SearchViewQueryTextEvent.create(searchView, "", false));
        repositoryPublisher.onNext(values);

        assertThat(viewPublisher.hasObservers()).isTrue();
        verify(view.update()).accept(viewCaptor.capture());
        verify(repository).list();
        assertThat(viewCaptor.getValue()).isEqualTo(values);

        values.add( SelectionViewModel.create(UID_3, NAME_3));
        viewPublisher.onNext(SearchViewQueryTextEvent.create(searchView, "3", false));
        repositoryPublisher.onNext(values);

        assertThat(viewPublisher.hasObservers()).isTrue();
        verify(view.update(), times(2)).accept(viewCaptor.capture());
        verify(repository, times(2)).list();
        assertThat(viewCaptor.getValue().size()).isEqualTo(1);
        assertThat(viewCaptor.getValue().get(0)).isEqualTo(SelectionViewModel.create(UID_3, NAME_3));
    }

    @Test
    public void onDetach() {
        presenter.onAttach(view);
        assertThat(viewPublisher.hasObservers()).isTrue();
        presenter.onDetach();
        assertThat(repositoryPublisher.hasSubscribers()).isFalse();
        assertThat(viewPublisher.hasObservers()).isFalse();
    }
}



