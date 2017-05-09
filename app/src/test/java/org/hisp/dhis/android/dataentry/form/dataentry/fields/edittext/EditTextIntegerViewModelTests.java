package org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext;

import android.text.InputType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class EditTextIntegerViewModelTests {

    @Test
    public void createShouldPropagateArgumentsCorrectly() {
        EditTextIntegerViewModel viewModel = EditTextIntegerViewModel.create("test_uid",
                "test_label", true, 5, "test_hint", InputType.TYPE_CLASS_NUMBER);

        assertThat(viewModel.uid()).isEqualTo("test_uid");
        assertThat(viewModel.label()).isEqualTo("test_label");
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.hint()).isEqualTo("test_hint");
        assertThat(viewModel.maxLines()).isEqualTo(1);
        assertThat(viewModel.value()).isEqualTo(5);
        assertThat(viewModel.inputType()).isEqualTo(InputType.TYPE_CLASS_NUMBER);
    }
}
