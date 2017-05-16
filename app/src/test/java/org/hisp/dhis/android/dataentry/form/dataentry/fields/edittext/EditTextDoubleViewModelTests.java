package org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext;

import android.text.InputType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class EditTextDoubleViewModelTests {

    @Test
    public void createShouldPropagateArgumentsCorrectly() {
        EditTextDoubleViewModel viewModel = EditTextDoubleViewModel.create("test_uid",
                "test_label", true, 5.0, "test_hint");

        assertThat(viewModel.uid()).isEqualTo("test_uid");
        assertThat(viewModel.label()).isEqualTo("test_label");
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.hint()).isEqualTo("test_hint");
        assertThat(viewModel.maxLines()).isEqualTo(1);
        assertThat(viewModel.value()).isEqualTo(5.0);
        assertThat(viewModel.inputType()).isEqualTo(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    @Test
    public void fromShouldFallbackToNullValueIfEmptyString() {
        EditTextDoubleViewModel viewModel = EditTextDoubleViewModel.from(
                "test_uid", "test_label", true, "", "test_hint");

        assertThat(viewModel.uid()).isEqualTo("test_uid");
        assertThat(viewModel.label()).isEqualTo("test_label");
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.hint()).isEqualTo("test_hint");
        assertThat(viewModel.maxLines()).isEqualTo(1);
        assertThat(viewModel.value()).isNull();
        assertThat(viewModel.inputType()).isEqualTo(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
    }
}
