package org.hisp.dhis.android.dataentry.create;

import org.hisp.dhis.android.dataentry.commons.schedulers.MockSchedulersProvider;
import org.hisp.dhis.android.dataentry.create.CreateItemsArgument.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateItemsPresenterTests {

    public static final String ARG_NAME = "Name";
    public static final String ARG_UID = "arg_uid";
    public static final int FIRST_CARDVIEW = 0;
    public static final int SECOND_CARDVIEW = 1;

    @Mock
    CreateItemsArgument argument;

    @Mock
    CreateItemsRepository repository;

    @Mock
    CreateItemsView view;

    PublishSubject<Object> viewClick1Publisher;
    PublishSubject<Object> viewClick2Publisher;

    PublishSubject<Object> viewClear1Publisher;
    PublishSubject<Object> viewClear2Publisher;

    PublishSubject<Object> viewCreatePublisher;

    CreateItemsPresenter presenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        //Setup the argument:
        when(argument.name()).thenReturn(ARG_NAME);
        when(argument.uid()).thenReturn(ARG_UID);
        when(argument.type()).thenReturn(Type.TEI);

        //init the publishers:
        viewClick1Publisher = PublishSubject.create();
        viewClick2Publisher = PublishSubject.create();

        viewClear1Publisher = PublishSubject.create();
        viewClear2Publisher = PublishSubject.create();

        viewCreatePublisher = PublishSubject.create();

        //Setup the view publishers:
        when(view.cardViewClickEvent(FIRST_CARDVIEW)).thenReturn(viewClick1Publisher);
        when(view.cardViewClickEvent(SECOND_CARDVIEW)).thenReturn(viewClick2Publisher);

        when(view.cardViewClearEvent(FIRST_CARDVIEW)).thenReturn(viewClear1Publisher);
        when(view.cardViewClearEvent(SECOND_CARDVIEW)).thenReturn(viewClear2Publisher);

        when(view.createButtonEvent()).thenReturn(viewCreatePublisher);

        presenter = new CreateItemsPresenterImpl(argument, repository, new MockSchedulersProvider());
    }

    @Test
    public void enrollmentEventHints() {
        when(argument.type()).thenReturn(Type.ENROLMENT_EVENT);

        presenter.onAttach(view);

        verify(view, times(1)).setCardViewsHintsEnrollment();
    }

    @Test
    public void clearFirstCardView() {
        presenter.onAttach(view);

        viewClear1Publisher.onNext(new Object());

        verify(view, times(1)).setCardViewText(eq(FIRST_CARDVIEW), eq(""));
        verify(view, times(1)).setCardViewText(eq(SECOND_CARDVIEW), eq(""));
        verify(view, never()).createItem();
        verify(view, never()).setCardViewsHintsEnrollment();
        verify(view, never()).showDialog(anyInt());
        verify(view, never()).createItem();
    }

    @Test
    public void clearSecondCardView() {
        presenter.onAttach(view);

        viewClear2Publisher.onNext(new Object());

        verify(view, times(1)).setCardViewText(eq(SECOND_CARDVIEW), eq(""));
        verify(view, never()).setCardViewText(eq(FIRST_CARDVIEW), anyString());
        verify(view, never()).setCardViewsHintsEnrollment();
        verify(view, never()).showDialog(anyInt());
        verify(view, never()).createItem();
    }

    @Test
    public void clickFirstCardView() {
        presenter.onAttach(view);

        viewClick1Publisher.onNext(new Object());

        verify(view, times(1)).showDialog(eq(FIRST_CARDVIEW));
        verify(view, times(1)).showDialog(eq(FIRST_CARDVIEW));
        verify(view, never()).showDialog(eq(SECOND_CARDVIEW));
        verify(view, never()).setCardViewText(anyInt(), anyString());
        verify(view, never()).setCardViewsHintsEnrollment();
        verify(view, never()).createItem();
    }

    @Test
    public void clickSecondCardView() {
        presenter.onAttach(view);

        viewClick2Publisher.onNext(new Object());

        verify(view, times(1)).showDialog(eq(SECOND_CARDVIEW));
        verify(view, times(1)).showDialog(eq(SECOND_CARDVIEW));
        verify(view, never()).showDialog(eq(FIRST_CARDVIEW));
        verify(view, never()).setCardViewText(anyInt(), anyString());
        verify(view, never()).setCardViewsHintsEnrollment();
        verify(view, never()).createItem();
    }

    @Test
    public void clickCreate() {
        presenter.onAttach(view);

        viewCreatePublisher.onNext(new Object());

        verify(view, times(1)).createItem();
        verify(view, never()).showDialog(anyInt());
        verify(view, never()).showDialog(eq(FIRST_CARDVIEW));
        verify(view, never()).setCardViewText(anyInt(), anyString());
        verify(view, never()).setCardViewsHintsEnrollment();
    }

        /*  @Test
      public void onAttach() {
          presenter.onAttach(view);

          viewClick1Publisher.onNext(new Object());
  //        view.
  //        assertThat(view.)
      }*/

}
