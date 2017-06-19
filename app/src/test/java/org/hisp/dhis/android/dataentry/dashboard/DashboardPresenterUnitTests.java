package org.hisp.dhis.android.dataentry.dashboard;

import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.dataentry.commons.schedulers.MockSchedulersProvider;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import io.reactivex.processors.PublishProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DashboardPresenterUnitTests {

    private DashboardPresenter dashboardPresenter;

    private String enrollmentUid = "enrollment_uid";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DashboardView dashboardView;

    @Mock
    private DashboardRepository dashboardRepository;

    @Captor
    private ArgumentCaptor<Pair<String, String>> attributesCaptor;

    @Captor
    private ArgumentCaptor<List<EventViewModel>> eventsCaptor;

    private PublishProcessor<List<String>> attributesPublisher;
    private PublishProcessor<List<EventViewModel>> eventsPublisher;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        attributesPublisher = PublishProcessor.create();
        eventsPublisher = PublishProcessor.create();

        when(dashboardRepository.attributes(enrollmentUid)).thenReturn(attributesPublisher);
        when(dashboardRepository.events(enrollmentUid)).thenReturn(eventsPublisher);

        dashboardPresenter = new DashboardPresenterImpl(enrollmentUid, new MockSchedulersProvider(),
                dashboardRepository);
    }

    @Test
    public void attributesAreRenderedOnAttach() throws Exception {
        dashboardPresenter.onAttach(dashboardView);
        attributesPublisher.onNext(Arrays.asList("Attribute 1", "Attribute 2"));

        verify(dashboardView.renderAttributes()).accept(attributesCaptor.capture());
        verify(dashboardRepository).attributes(enrollmentUid);
        assertThat(attributesCaptor.getValue().val0()).isEqualTo("Attribute 1");
        assertThat(attributesCaptor.getValue().val1()).isEqualTo("Attribute 2");
    }

    @Test
    public void attributesAreUpdatedAccordingToDatabase() throws Exception {
        dashboardPresenter.onAttach(dashboardView);
        attributesPublisher.onNext(Arrays.asList("Attribute 1", "Attribute 2"));

        verify(dashboardView.renderAttributes()).accept(attributesCaptor.capture());
        verify(dashboardRepository).attributes(enrollmentUid);
        assertThat(attributesCaptor.getValue().val0()).isEqualTo("Attribute 1");
        assertThat(attributesCaptor.getValue().val1()).isEqualTo("Attribute 2");

        attributesPublisher.onNext(Arrays.asList("Second Attribute 1", "Second Attribute 2"));
        verify(dashboardView.renderAttributes(), times(2)).accept(attributesCaptor.capture());
        assertThat(attributesCaptor.getValue().val0()).isEqualTo("Second Attribute 1");
        assertThat(attributesCaptor.getValue().val1()).isEqualTo("Second Attribute 2");
    }

    @Test
    public void eventsAreRenderedOnAttach() throws Exception {
        dashboardPresenter.onAttach(dashboardView);

        EventViewModel eventOne = EventViewModel.create("event_uid_1", "Event 1", "2017-06-30", EventStatus.SCHEDULE);
        EventViewModel eventTwo = EventViewModel.create("event_uid_2", "Event 2", "1999-12-31", EventStatus.SKIPPED);

        eventsPublisher.onNext(Arrays.asList(eventOne, eventTwo));

        verify(dashboardView.renderEvents()).accept(eventsCaptor.capture());
        verify(dashboardRepository).events(enrollmentUid);
        assertThat(eventsCaptor.getValue().get(0)).isEqualTo(eventOne);
        assertThat(eventsCaptor.getValue().get(1)).isEqualTo(eventTwo);
    }

    @Test
    public void eventsAreUpdatedAccordingToDatabase() throws Exception {
        dashboardPresenter.onAttach(dashboardView);

        EventViewModel eventOne = EventViewModel.create("event_uid_1", "Event 1", "2017-06-30", EventStatus.SCHEDULE);
        EventViewModel eventTwo = EventViewModel.create("event_uid_2", "Event 2", "1999-12-31", EventStatus.SKIPPED);

        eventsPublisher.onNext(Arrays.asList(eventOne, eventTwo));

        verify(dashboardView.renderEvents()).accept(eventsCaptor.capture());
        verify(dashboardRepository).events(enrollmentUid);
        assertThat(eventsCaptor.getValue().get(0)).isEqualTo(eventOne);
        assertThat(eventsCaptor.getValue().get(1)).isEqualTo(eventTwo);


        EventViewModel newEventOne =
                EventViewModel.create("new_event_uid_1", "New Event 1", "2099-06-30", EventStatus.SCHEDULE);
        EventViewModel newEventTwo =
                EventViewModel.create("new_event_uid_2", "New Event 2", "1997-11-31", EventStatus.SKIPPED);

        eventsPublisher.onNext(Arrays.asList(newEventOne, newEventTwo));

        verify(dashboardView.renderEvents(), times(2)).accept(eventsCaptor.capture());
        assertThat(eventsCaptor.getValue().get(0)).isEqualTo(newEventOne);
        assertThat(eventsCaptor.getValue().get(1)).isEqualTo(newEventTwo);
    }
}