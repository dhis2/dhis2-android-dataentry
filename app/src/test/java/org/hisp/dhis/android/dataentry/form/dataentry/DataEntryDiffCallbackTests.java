package org.hisp.dhis.android.dataentry.form.dataentry;

import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.text.TextViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataEntryDiffCallbackTests {

    @Test
    public void sizePropertiesShouldMatchToInjectedValues() {
        List<FieldViewModel> oldList = Arrays.asList(
                mock(FieldViewModel.class),
                mock(FieldViewModel.class));
        List<FieldViewModel> newList = Arrays.asList(
                mock(FieldViewModel.class),
                mock(FieldViewModel.class),
                mock(FieldViewModel.class));

        DataEntryDiffCallback dataEntryDiffCallback =
                new DataEntryDiffCallback(oldList, newList);

        assertThat(dataEntryDiffCallback.getOldListSize()).isEqualTo(2);
        assertThat(dataEntryDiffCallback.getNewListSize()).isEqualTo(3);
    }

    @Test
    public void areItemsTheSameShouldUseUidForComparison() {
        FieldViewModel modelOld = mock(FieldViewModel.class);
        FieldViewModel modelNew = mock(FieldViewModel.class);

        when(modelOld.uid()).thenReturn("model_one_uid");
        when(modelNew.uid()).thenReturn("model_one_uid");

        List<FieldViewModel> oldList = Arrays.asList(modelOld);
        List<FieldViewModel> newList = Arrays.asList(modelNew);

        DataEntryDiffCallback dataEntryDiffCallback =
                new DataEntryDiffCallback(oldList, newList);

        assertThat(dataEntryDiffCallback.getOldListSize()).isEqualTo(1);
        assertThat(dataEntryDiffCallback.getNewListSize()).isEqualTo(1);
        assertThat(dataEntryDiffCallback.areItemsTheSame(0, 0)).isTrue();

        verify(modelOld, times(1)).uid();
        verify(modelNew, times(1)).uid();
    }

    @Test
    public void areContentsTheSameShouldUseEqualsForComparison() {
        FieldViewModel modelOld = TextViewModel.create(
                "model_uid", "model_label", "model_value");
        FieldViewModel modelNew = TextViewModel.create(
                "model_uid", "model_label", "model_value");

        List<FieldViewModel> oldList = Arrays.asList(modelOld);
        List<FieldViewModel> newList = Arrays.asList(modelNew);

        DataEntryDiffCallback dataEntryDiffCallback =
                new DataEntryDiffCallback(oldList, newList);

        assertThat(dataEntryDiffCallback.getOldListSize()).isEqualTo(1);
        assertThat(dataEntryDiffCallback.getNewListSize()).isEqualTo(1);
        assertThat(dataEntryDiffCallback.areContentsTheSame(0, 0)).isTrue();
    }
}
