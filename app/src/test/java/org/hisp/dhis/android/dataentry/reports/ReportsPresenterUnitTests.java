package org.hisp.dhis.android.dataentry.reports;

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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ReportsPresenterUnitTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ReportsView reportsView;

    @Mock
    private ReportsRepository reportsRepository;

    @Mock
    private ReportsArguments reportsArguments;

    @Captor
    private ArgumentCaptor<List<ReportViewModel>> reportViewModelsCaptor;

    @Captor
    private ArgumentCaptor<String> createReportCaptor;

    private ReportsPresenter reportsPresenter;
    private PublishProcessor<List<ReportViewModel>> reportsPublisher;
    private PublishSubject<Object> createReportPublisher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        reportsPublisher = PublishProcessor.create();
        createReportPublisher = PublishSubject.create();
        reportsPresenter = new ReportsPresenterImpl(reportsArguments,
                new MockSchedulersProvider(), reportsRepository);

        when(reportsArguments.entityUid()).thenReturn("test_entity_uid");
        when(reportsRepository.reports("test_entity_uid")).thenReturn(reportsPublisher);
        when(reportsView.createReportsActions()).thenReturn(createReportPublisher);
    }

    @Test
    public void onAttachShouldRenderReportViewModels() throws Exception {
        List<ReportViewModel> reports = Arrays.asList(
                ReportViewModel.create("test_report_id_one", ReportViewModel.Status.SYNCED,
                        Arrays.asList("test_label_one", "test_label_two")),
                ReportViewModel.create("test_report_id_two", ReportViewModel.Status.FAILED,
                        Arrays.asList("test_label_one", "test_label_two")),
                ReportViewModel.create("test_report_id_three", ReportViewModel.Status.TO_SYNC,
                        Arrays.asList("test_label_one", "test_label_two")));

        reportsPresenter.onAttach(reportsView);
        reportsPublisher.onNext(reports);

        verify(reportsView.renderReportViewModels()).accept(reportViewModelsCaptor.capture());
        verify(reportsRepository).reports("test_entity_uid");
        assertThat(reportViewModelsCaptor.getValue()).isEqualTo(reports);
    }

    @Test
    public void updatesShouldBePropagatedToView() throws Exception {
        List<ReportViewModel> reportsOne = Arrays.asList(ReportViewModel.create("test_report_id_one",
                ReportViewModel.Status.SYNCED, Arrays.asList("test_label_one", "test_label_two")));

        when(reportsRepository.reports("test_entity_uid")).thenReturn(reportsPublisher);

        reportsPresenter.onAttach(reportsView);
        reportsPublisher.onNext(reportsOne);

        verify(reportsView.renderReportViewModels()).accept(reportViewModelsCaptor.capture());
        verify(reportsRepository).reports("test_entity_uid");
        assertThat(reportViewModelsCaptor.getValue()).isEqualTo(reportsOne);

        List<ReportViewModel> reportsTwo = Arrays.asList(ReportViewModel.create("test_report_id_two",
                ReportViewModel.Status.SYNCED, Arrays.asList("test_label_three", "test_label_four")));

        reportsPublisher.onNext(reportsTwo);

        verify(reportsView.renderReportViewModels(), times(2)).accept(reportViewModelsCaptor.capture());
        verify(reportsRepository).reports("test_entity_uid");
        assertThat(reportViewModelsCaptor.getValue()).isEqualTo(reportsTwo);
    }

    @Test
    public void createReportActionsShouldCallCreateReport() throws Exception {
        when(reportsRepository.reports("test_program_uid")).thenReturn(reportsPublisher);
        when(reportsArguments.entityUid()).thenReturn("test_program_uid");

        // simulate click
        reportsPresenter.onAttach(reportsView);
        createReportPublisher.onNext(new Object());

        verify(reportsView.createReport()).accept(createReportCaptor.capture());
        assertThat(createReportCaptor.getValue()).isEqualTo("test_program_uid");
    }

    @Test
    public void onDetachShouldUnsubscribeFromRepository() {
        reportsPresenter.onAttach(reportsView);
        assertThat(reportsPublisher.hasSubscribers()).isTrue();
        assertThat(createReportPublisher.hasObservers()).isTrue();

        reportsPresenter.onDetach();
        assertThat(reportsPublisher.hasSubscribers()).isFalse();
        assertThat(createReportPublisher.hasObservers()).isFalse();
    }
}
