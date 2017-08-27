package org.hisp.dhis.android.dataentry.form.dataentry;

import org.hisp.dhis.android.dataentry.commons.schedulers.MockSchedulersProvider;
import org.hisp.dhis.android.dataentry.form.FormRepository;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.text.TextViewModel;
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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataEntryPresenterUnitTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    DataEntryStore dataEntryStore;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    DataEntryRepository dataEntryRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    FormRepository formRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    DataEntryView dataEntryView;

    @Captor
    ArgumentCaptor<List<FieldViewModel>> fieldsCaptor;

    @Captor
    ArgumentCaptor<RowAction> rowActionCaptor;

    PublishProcessor<List<FieldViewModel>> fields;
    PublishProcessor<RowAction> rowActions;
    DataEntryPresenter dataEntryPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        fields = PublishProcessor.create();
        rowActions = PublishProcessor.create();

        when(dataEntryRepository.list()).thenReturn(fields);
        when(dataEntryView.rowActions()).thenReturn(rowActions);

        dataEntryPresenter = new DataEntryPresenterImpl(dataEntryStore,
                dataEntryRepository, formRepository, new MockSchedulersProvider());
    }

    @Test
    public void onAttachShouldSubscribeToRepositoryAndView() {
        assertThat(fields.hasSubscribers()).isFalse();
        assertThat(rowActions.hasSubscribers()).isFalse();

        dataEntryPresenter.onAttach(dataEntryView);

        assertThat(fields.hasSubscribers()).isTrue();
        assertThat(rowActions.hasSubscribers()).isTrue();
    }

    @Test
    public void onAttachShouldPropagateUpdatesFromRepositoryToView() throws Exception {
        dataEntryPresenter.onAttach(dataEntryView);

        List<FieldViewModel> fieldViewModels = Arrays.asList(
                TextViewModel.create("test_uid_one", "test_label_one", "test_value_one"),
                TextViewModel.create("test_uid_two", "test_label_two", "test_value_two"));

        // first event
        fields.onNext(fieldViewModels);

        verify(dataEntryView, times(1)).showFields();
        verify(dataEntryView.showFields()).accept(fieldsCaptor.capture());
        assertThat(fieldsCaptor.getValue()).isEqualTo(fieldViewModels);


        List<FieldViewModel> fieldViewModelsTwo = Arrays.asList(
                TextViewModel.create("test_uid_three", "test_label_three", "test_value_three"));

        // second event
        fields.onNext(fieldViewModelsTwo);

        verify(dataEntryView, times(2)).showFields();
        verify(dataEntryView.showFields(), times(2)).accept(fieldsCaptor.capture());
        assertThat(fieldsCaptor.getValue()).isEqualTo(fieldViewModelsTwo);
    }

    @Test
    public void onAttachShouldPropagateUpdatesFromViewToRepository() {
        dataEntryPresenter.onAttach(dataEntryView);

        rowActions.onNext(RowAction.create("test_uid", "test_value"));
        verify(dataEntryStore, times(1)).save("test_uid", "test_value");

        rowActions.onNext(RowAction.create("test_uid_two", "test_value_two"));
        verify(dataEntryStore, times(1)).save("test_uid_two", "test_value_two");
    }

    @Test
    public void onDetachShouldUnsubscribeFromRepositoryAndView() {
        assertThat(fields.hasSubscribers()).isFalse();
        assertThat(rowActions.hasSubscribers()).isFalse();

        dataEntryPresenter.onAttach(dataEntryView);
        assertThat(fields.hasSubscribers()).isTrue();
        assertThat(rowActions.hasSubscribers()).isTrue();

        dataEntryPresenter.onDetach();
        assertThat(fields.hasSubscribers()).isFalse();
        assertThat(rowActions.hasSubscribers()).isFalse();
    }
}
