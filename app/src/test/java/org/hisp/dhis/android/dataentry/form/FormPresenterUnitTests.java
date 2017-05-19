package org.hisp.dhis.android.dataentry.form;

import org.hisp.dhis.android.core.event.EventStatus;
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
    private ArgumentCaptor<EventStatus> eventStatusCaptor;

    private PublishProcessor<List<FormSectionViewModel>> sectionPublisher;
    private PublishProcessor<String> titlePublisher;
    private PublishProcessor<String> reportDatePublisher;
    private PublishProcessor<EventStatus> eventStatusPublisher;

    private PublishSubject<EventStatus> eventStatusSubject;
    private PublishSubject<String> reportDateSubject;

    @Mock
    Consumer<String> reportDateConsumer;

    @Mock
    Consumer<EventStatus> eventStatusConsumer;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        sectionPublisher = PublishProcessor.create();
        titlePublisher = PublishProcessor.create();
        reportDatePublisher = PublishProcessor.create();
        eventStatusPublisher = PublishProcessor.create();

        when(formView.formViewArguments()).thenReturn(formViewArguments);
        when(formViewArguments.uid()).thenReturn("test_uid");

        when(formRepository.title("test_uid")).thenReturn(titlePublisher);
        when(formRepository.reportDate("test_uid")).thenReturn(reportDatePublisher);
        when(formRepository.reportStatus("test_uid")).thenReturn(eventStatusPublisher);
        when(formRepository.sections("test_uid")).thenReturn(sectionPublisher);

        when(formRepository.storeReportDate("test_uid")).thenReturn(reportDateConsumer);
        when(formRepository.storeEventStatus("test_uid")).thenReturn(eventStatusConsumer);

        eventStatusSubject = PublishSubject.create();
        reportDateSubject = PublishSubject.create();

        when(formView.eventStatusChanged()).thenReturn(eventStatusSubject);
        when(formView.reportDateChanged()).thenReturn(reportDateSubject);

        formPresenter = new FormPresenterImpl(new MockSchedulersProvider(), formRepository);

        // TODO: test storing of report date and event status
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
    public void eventStatusIsRenderedOnAttach() throws Exception {
        when(formViewArguments.type()).thenReturn(FormViewArguments.Type.EVENT);

        formPresenter.onAttach(formView);
        eventStatusPublisher.onNext(EventStatus.ACTIVE);

        verify(formView.renderStatus()).accept(eventStatusCaptor.capture());
        verify(formRepository).reportStatus("test_uid");
        assertThat(eventStatusCaptor.getValue()).isEqualTo(EventStatus.ACTIVE);
    }

    @Test
    public void eventStatusIsUpdatedAccordingToDatabase() throws Exception {
        when(formViewArguments.type()).thenReturn(FormViewArguments.Type.EVENT);

        formPresenter.onAttach(formView);
        eventStatusPublisher.onNext(EventStatus.ACTIVE);

        verify(formView.renderStatus()).accept(eventStatusCaptor.capture());
        verify(formRepository).reportStatus("test_uid");
        assertThat(eventStatusCaptor.getValue()).isEqualTo(EventStatus.ACTIVE);

        eventStatusPublisher.onNext(EventStatus.COMPLETED);
        verify(formView.renderStatus(), times(2)).accept(eventStatusCaptor.capture());
        assertThat(eventStatusCaptor.getValue()).isEqualTo(EventStatus.COMPLETED);
    }

    @Test
    public void statusChangeSnackBarIsRenderedOnStatusChanges() throws Exception {
        when(formViewArguments.type()).thenReturn(FormViewArguments.Type.EVENT);

        formPresenter.onAttach(formView);
        verify(formRepository, times(1)).reportStatus("test_uid");

        // no snack bar is shown when page is first drawn
        eventStatusPublisher.onNext(EventStatus.ACTIVE);
        verify(formView, times(0)).renderStatusChangeSnackBar(EventStatus.ACTIVE);

        // snack bar is shown on consequent status changes
        eventStatusPublisher.onNext(EventStatus.COMPLETED);
        verify(formView).renderStatusChangeSnackBar(EventStatus.COMPLETED);

        // dont show snackbar if status is the same
        eventStatusPublisher.onNext(EventStatus.COMPLETED);
        // still only one invocation
        verify(formView, times(1)).renderStatusChangeSnackBar(EventStatus.COMPLETED);

        // snack bar is shown on consequent status changes
        eventStatusPublisher.onNext(EventStatus.ACTIVE);
        verify(formView).renderStatusChangeSnackBar(EventStatus.ACTIVE);
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
    public void eventStatusPropagatedToRepositoryForStoring() throws Exception {
        formPresenter.onAttach(formView);
        eventStatusSubject.onNext(EventStatus.COMPLETED);
        verify(eventStatusConsumer).accept(EventStatus.COMPLETED);
    }

    @Test
    public void onDetachShouldUnsubscribeFromRepositoryAndView() {
        formPresenter.onAttach(formView);
        assertThat(sectionPublisher.hasSubscribers()).isTrue();
        assertThat(titlePublisher.hasSubscribers()).isTrue();
        assertThat(reportDatePublisher.hasSubscribers()).isTrue();
        assertThat(eventStatusPublisher.hasSubscribers()).isTrue();
        assertThat(eventStatusSubject.hasObservers()).isTrue();
        assertThat(reportDateSubject.hasObservers()).isTrue();

        formPresenter.onDetach();
        assertThat(sectionPublisher.hasSubscribers()).isFalse();
        assertThat(titlePublisher.hasSubscribers()).isFalse();
        assertThat(reportDatePublisher.hasSubscribers()).isFalse();
        assertThat(eventStatusPublisher.hasSubscribers()).isFalse();
        assertThat(eventStatusSubject.hasObservers()).isFalse();
        assertThat(reportDateSubject.hasObservers()).isFalse();
    }

}