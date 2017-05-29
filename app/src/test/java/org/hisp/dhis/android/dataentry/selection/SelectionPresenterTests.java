package org.hisp.dhis.android.dataentry.selection;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(JUnit4.class)
public class SelectionPresenterTests {

    public static final String ARG_UID = "test_parent_uid";
    public static final String ARG_NAME = "test_parent_name";
    public static final String UID_1 = "test_uid_1";
    public static final String NAME_1 = "test_name_1";
    public static final String UID_2 = "test_uid_2";
    public static final String NAME_2 = "test_name_2";
    public static final String UID_3 = "test_uid_3";
    public static final String NAME_3 = "test_name_3";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SelectionView view;

    @Captor
    private ArgumentCaptor<List<SelectionViewModel>> captor;
    private PublishProcessor<List<SelectionViewModel>> publisher;

    @Mock
    private SelectionRepository repository;

    @Mock
    private SelectionArgument argument;

    private SelectionPresenter presenter;

    private List<SelectionViewModel> values;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        publisher = PublishProcessor.create();
        presenter = new SelectionPresenterImpl(argument, repository, new MockSchedulersProvider());

        values = new ArrayList<>(3);
        values.add(SelectionViewModel.create(UID_1, NAME_1));
        values.add(SelectionViewModel.create(UID_2, NAME_2));

        when(argument.uid()).thenReturn(ARG_UID);
        when(argument.name()).thenReturn(ARG_NAME);

        when(repository.list(ARG_UID)).thenReturn(publisher);
    }

    @Test
    public void onAttach() throws Exception {
        presenter.onAttach(view);
        publisher.onNext(values);

        assertThat(publisher.hasSubscribers()).isTrue();
        verify(view.update()).accept(captor.capture());
        verify(repository).list(ARG_UID);
        assertThat(captor.getValue()).isEqualTo(values);
    }

    @Test
    public void updateView() throws Exception {
        presenter.onAttach(view);
        publisher.onNext(values);

        assertThat(publisher.hasSubscribers()).isTrue();
        verify(view.update()).accept(captor.capture());
        verify(repository).list(ARG_UID);
        assertThat(captor.getValue()).isEqualTo(values);

        values.add( SelectionViewModel.create(UID_3, NAME_3));
        publisher.onNext(values);

        assertThat(publisher.hasSubscribers()).isTrue();
        verify(view.update(), times(2)).accept(captor.capture());
        verify(repository).list(ARG_UID);
        assertThat(captor.getValue()).isEqualTo(values);
    }

    @Test
    public void onDetach() {
        presenter.onAttach(view);
        presenter.onDetach();
        assertThat(publisher.hasSubscribers()).isFalse();
    }
}



