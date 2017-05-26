package org.hisp.dhis.android.dataentry.form;

import org.hisp.dhis.android.dataentry.commons.schedulers.MockSchedulersProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormPresenterUnitTests {

    private FormPresenterImpl formPresenter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FormView formView;

    @Mock
    private FormRepository formRepository;

    @Mock
    FormViewArguments formViewArguments;

    @Captor
    private ArgumentCaptor<List<FormSectionViewModel>> sectionsCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<ReportStatus> reportStatusCaptor;

    private PublishProcessor<List<FormSectionViewModel>> sectionPublisher;
    private PublishProcessor<String> titlePublisher;
    private PublishProcessor<String> reportDatePublisher;
    private PublishProcessor<ReportStatus> reportStatusPublisher;

    private PublishSubject<ReportStatus> reportStatusSubject;
    private PublishSubject<String> reportDateSubject;

    @Mock
    Consumer<String> reportDateConsumer;

    @Mock
    Consumer<ReportStatus> reportStatusConsumer;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        sectionPublisher = PublishProcessor.create();
        titlePublisher = PublishProcessor.create();
        reportDatePublisher = PublishProcessor.create();
        reportStatusPublisher = PublishProcessor.create();

        when(formViewArguments.uid()).thenReturn("test_uid");

        when(formRepository.title("test_uid")).thenReturn(titlePublisher);
        when(formRepository.reportDate("test_uid")).thenReturn(reportDatePublisher);
        when(formRepository.reportStatus("test_uid")).thenReturn(reportStatusPublisher);
        when(formRepository.sections("test_uid")).thenReturn(sectionPublisher);

        when(formRepository.storeReportDate("test_uid")).thenReturn(reportDateConsumer);
        when(formRepository.storeReportStatus("test_uid")).thenReturn(reportStatusConsumer);

        reportStatusSubject = PublishSubject.create();
        reportDateSubject = PublishSubject.create();

        when(formView.eventStatusChanged()).thenReturn(reportStatusSubject);
        when(formView.reportDateChanged()).thenReturn(reportDateSubject);

        formPresenter = new FormPresenterImpl(formViewArguments,
                new MockSchedulersProvider(), formRepository);
    }

    @Test
    public void titleIsRenderedOnAttach() throws Exception {
        formPresenter.onAttach(formView);
        titlePublisher.onNext("TITLE");

        verify(formView.renderTitle()).accept(stringCaptor.capture());
        verify(formRepository).title("test_uid");
        assertThat(stringCaptor.getValue()).isEqualTo("TITLE");
    }

    @Test
    public void titleIsUpdatedAccordingToDatabase() throws Exception {
        formPresenter.onAttach(formView);
        titlePublisher.onNext("TITLE");

        verify(formView.renderTitle()).accept(stringCaptor.capture());
        verify(formRepository).title("test_uid");
        assertThat(stringCaptor.getValue()).isEqualTo("TITLE");

        titlePublisher.onNext("NEW TITLE");
        verify(formView.renderTitle(), times(2)).accept(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("NEW TITLE");
    }

    @Test
    public void reportDateIsRenderedOnAttach() throws Exception {
        formPresenter.onAttach(formView);
        reportDatePublisher.onNext("2016-09-09");

        verify(formView.renderReportDate()).accept(stringCaptor.capture());
        verify(formRepository).reportDate("test_uid");
        assertThat(stringCaptor.getValue()).isEqualTo("2016-09-09");
    }

    @Test
    public void reportDateIsUpdatedAccordingToDatabase() throws Exception {
        formPresenter.onAttach(formView);
        reportDatePublisher.onNext("2016-09-09");

        verify(formView.renderReportDate()).accept(stringCaptor.capture());
        verify(formRepository).reportDate("test_uid");
        assertThat(stringCaptor.getValue()).isEqualTo("2016-09-09");

        reportDatePublisher.onNext("2077-07-07");

        verify(formView.renderReportDate(), times(2)).accept(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("2077-07-07");
    }

    @Test
    public void reportStatusIsRenderedOnAttach() throws Exception {
        when(formViewArguments.type()).thenReturn(FormViewArguments.Type.EVENT);

        formPresenter.onAttach(formView);
        reportStatusPublisher.onNext(ReportStatus.ACTIVE);

        verify(formView.renderStatus()).accept(reportStatusCaptor.capture());
        verify(formRepository).reportStatus("test_uid");
        assertThat(reportStatusCaptor.getValue()).isEqualTo(ReportStatus.ACTIVE);
    }

    @Test
    public void reportStatusIsUpdatedAccordingToDatabase() throws Exception {
        when(formViewArguments.type()).thenReturn(FormViewArguments.Type.EVENT);

        formPresenter.onAttach(formView);
        reportStatusPublisher.onNext(ReportStatus.ACTIVE);

        verify(formView.renderStatus()).accept(reportStatusCaptor.capture());
        verify(formRepository).reportStatus("test_uid");
        assertThat(reportStatusCaptor.getValue()).isEqualTo(ReportStatus.ACTIVE);

        reportStatusPublisher.onNext(ReportStatus.COMPLETED);
        verify(formView.renderStatus(), times(2)).accept(reportStatusCaptor.capture());
        assertThat(reportStatusCaptor.getValue()).isEqualTo(ReportStatus.COMPLETED);
    }

    @Test
    public void statusChangeSnackBarIsRenderedOnStatusChanges() throws Exception {
        formPresenter.onAttach(formView);
        verify(formRepository, times(1)).reportStatus("test_uid");

        // no snack bar is shown when page is first drawn
        reportStatusPublisher.onNext(ReportStatus.ACTIVE);
        verify(formView, times(0)).renderStatusChangeSnackBar(ReportStatus.ACTIVE);

        // snack bar is shown on consequent status changes
        reportStatusPublisher.onNext(ReportStatus.COMPLETED);
        verify(formView).renderStatusChangeSnackBar(ReportStatus.COMPLETED);

        // dont show snackbar if status is the same
        reportStatusPublisher.onNext(ReportStatus.COMPLETED);
        // still only one invocation
        verify(formView, times(1)).renderStatusChangeSnackBar(ReportStatus.COMPLETED);

        // snack bar is shown on consequent status changes
        reportStatusPublisher.onNext(ReportStatus.ACTIVE);
        verify(formView).renderStatusChangeSnackBar(ReportStatus.ACTIVE);
    }

    @Test
    public void sectionsAreRenderedOnAttach() throws Exception {
        FormSectionViewModel formSectionViewModel1 =
                FormSectionViewModel.createForSection("event_uid", "section_uid", "label");
        FormSectionViewModel formSectionViewModel2 =
                FormSectionViewModel.createForSection("event_uid2", "section_uid2", "label2");
        List<FormSectionViewModel> sections = Arrays.asList(formSectionViewModel1, formSectionViewModel2);

        formPresenter.onAttach(formView);
        sectionPublisher.onNext(sections);

        verify(formView.renderSectionViewModels()).accept(sectionsCaptor.capture());
        verify(formRepository).sections("test_uid");
        assertThat(sectionsCaptor.getValue()).isEqualTo(sections);
    }

    @Test
    public void sectionsAreUpdatedAccordingToDatabase() throws Exception {
        FormSectionViewModel formSectionViewModel1 =
                FormSectionViewModel.createForSection("event_uid", "section_uid", "label");
        FormSectionViewModel formSectionViewModel2 =
                FormSectionViewModel.createForSection("event_uid2", "section_uid2", "label2");
        List<FormSectionViewModel> sections = Arrays.asList(formSectionViewModel1, formSectionViewModel2);

        formPresenter.onAttach(formView);
        sectionPublisher.onNext(sections);

        verify(formView.renderSectionViewModels()).accept(sectionsCaptor.capture());
        verify(formRepository).sections("test_uid");
        assertThat(sectionsCaptor.getValue()).isEqualTo(sections);

        FormSectionViewModel newFormSectionViewModel =
                FormSectionViewModel.createForSection("new_event_uid", "new_section_uid", "new_label");
        FormSectionViewModel newFormSectionViewModel2 =
                FormSectionViewModel.createForSection("new_event_uid2", "new_section_uid2", "new_label2");

        List<FormSectionViewModel> newSections =
                Arrays.asList(newFormSectionViewModel, newFormSectionViewModel2);

        sectionPublisher.onNext(newSections);

        verify(formView.renderSectionViewModels(), times(2)).accept(sectionsCaptor.capture());
        assertThat(sectionsCaptor.getValue()).isEqualTo(newSections);
    }

    @Test
    public void reportDateIsPropagatedToRepositoryForStoring() throws Exception {
        formPresenter.onAttach(formView);
        reportDateSubject.onNext("2016-19-09");
        verify(reportDateConsumer).accept("2016-19-09");
    }

    @Test
    public void reportStatusIsPropagatedToRepositoryForStoring() throws Exception {
        formPresenter.onAttach(formView);
        reportStatusSubject.onNext(ReportStatus.COMPLETED);
        verify(reportStatusConsumer).accept(ReportStatus.COMPLETED);
    }

    @Test
    public void onDetachShouldUnsubscribeFromRepositoryAndView() {
        formPresenter.onAttach(formView);
        assertThat(sectionPublisher.hasSubscribers()).isTrue();
        assertThat(titlePublisher.hasSubscribers()).isTrue();
        assertThat(reportDatePublisher.hasSubscribers()).isTrue();
        assertThat(reportStatusPublisher.hasSubscribers()).isTrue();
        assertThat(reportStatusSubject.hasObservers()).isTrue();
        assertThat(reportDateSubject.hasObservers()).isTrue();

        formPresenter.onDetach();
        assertThat(sectionPublisher.hasSubscribers()).isFalse();
        assertThat(titlePublisher.hasSubscribers()).isFalse();
        assertThat(reportDatePublisher.hasSubscribers()).isFalse();
        assertThat(reportStatusPublisher.hasSubscribers()).isFalse();
        assertThat(reportStatusSubject.hasObservers()).isFalse();
        assertThat(reportDateSubject.hasObservers()).isFalse();
    }

}