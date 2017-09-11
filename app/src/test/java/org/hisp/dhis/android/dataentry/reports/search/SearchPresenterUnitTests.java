package org.hisp.dhis.android.dataentry.reports.search;

import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;

import org.hisp.dhis.android.dataentry.commons.schedulers.MockSchedulersProvider;
import org.hisp.dhis.android.dataentry.reports.ReportViewModel;
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
import java.util.Arrays;
import java.util.List;

import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class SearchPresenterUnitTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SearchView searchView;

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private SearchArguments searchArguments;

    @Captor
    private ArgumentCaptor<List<ReportViewModel>> reportViewModelsCaptor;

    @Captor
    private ArgumentCaptor<String> createReportCaptor;

    @Captor
    private ArgumentCaptor<Boolean> createButtonVisibility;

    private SearchPresenter searchPresenter;
    private PublishProcessor<List<ReportViewModel>> reportsPublisher;

    private PublishSubject<CharSequence> searchBoxActions;
    private PublishSubject<Object> createReportPublisher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        reportsPublisher = PublishProcessor.create();
        searchBoxActions = PublishSubject.create();
        createReportPublisher = PublishSubject.create();

        searchPresenter = new SearchPresenterImpl(searchArguments,
                new MockSchedulersProvider(), searchRepository);

        when(searchArguments.entityUid()).thenReturn("test_entity_uid");
        when(searchRepository.search("test_entity")).thenReturn(reportsPublisher);
        when(searchView.createReportsActions()).thenReturn(createReportPublisher);
        when(searchView.searchBoxActions()).thenReturn(searchBoxActions);
    }

    @Test
    public void onAttachShouldListenToUpdatesInView() throws Exception {
        List<ReportViewModel> reports = Arrays.asList(
                ReportViewModel.create(ReportViewModel.Status.SYNCED,
                        "test_report_id_one", "test_label_one\ntest_label_two"),
                ReportViewModel.create(ReportViewModel.Status.FAILED, "test_report_id_two",
                        "test_label_one\ntest_label_two"),
                ReportViewModel.create(ReportViewModel.Status.TO_SYNC, "test_report_id_three",
                        "test_label_one\ntest_label_two"));

        searchPresenter.onAttach(searchView);
        searchBoxActions.onNext("test_entity");
        reportsPublisher.onNext(reports);

        verify(searchView.renderSearchResults()).accept(reportViewModelsCaptor.capture());
        verify(searchRepository).search("test_entity");
        assertThat(reportViewModelsCaptor.getValue()).isEqualTo(reports);
    }

    @Test
    public void updatesShouldBePropagatedToView() throws Exception {
        List<ReportViewModel> reportsOne = Arrays.asList(ReportViewModel.create(ReportViewModel.Status.SYNCED,
                "test_report_id_one", "test_label_one\ntest_label_two"));

        when(searchRepository.search("test_entity")).thenReturn(reportsPublisher);

        searchPresenter.onAttach(searchView);
        searchBoxActions.onNext("test_entity");
        reportsPublisher.onNext(reportsOne);

        verify(searchView.renderSearchResults()).accept(reportViewModelsCaptor.capture());
        verify(searchRepository).search("test_entity");
        assertThat(reportViewModelsCaptor.getValue()).isEqualTo(reportsOne);

        List<ReportViewModel> reportsTwo = Arrays.asList(ReportViewModel.create(
                ReportViewModel.Status.SYNCED, "test_report_id_two", "test_label_three\ntest_label_four"));

        reportsPublisher.onNext(reportsTwo);

        verify(searchView.renderSearchResults(), times(2)).accept(reportViewModelsCaptor.capture());
        verify(searchRepository).search("test_entity");
        assertThat(reportViewModelsCaptor.getValue()).isEqualTo(reportsTwo);
    }

    @Test
    public void onAttachMustListenToFabUpdates() throws Exception {
        searchPresenter.onAttach(searchView);

        // empty query
        when(searchRepository.search("")).thenReturn(reportsPublisher);
        searchBoxActions.onNext("test_entity");
        reportsPublisher.onNext(new ArrayList<>());

        verify(searchView.renderSearchResults()).accept(reportViewModelsCaptor.capture());
        verify(searchView.renderCreateButton(), times(1)).accept(createButtonVisibility.capture());
        verify(searchRepository).search("test_entity");

        assertThat(reportViewModelsCaptor.getValue()).isEmpty();
        assertThat(createButtonVisibility.getAllValues().get(0)).isTrue();

        // non-empty query
        SearchViewQueryTextEvent textChangeEventTwo = mock(SearchViewQueryTextEvent.class, Answers.RETURNS_DEEP_STUBS);
        when(searchRepository.search("test_entity")).thenReturn(reportsPublisher);
        when(textChangeEventTwo.queryText().toString()).thenReturn("test_entity");
        searchBoxActions.onNext("test_entity");
        reportsPublisher.onNext(new ArrayList<>());

        verify(searchView.renderSearchResults(), times(2)).accept(reportViewModelsCaptor.capture());
        verify(searchView.renderCreateButton(), times(2)).accept(createButtonVisibility.capture());
        verify(searchRepository).search("test_entity");

        assertThat(reportViewModelsCaptor.getAllValues().get(2)).isEmpty();
        assertThat(createButtonVisibility.getAllValues().get(2)).isTrue();
    }

    @Test
    public void createReportActionsShouldCallCreateReport() throws Exception {
        when(searchRepository.search("test_entity")).thenReturn(reportsPublisher);
        when(searchArguments.entityUid()).thenReturn("test_uid");

        // simulate click
        searchPresenter.onAttach(searchView);
        createReportPublisher.onNext(new Object());

        verify(searchView.createReport()).accept(createReportCaptor.capture());
        assertThat(createReportCaptor.getValue()).isEqualTo("test_uid");
    }

    @Test
    public void onDetachShouldUnsubscribeFromRepository() {
        searchPresenter.onAttach(searchView);
        assertThat(searchBoxActions.hasObservers()).isTrue();
        assertThat(createReportPublisher.hasObservers()).isTrue();

        searchPresenter.onDetach();
        assertThat(searchBoxActions.hasObservers()).isFalse();
        assertThat(createReportPublisher.hasObservers()).isFalse();
    }
}
