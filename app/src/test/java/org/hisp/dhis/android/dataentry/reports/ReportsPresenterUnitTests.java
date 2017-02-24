package org.hisp.dhis.android.dataentry.reports;

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

import java.util.Arrays;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ReportsPresenterUnitTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ReportsView reportsView;

    @Mock
    private ReportsRepository reportsRepository;

    @Captor
    private ArgumentCaptor<List<ReportViewModel>> reportViewModelsCaptor;

    private ReportsPresenter reportsPresenter;
    private PublishSubject<List<ReportViewModel>> reportsPublisher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        reportsPublisher = PublishSubject.create();
        reportsPresenter = new ReportsPresenterImpl(ReportType.EVENT,
                new MockSchedulersProvider(), reportsRepository);
        when(reportsRepository.reports(ReportType.EVENT))
                .thenReturn(reportsPublisher);
    }

    @Test
    public void onAttachShouldRenderReportViewModels() throws Exception {
        List<ReportViewModel> reports = Arrays.asList(
                ReportViewModel.create("test_report_id_one", ReportViewModel.Status.SENT,
                        Arrays.asList("test_label_one", "test_label_two")),
                ReportViewModel.create("test_report_id_two", ReportViewModel.Status.ERROR,
                        Arrays.asList("test_label_one", "test_label_two")),
                ReportViewModel.create("test_report_id_three", ReportViewModel.Status.TO_POST,
                        Arrays.asList("test_label_one", "test_label_two")));

        reportsPresenter.onAttach(reportsView);
        reportsPublisher.onNext(reports);

        verify(reportsView.renderReportViewModels()).accept(reportViewModelsCaptor.capture());
        verify(reportsRepository).reports(ReportType.EVENT);
        assertThat(reportViewModelsCaptor.getValue()).isEqualTo(reports);
    }

    @Test
    public void updatesShouldBePropagatedToView() throws Exception {
        List<ReportViewModel> reports = Arrays.asList(ReportViewModel.create("test_report_id_one",
                ReportViewModel.Status.SENT, Arrays.asList("test_label_one", "test_label_two")));

        when(reportsRepository.reports(ReportType.EVENT)).thenReturn(reportsPublisher);

        reportsPresenter.onAttach(reportsView);

        // push new model to the presenter
        reportsPublisher.onNext(reports);

        verify(reportsView.renderReportViewModels()).accept(reportViewModelsCaptor.capture());
        verify(reportsRepository).reports(ReportType.EVENT);
        assertThat(reportViewModelsCaptor.getValue()).isEqualTo(reports);
    }

    @Test
    public void onDetachShouldUnsubscribeFromRepository() {
        reportsPresenter.onAttach(reportsView);
        assertThat(reportsPublisher.hasObservers()).isTrue();

        reportsPresenter.onDetach();
        assertThat(reportsPublisher.hasObservers()).isFalse();
    }
}
