package org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class CheckBoxViewModelTests {
    private static final String TEST_UID = "test_uid";
    private static final String TEST_LABEL = "test_label";

    @Test
    public void fromRawValueShouldMapTrueToValueEnumCorrectly() {
        CheckBoxViewModel viewModel = CheckBoxViewModel.fromRawValue(
                TEST_UID, TEST_LABEL, true, "true");

        assertThat(viewModel.uid()).isEqualTo(TEST_UID);
        assertThat(viewModel.label()).isEqualTo(TEST_LABEL);
        assertThat(viewModel.mandatory()).isTrue();
        assertThat(viewModel.value()).isEqualTo(CheckBoxViewModel.Value.CHECKED);
    }

    @Test
    public void fromRawValueShouldMapEmptyStringToValueEnumCorrectly() {
        CheckBoxViewModel viewModel = CheckBoxViewModel.fromRawValue(
                TEST_UID, TEST_LABEL, true, "");

        assertThat(viewModel.uid()).isEqualTo(TEST_UID);
        assertThat(viewModel.label()).isEqualTo(TEST_LABEL);
        assertThat(viewModel.mandatory()).isTrue();
        assertThat(viewModel.value()).isEqualTo(CheckBoxViewModel.Value.UNCHECKED);
    }

    @Test
    public void fromRawValueShouldThrowOnUnsupportedValue() {
        try {
            CheckBoxViewModel.fromRawValue(TEST_UID, TEST_LABEL, true, "some_value");
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }
}
