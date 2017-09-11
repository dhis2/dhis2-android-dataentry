package org.hisp.dhis.android.dataentry.dashboard.navigation;

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

public class NavigationPresenterUnitTests {

    private NavigationPresenter navigationPresenter;

    private String enrollmentUid = "enrollment_uid";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NavigationView navigationView;

    @Mock
    private NavigationRepository navigationRepository;

    @Captor
    private ArgumentCaptor<String> titleCaptor;

    @Captor
    private ArgumentCaptor<Pair<String, String>> attributesCaptor;

    @Captor
    private ArgumentCaptor<List<EventViewModel>> eventsCaptor;

    private PublishProcessor<String> titlePublisher;
    private PublishProcessor<List<String>> attributesPublisher;
    private PublishProcessor<List<EventViewModel>> eventsPublisher;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        titlePublisher = PublishProcessor.create();
        attributesPublisher = PublishProcessor.create();
        eventsPublisher = PublishProcessor.create();

        when(navigationRepository.title(enrollmentUid)).thenReturn(titlePublisher);
        when(navigationRepository.attributes(enrollmentUid)).thenReturn(attributesPublisher);
        when(navigationRepository.events(enrollmentUid)).thenReturn(eventsPublisher);

        navigationPresenter = new NavigationPresenterImpl(enrollmentUid, new MockSchedulersProvider(),
                navigationRepository);
    }

    @Test
    public void titleIsRenderedOnAttach() throws Exception {
        navigationPresenter.onAttach(navigationView);
        titlePublisher.onNext("Title");

        verify(navigationView.renderTitle()).accept(titleCaptor.capture());
        verify(navigationRepository).title(enrollmentUid);
        assertThat(titleCaptor.getValue()).isEqualTo("Title");
    }

    @Test
    public void titleIsUpdatedAccordingToDatabase() throws Exception {
        navigationPresenter.onAttach(navigationView);
        titlePublisher.onNext("Title");

        verify(navigationView.renderTitle()).accept(titleCaptor.capture());
        verify(navigationRepository).title(enrollmentUid);
        assertThat(titleCaptor.getValue()).isEqualTo("Title");

        titlePublisher.onNext("Second title");
        verify(navigationView.renderTitle(), times(2)).accept(titleCaptor.capture());
        assertThat(titleCaptor.getValue()).isEqualTo("Second title");
    }

    @Test
    public void attributesAreRenderedOnAttach() throws Exception {
        navigationPresenter.onAttach(navigationView);
        attributesPublisher.onNext(Arrays.asList("Attribute 1", "Attribute 2"));

        verify(navigationView.renderAttributes()).accept(attributesCaptor.capture());
        verify(navigationRepository).attributes(enrollmentUid);
        assertThat(attributesCaptor.getValue().val0()).isEqualTo("Attribute 1");
        assertThat(attributesCaptor.getValue().val1()).isEqualTo("Attribute 2");
    }

    @Test
    public void attributesAreUpdatedAccordingToDatabase() throws Exception {
        navigationPresenter.onAttach(navigationView);
        attributesPublisher.onNext(Arrays.asList("Attribute 1", "Attribute 2"));

        verify(navigationView.renderAttributes()).accept(attributesCaptor.capture());
        verify(navigationRepository).attributes(enrollmentUid);
        assertThat(attributesCaptor.getValue().val0()).isEqualTo("Attribute 1");
        assertThat(attributesCaptor.getValue().val1()).isEqualTo("Attribute 2");

        attributesPublisher.onNext(Arrays.asList("Second Attribute 1", "Second Attribute 2"));
        verify(navigationView.renderAttributes(), times(2)).accept(attributesCaptor.capture());
        assertThat(attributesCaptor.getValue().val0()).isEqualTo("Second Attribute 1");
        assertThat(attributesCaptor.getValue().val1()).isEqualTo("Second Attribute 2");
    }

    @Test
    public void eventsAreRenderedOnAttach() throws Exception {
        navigationPresenter.onAttach(navigationView);

        EventViewModel eventOne = EventViewModel.create("event_uid_1", "Event 1", "2017-06-30", EventStatus.SCHEDULE);
        EventViewModel eventTwo = EventViewModel.create("event_uid_2", "Event 2", "1999-12-31", EventStatus.SKIPPED);

        eventsPublisher.onNext(Arrays.asList(eventOne, eventTwo));

        verify(navigationView.renderEvents()).accept(eventsCaptor.capture());
        verify(navigationRepository).events(enrollmentUid);
        assertThat(eventsCaptor.getValue().get(0)).isEqualTo(eventOne);
        assertThat(eventsCaptor.getValue().get(1)).isEqualTo(eventTwo);
    }

    @Test
    public void eventsAreUpdatedAccordingToDatabase() throws Exception {
        navigationPresenter.onAttach(navigationView);

        EventViewModel eventOne = EventViewModel.create("event_uid_1", "Event 1", "2017-06-30", EventStatus.SCHEDULE);
        EventViewModel eventTwo = EventViewModel.create("event_uid_2", "Event 2", "1999-12-31", EventStatus.SKIPPED);

        eventsPublisher.onNext(Arrays.asList(eventOne, eventTwo));

        verify(navigationView.renderEvents()).accept(eventsCaptor.capture());
        verify(navigationRepository).events(enrollmentUid);
        assertThat(eventsCaptor.getValue().get(0)).isEqualTo(eventOne);
        assertThat(eventsCaptor.getValue().get(1)).isEqualTo(eventTwo);


        EventViewModel newEventOne =
                EventViewModel.create("new_event_uid_1", "New Event 1", "2099-06-30", EventStatus.SCHEDULE);
        EventViewModel newEventTwo =
                EventViewModel.create("new_event_uid_2", "New Event 2", "1997-11-31", EventStatus.SKIPPED);

        eventsPublisher.onNext(Arrays.asList(newEventOne, newEventTwo));

        verify(navigationView.renderEvents(), times(2)).accept(eventsCaptor.capture());
        assertThat(eventsCaptor.getValue().get(0)).isEqualTo(newEventOne);
        assertThat(eventsCaptor.getValue().get(1)).isEqualTo(newEventTwo);
    }
}